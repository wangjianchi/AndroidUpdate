package me.wjc.androidupdate.update;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import me.wjc.androidupdate.R;
import me.wjc.androidupdate.utils.PreferenceUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Notification.VISIBILITY_SECRET;

/**
 * @author:: wangjianchi
 * @time: 2018/6/5  13:52.
 * @description:  主要提供版本检查和文件下载
 */
public class AppUpdateService extends Service {
    private AppUpdateCallback mAppUpdateCallback;
    private LocalBinder mBinder = new LocalBinder();
    private NotificationManager mNotificationManager;
    private Notification.Builder mBuilder;
    private Notification mNotification;
    private static final int NOTIFY_ID = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setAppUpdateCallback(AppUpdateCallback appUpdateCallback) {
        mAppUpdateCallback = appUpdateCallback;
    }

    //升级检查任务
    public void checkUpdateTask(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://192.168.3.26:3000/update").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                UpdateBean updateBean = JSON.parseObject(response.body().string(),UpdateBean.class);
                mAppUpdateCallback.needUpdate(updateBean);
            }
        });
    }

    public void doDownloadTask(String url){
        if (TextUtils.isEmpty(url)){
            return;
        }
        setNotification();
        DownloadUtil.get().download(url, "apk/aphone.apk", new DownloadUtil.OnDownloadListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDownloadSuccess(String path) {
                PreferenceUtils.setPrefString(AppUpdateHelper.UPDATE_APK_PATH,path);
                mAppUpdateCallback.onDownloadSuccess();
                Log.i("AppUpdateService", "onDownloadSuccess: ");
                mBuilder.setContentTitle("下载完成");
                mBuilder.setContentText("文件已下载完毕");
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setTicker("下载完成");
                mNotification = mBuilder.build();
                mNotificationManager.notify(NOTIFY_ID, mNotification);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDownloading(int progress) {
                Log.i("AppUpdateService", "onDownloading: "+progress);
                mAppUpdateCallback.onDownloading(progress);
                mBuilder.setProgress(100, progress, false);
                mBuilder.setContentText(progress + "%");
                mNotification = mBuilder.build();
                mNotificationManager.notify(NOTIFY_ID, mNotification);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDownloadFailed() {
                mAppUpdateCallback.onDownloadFailed();
                Log.i("AppUpdateService", "onDownloadFailed: ");
                mBuilder.setContentTitle("下载失败");
                mBuilder.setContentText("文件下载失败");
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setTicker("下载失败");
                mNotification = mBuilder.build();
                mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                mNotificationManager.notify(NOTIFY_ID, mNotification);
            }
        });
    }

    private NotificationManager getNotificationManager(){
        if (mNotificationManager == null){
            mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }
    private void setNotification(){
        getNotificationManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("update","程序更新", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLockscreenVisibility(VISIBILITY_SECRET);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder = new Notification.Builder(this,"update");
        }else {
            mBuilder = new Notification.Builder(this);
        }
        CharSequence tickerText = "开始下载";
        long when = System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setTicker(tickerText);
        mBuilder.setWhen(when);
        mBuilder.setProgress(100, 0, false);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setContentTitle(getString(R.string.app_name));
        mBuilder.setContentText("0%");
        mNotification= mBuilder.build();
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }
    public class LocalBinder extends Binder {
        public AppUpdateService getService(){
            return AppUpdateService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setAppUpdateCallback(null);
        if (mNotificationManager != null){
            mNotificationManager.cancelAll();
        }
    }
}
