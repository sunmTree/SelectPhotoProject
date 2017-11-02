package com.photo.model;

import android.content.Context;
import android.util.Log;

import com.photo.AppConfig;
import com.photo.bean.AlbumBean;
import com.photo.utils.AlbumUtils;

import java.util.List;

/**
 * Created by Admin on 2017/11/2.
 */

public class PickPhotoPresenter {
    private static final String TAG = "PickPhotoPresenter";
    private static final boolean DEBUG = AppConfig.DEBUG;

    private AlbumUtils mAlbumUtils;
    private IUpdateView mUpdateView;

    public PickPhotoPresenter(Context context, IUpdateView updateView) {
        this.mUpdateView = updateView;
        mAlbumUtils = new AlbumUtils(context);
    }

    public void getAllAlbum() {
        if (mUpdateView != null) {
            if (DEBUG) {
                Log.d(TAG, "presenter start load all album");
            }
            mUpdateView.showDialog();
            mAlbumUtils.getDataList(null, new BaseCallback<List<AlbumBean>>() {
                @Override
                public void onResult(List<AlbumBean> albumBeans) {
                    mUpdateView.updateView(albumBeans);
                }
            });

        }
    }
}
