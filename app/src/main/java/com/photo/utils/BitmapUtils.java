package com.photo.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Administrator on 2017/11/19.
 */

public class BitmapUtils {
    /**
     * 图片效果叠加   画布方式（这个比上边的快）
     * @param bottomBitmap
     * @param topBitmap
     * @param bottomAlpha
     * @param topAlpha
     * @param wh
     * @return
     */
    public static Bitmap overlay2Bitmap(Bitmap bottomBitmap, Bitmap topBitmap, int bottomAlpha, int topAlpha, int wh) {
        Rect srcRect = new Rect(0, 0, wh, wh);
        Bitmap resultBitmap = Bitmap.createBitmap(wh, wh, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);

        Paint paint1 = new Paint();
        paint1.setColor(Color.TRANSPARENT);
        paint1.setAlpha(bottomAlpha);
        canvas.drawBitmap(bottomBitmap, 0, 0, paint1);

        paint1.setAlpha(topAlpha);
        canvas.drawBitmap(topBitmap, 0, 0, paint1);

        return resultBitmap;
    }
}
