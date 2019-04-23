package com.fansp.myapplication;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;//上下文
    private static Thread mMainThread;//主线程
    @Override
    public void onCreate() {
        super.onCreate();
        if (mContext == null) {
            mContext = getApplicationContext();
        }
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        MyApplication.mContext = mContext;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

    public static void setMainThread(Thread mMainThread) {
        MyApplication.mMainThread = mMainThread;
    }
}
