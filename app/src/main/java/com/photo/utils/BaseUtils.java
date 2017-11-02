package com.photo.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.photo.AppConfig;
import com.photo.model.BaseCallback;

import java.lang.ref.WeakReference;


public abstract class BaseUtils {
    protected static final String TAG = "BaseUtils";
    protected static final boolean DEBUG = AppConfig.DEBUG;

    protected static final int LOAD_FINISH = 0x1;
    private BaseCallback mCallback;
    protected MyHandler mHandler = new MyHandler(this);
    protected Context mContext;

    public BaseUtils(Context context) {
        this.mContext = context;
    }

    private void dealMessage(Message message) {
        int what = message.what;
        if (what == LOAD_FINISH) {
            if (mCallback != null) {
                mCallback.onResult(message.obj);
            }
        }
    }

    @CallSuper
    public void getDataList(@Nullable final String path, @NonNull BaseCallback callback) {
        mCallback = callback;
        new Thread(){
            @Override
            public void run() {
                doInBackground(path);
            }
        }.start();
    }

    protected abstract void doInBackground(String path);

    class MyHandler extends Handler {
        private WeakReference<BaseUtils> mReference;

        public MyHandler(BaseUtils utils) {
            mReference = new WeakReference<>(utils);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mReference != null && mReference.get() != null) {
                mReference.get().dealMessage(msg);
            }
        }
    }
}
