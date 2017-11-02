package com.photo.async;


import android.os.Handler;
import android.os.Message;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * provider two method one do in background one in UI Thread
 * @author sunTree
 * @version 1.0
 */

public abstract class BackTask<Params, Result> {
    private static final String TAG = "BackTask";
    private static final int FINISH_LOAD_TASK = 0x1;

    private InnerHandler mHandler = new InnerHandler(this);
    private Params p;

    public void execute(Params params) {
        p = params;
        new Thread(){
            @Override
            public void run() {
                Result result = doInBackground(p);
                Log.d(TAG, "doInBackground");
                Message message = mHandler.obtainMessage(FINISH_LOAD_TASK);
                message.obj = result;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    @WorkerThread
    public abstract Result doInBackground(Params params);

    @WorkerThread
    public abstract void onPostResult(Result result);

    class InnerHandler extends Handler {
        private WeakReference<BackTask> mReference;

        public InnerHandler(BackTask task) {
            mReference = new WeakReference<>(task);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mReference != null && mReference.get() != null) {
                if (msg.what == FINISH_LOAD_TASK) {
                    Log.d(TAG, "load finish");
                    mReference.get().onPostResult(msg.obj);
                }
            }
        }
    }

}
