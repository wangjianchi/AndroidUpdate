package me.wjc.androidupdate.utils;

import android.content.Context;
import android.content.SharedPreferences;

import me.wjc.androidupdate.MyApplication;

/**
 * @author:: wangjianchi
 * @time: 2018/6/6  15:40.
 * @description:
 */
public class PreferenceUtils {
    private static final String PREFERENCE_FILE_NAME = "file";
    public static String getPrefString( String key, final String defaultValue) {
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,defaultValue);
    }
    public static void setPrefString(final String key, final String value) {
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(PREFERENCE_FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
