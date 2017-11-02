package com.photo.utils;

import android.content.Context;
import android.os.Message;

import com.photo.bean.PhotoBean;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by Admin on 2017/11/2.
 */

public class PhotoUtils extends BaseUtils {

    public PhotoUtils(Context context) {
        super(context);
    }

    @Override
    protected void doInBackground(String path) {
        ArrayList<PhotoBean> photoBeans = new ArrayList<>();
        File parentFile = new File(path);
        if (parentFile != null && parentFile.exists()) {
            File[] files = parentFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".jpeg") || name.endsWith(".jpg") || name.endsWith(".png")) {
                        return true;
                    }
                    return false;
                }
            });

            for (File file : files) {
                PhotoBean photoBean = new PhotoBean();
                photoBean.setFileName(file.getName());
                photoBean.setFilePath(file.getAbsolutePath());
                photoBeans.add(photoBean);
            }

            Message message = mHandler.obtainMessage(LOAD_FINISH);
            message.obj = photoBeans;
            mHandler.sendMessage(message);
        }
    }

}
