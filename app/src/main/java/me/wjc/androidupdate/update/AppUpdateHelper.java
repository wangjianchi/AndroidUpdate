package me.wjc.androidupdate.update;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

import me.wjc.androidupdate.utils.PreferenceUtils;
import me.wjc.androidupdate.view.UpdateDialog;
import me.wjc.androidupdate.view.UpdateProgressDialog;

/**
 * @author:: wangjianchi
 * @time: 2018/6/5  13:57.
 * @description:
 */
public class AppUpdateHelper implements ServiceConnection, AppUpdateCallback {
    public static final String UPDATE_APK_PATH = "update_apk_path";
    private Context mContext;
    private AppUpdateService mService;
    private UpdateProgressDialog mUpdateProgressDialog;
    private UpdateBean mUpdateBean;
    private OnAndroidORequestPermission mPermission;

    public AppUpdateHelper(Context context) {
        this.mContext = context;
    }

    public void startUpdateVersion() {
        mContext.bindService(new Intent(mContext, AppUpdateService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mService = ((AppUpdateService.LocalBinder) iBinder).getService();
        mService.setAppUpdateCallback(this);
        mService.checkUpdateTask();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (mService != null) {
            mService.setAppUpdateCallback(null);
        }
        mService = null;
        mContext = null;
    }

    @Override
    public void needUpdate(final UpdateBean updateBean) {
        this.mUpdateBean = updateBean;
        Message msg = new Message();
        msg.what = 4;
        mHandler.sendMessage(msg);
    }

    @Override
    public void noUpdate() {
        unBindService();
    }

    @Override
    public void onDownloadSuccess() {
        Message msg = new Message();
        msg.what = 3;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onDownloading(int progress) {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = progress;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onDownloadFailed() {
        Message msg = new Message();
        msg.what = 2;
        msg.obj = "万选通下载失败";
        mHandler.sendMessage(msg);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mUpdateProgressDialog == null){
                        mUpdateProgressDialog = new UpdateProgressDialog(mContext);
                        mUpdateProgressDialog.show();
                    }
                    mUpdateProgressDialog.setProgress((int)msg.obj);
                    break;
                case 2:

                    break;
                case 3:
                    String downloadpath = PreferenceUtils.getPrefString(UPDATE_APK_PATH, "");
                    installApk(mContext, downloadpath);
                    break;
                case 4:
                    UpdateDialog updateDialog = new UpdateDialog(mContext);
                    updateDialog.show();
                    updateDialog.initView(mUpdateBean, new UpdateDialog.OnUpdateClickListener() {
                        @Override
                        public void update() {
                            String historyPath = PreferenceUtils.getPrefString(UPDATE_APK_PATH, "");
                            if (compare(getApkInfo(mContext, historyPath), mContext)) {
                                installApk(mContext, historyPath);
                            } else {
                                mService.doDownloadTask(mUpdateBean.getUrl());
                            }
                        }
                    });
                    break;
            }
        }
    };

    /**
     * 安装apk
     */
    public void installApk(Context context, String downloadFileUrl) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean haveInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {
                if (mPermission != null){
                    mPermission.request();
                }
                return;
            }
        }
        if (!TextUtils.isEmpty(downloadFileUrl)) {
            Uri downloadFileUri;
            Intent install = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= 24) {
                if (downloadFileUrl.startsWith("file://")) {
                    downloadFileUrl = downloadFileUrl.replaceFirst("file://", "");
                }
                File file = new File(downloadFileUrl);
                downloadFileUri = FileProvider.getUriForFile(context, "com.me.wjc.fileProvider", file);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                downloadFileUri = Uri.parse(downloadFileUrl);
            }
            if (downloadFileUri != null) {
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            } else {
                Toast.makeText(context, "安装失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "安装失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取apk程序信息[packageName,versionName...]
     *
     * @param context Context
     * @param path    apk path
     */
    private static PackageInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info;
        }
        return null;
    }


    /**
     * 下载的apk和当前程序版本比较
     *
     * @param apkInfo apk file's packageInfo
     * @param context Context
     * @return 如果当前应用版本小于apk的版本则返回true
     */
    private static boolean compare(PackageInfo apkInfo, Context context) {
        if (apkInfo == null) {
            return false;
        }
        String localPackage = context.getPackageName();
        if (apkInfo.packageName.equals(localPackage)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void unBindService() {
        if (mService != null) {
            mContext.unbindService(this);
        }
    }

    public interface OnAndroidORequestPermission{
        void request();
    }

    public void setPermission(OnAndroidORequestPermission permission) {
        mPermission = permission;
    }
}
