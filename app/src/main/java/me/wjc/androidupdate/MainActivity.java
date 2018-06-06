package me.wjc.androidupdate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import me.wjc.androidupdate.update.AppUpdateHelper;
import me.wjc.androidupdate.utils.PreferenceUtils;

public class MainActivity extends AppCompatActivity {
    private static final int GET_UNKNOWN_APP_SOURCES = 10022;
    private static final int INSTALL_PACKAGES_REQUESTCODE = 10012;
    private AppUpdateHelper mAppUpdateHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppUpdateHelper = new AppUpdateHelper(this);
        mAppUpdateHelper.startUpdateVersion();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            mAppUpdateHelper.setPermission(new AppUpdateHelper.OnAndroidORequestPermission() {
                @Override
                public void request() {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUESTCODE);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == INSTALL_PACKAGES_REQUESTCODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mAppUpdateHelper != null){
                    mAppUpdateHelper.installApk(this, PreferenceUtils.getPrefString(AppUpdateHelper.UPDATE_APK_PATH,""));
                }
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_UNKNOWN_APP_SOURCES){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                if (!haveInstallPermission) {
                    Toast.makeText(MyApplication.getContext(),"请允许安装未知来源的应用程序，否则无法安装更新",Toast.LENGTH_LONG);
                }else {
                    if (mAppUpdateHelper != null){
                        mAppUpdateHelper.installApk(this,PreferenceUtils.getPrefString(AppUpdateHelper.UPDATE_APK_PATH,""));
                    }
                }
            }
        }
    }
}
