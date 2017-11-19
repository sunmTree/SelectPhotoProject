package com.photo.encode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.text.TextUtils;
import android.util.Log;

import com.photo.AppConfig;
import com.photo.select.R;
import com.photo.utils.BitmapUtils;
import com.photo.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/19.
 */

public class MediaUtils {
    private static final String TAG = "MediaUtils";
    private static final boolean DEBUG = AppConfig.DEBUG;

    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video
    private static final int FRAME_RATE = 10; // 10FPS
    private static final int IFRAME_INTERVAL = 0; // seconds between I-frames

    private int mWidth = -1;
    private int mHeight = -1;
    private int mBitRate = -1;
    // largest color component delta seen
    private int mLargestColorDelta;

    private File outputFile;
    private MediaCodec mEncode;
    private MediaMuxer mMuxer;
    private int mTrackIndex;
    private boolean mMuxerStarted;
    private ArrayList<File> mFrames;
    private Context mContext;
    private String mPathImage;

    public MediaUtils(File outputFile, ArrayList<File> frames, Context context) {
        this.outputFile = outputFile;
        this.mFrames = frames;
        this.mContext = context;
    }

    public boolean startEncodeAndDecoder(int width, int height, int bitRate, String imagePath) {
        this.mPathImage = imagePath;
        setParameters(width, height, bitRate);
        return encodeDecoderVideoFromBuffer();
    }

    private boolean encodeDecoderVideoFromBuffer() {
        mLargestColorDelta = -1;

        MediaCodecInfo codecInfo = selectCodec(MIME_TYPE);
        if (codecInfo == null)
            throw new RuntimeException("Can't find enable codec");
        if (DEBUG)
            Log.i(TAG, "earn mediaCodecInfo from current phone " + codecInfo);
        int colorFormat = selectColorFormat(codecInfo, MIME_TYPE);
        if (colorFormat == -1) {
            colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
        }
        if (DEBUG)
            Log.i(TAG, "earn colorFormat " + colorFormat);

        MediaFormat videoFormat = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);

