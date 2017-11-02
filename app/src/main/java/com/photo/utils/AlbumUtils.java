package com.photo.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.photo.bean.AlbumBean;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by Admin on 2017/11/2.
 */

public class AlbumUtils extends BaseUtils {

    public AlbumUtils(Context context) {
        super(context);
    }

    @Override
    protected void doInBackground(String imagePath) {
        ArrayList<AlbumBean> albumBeans = new ArrayList<>();
        String selection = MediaStore.Images.Media.MIME_TYPE + "= ? or "
                + MediaStore.Images.Media.MIME_TYPE + " = ?";
        String[] selectionArgs = {"image/jpeg", "image/png"};
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, selection, selectionArgs, MediaStore.Images.Media.DATE_MODIFIED);
        ArrayList<String> dirList = new ArrayList<>();
        if (DEBUG) {
            Log.d(TAG, "get cursor " + cursor);
        }
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            File parentFile = new File(path).getParentFile();
            if (parentFile == null || !parentFile.exists()) {
                continue;
            }
            // get parent path
            String filePath = parentFile.getAbsolutePath();
            if (dirList.contains(filePath)) {
                continue;
            }

            dirList.add(filePath);
            AlbumBean albumBean = new AlbumBean();
            albumBean.setAlbumName(parentFile.getName());
            albumBean.setAlbumPath(filePath);
            albumBean.setFirstImgPath(path);
            String[] list = parentFile.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".jpeg") || name.endsWith(".jpg") || name.endsWith(".png")) {
                        return true;
                    }
                    return false;
                }
            });
            albumBean.setAlbumPhotoCount(list.length);
            if (DEBUG) {
                Log.d(TAG, "in while add album " + albumBean.toString());
            }
            albumBeans.add(albumBean);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (DEBUG) {
            Log.d(TAG, "finish while and return list");
        }

        Message message = mHandler.obtainMessage(LOAD_FINISH);
        message.obj = albumBeans;
        mHandler.sendMessage(message);
    }

}
