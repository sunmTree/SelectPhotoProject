package com.photo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Administrator on 2017/11/19.
 */

public class FileUtils {
    public static Bitmap getAssetFile(String path) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(path);
            if (bitmap == null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
//                每隔2行采1行, 每隔2列采一列, 那你解析出的图片就是原图大小的1 / 4.
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeFile(path, options);
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