        try {
            mEncode = MediaCodec.createEncoderByType(MIME_TYPE);
            mEncode.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mEncode.start();

            String outputPath = outputFile.getAbsolutePath();
            mMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            return doEncodeDecodeVideoFromBuffer(mEncode, colorFormat);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mEncode != null) {
                mEncode.stop();
                mEncode.release();
            }
            if (mMuxer != null) {
                mMuxer.stop();
                mMuxer.release();
            }
        }

        return false;
    }

    private boolean doEncodeDecodeVideoFromBuffer(MediaCodec encoder, int colorFormat) {
        final long TIMEOUT_USEC = System.currentTimeMillis();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        boolean inputDone = false;
        int generateIndex = 0;
        int pictureIndex = 0;
        int total = mFrames.size() * FRAME_RATE * 3 - 2;
        while (!inputDone && total >= generateIndex) {
            int inputBufferIndex = encoder.dequeueInputBuffer(TIMEOUT_USEC);
            if (inputBufferIndex >= 0) {
                long ptsUsec = computePresentationTime(generateIndex);
                if (total <= generateIndex) {
                    encoder.queueInputBuffer(inputBufferIndex, 0, 0, ptsUsec,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    inputDone = true;
                    drainEncoder(true, bufferInfo);
                } else {
                    pictureIndex = generateIndex / (FRAME_RATE * 3);
                    encodeImage(pictureIndex, generateIndex, inputBufferIndex, encoder, ptsUsec, bufferInfo);
                }
                generateIndex++;
            } else {
                if (DEBUG)
                    Log.d(TAG, "input buffer not available");
            }
        }
        return false;
    }

    private void encodeImage(int pictureIndex, int generateIndex, int inputBufferIndex,
                             MediaCodec encoder, long ptsUsec, MediaCodec.BufferInfo bufferInfo) {
        byte[] frameData;
        Bitmap bitmap = null;
        if (pictureIndex >= mFrames.size()) {
            pictureIndex = 0;
        }
        Bitmap currentBitmap = FileUtils.getAssetFile(mFrames.get(pictureIndex).getAbsolutePath());
        Bitmap nextBitmap;
        if (pictureIndex + 1 >= mFrames.size()) {
            nextBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher_round);
        } else {
            nextBitmap = FileUtils.getAssetFile(mFrames.get(pictureIndex + 1).getAbsolutePath());
        }

        float nowAlpha;
        float nextAlpha;
        int index = generateIndex % (FRAME_RATE * 3);
        if (index < (FRAME_RATE * 2)) {
            nextAlpha = 0;
        } else {
            nextAlpha = (float) ((index - (FRAME_RATE * 2)) / (FRAME_RATE * 1.0));
        }
        nowAlpha = 1 - nextAlpha;

        bitmap = BitmapUtils.overlay2Bitmap(currentBitmap, nextBitmap,
                (int) (nowAlpha * 255), (int) (nextAlpha * 255), 720);
        frameData = EncodeUtils.getYUV420sp(mWidth, mHeight, bitmap);
        byte[] yuv420sp = new byte[mWidth * mHeight * 3 / 2];
        EncodeUtils.NV21ToNV12(frameData, yuv420sp, mWidth, mHeight);
        frameData = yuv420sp;

        ByteBuffer[] inputBuffers = encoder.getInputBuffers();
        ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
        inputBuffer.clear();
        inputBuffer.put(frameData);
        encoder.queueInputBuffer(inputBufferIndex, 0, frameData.length, ptsUsec, 0);
        drainEncoder(false, bufferInfo);
    }

    private void drainEncoder(boolean endOfFrame, MediaCodec.BufferInfo bufferInfo) {
        final long TIMEOUT_USEC = 10000;
        if (endOfFrame) {
            mEncode.signalEndOfInputStream();
        }

        ByteBuffer[] outputBuffers = mEncode.getOutputBuffers();
        while (true) {
            int encoderStatus = mEncode.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfFrame) {
                    break;
                } else {
                    if (DEBUG)
                        Log.i(TAG, "no output available, spinning to await EOS");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBuffers = mEncode.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mMuxerStarted)
                    throw new RuntimeException("format change twice");
                MediaFormat newFormat = mEncode.getOutputFormat();
                if (DEBUG)
                    Log.d(TAG, "encode output format changed: " + newFormat);
                mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();
                mMuxerStarted = true;
            } else if (encoderStatus < 0) {
                if (DEBUG)
                    Log.e(TAG, "unexpected result from encode.dequeueOutputBuffer: " + encoderStatus);
            } else {
                ByteBuffer encoderData = outputBuffers[encoderStatus];
                if (encoderData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                }
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    if (DEBUG)
                        Log.d(TAG, "ignore BUFFER_CODEC_CONFIG");
                    bufferInfo.size = 0;
                }
                if (bufferInfo.size != 0) {
                    if (!mMuxerStarted)
                        throw new RuntimeException("muxer hasn't started");
                    encoderData.position(bufferInfo.offset);
                    encoderData.limit(bufferInfo.offset + bufferInfo.size);
                    if (DEBUG)
                        Log.d(TAG, "BufferInfo " + bufferInfo.offset + " , " + bufferInfo.presentationTimeUs);

                    mMuxer.writeSampleData(mTrackIndex, encoderData, bufferInfo);

                    mEncode.releaseOutputBuffer(encoderStatus, false);
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        if (!endOfFrame) {
                            if (DEBUG)
                                Log.i(TAG, "reached end of stream unexpectedly");
                        } else {
                            if (DEBUG)
                                Log.i(TAG, "end of stream reached");
                        }
                        break;
                    }
                }

            }
        }
    }

    private long computePresentationTime(int generateIndex) {
        return 132 + generateIndex * 1000000 / FRAME_RATE;
    }

    private int selectColorFormat(MediaCodecInfo codecInfo, String mimeType) {
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            int colorFormat = capabilities.colorFormats[i];
            if (isRecognizedFormat(colorFormat)) {
                return colorFormat;
            }
        }
        return -1;
    }

    private boolean isRecognizedFormat(int colorFormat) {

        switch (colorFormat) {
            // these are the formats we know how to handle for
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                if (DEBUG) {
                    Log.d(TAG, "isRecognizedFormat " + colorFormat);
                }
                return true;
            default:
                return false;
        }
    }

    // 选择当前设备支持的codec
    private MediaCodecInfo selectCodec(String mimeType) {
        int codecCount = MediaCodecList.getCodecCount();
        for (int i = 0; i < codecCount; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (TextUtils.equals(type, mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    /**
     * h264 文件的最小单元是16x16的，如果width height 不是16的整数倍，在有的CPU上会花屏
     *
     * @param width
     * @param height
     * @param bitRate
     */
    private void setParameters(int width, int height, int bitRate) {
        if (width % 16 != 0 || height % 16 != 0)
            throw new RuntimeException("width and height must multi of 16");
        mWidth = width;
        mHeight = height;
        mBitRate = bitRate;
    }
}
