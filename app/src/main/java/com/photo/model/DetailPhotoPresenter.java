package com.photo.model;

import android.content.Context;

import com.photo.AppConfig;
import com.photo.bean.PhotoBean;
import com.photo.utils.BaseUtils;
import com.photo.utils.PhotoUtils;

import java.util.List;


/**
 * Created by Admin on 2017/11/2.
 */

public class DetailPhotoPresenter {
    private static final String TAG = "DetailPhotoPresenter";
    private static final boolean DEBUG = AppConfig.DEBUG;

    private IUpdateView mUpdateView;
    private BaseUtils mPhotoUtils;

    public DetailPhotoPresenter(Context context, IUpdateView updateView) {
        this.mUpdateView = updateView;
        mPhotoUtils = new PhotoUtils(context);
    }

    public void getPhotoDetail(String parentFilePath) {
        if (mUpdateView != null) {
            mUpdateView.showDialog();
            mPhotoUtils.getDataList(parentFilePath, new BaseCallback<List<PhotoBean>>() {
                @Override
                public void onResult(List<PhotoBean> photoBeans) {
                    mUpdateView.updateView(photoBeans);
                }
            });
        }
    }

}
