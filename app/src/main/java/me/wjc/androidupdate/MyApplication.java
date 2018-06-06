package me.wjc.androidupdate;

import android.app.Application;
import android.content.Context;

/**
 * @author:: wangjianchi
 * @time: 2018/6/6  15:16.
 * @description:
 */
public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
