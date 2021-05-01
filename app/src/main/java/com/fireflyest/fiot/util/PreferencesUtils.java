package com.fireflyest.fiot.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.UnicodeSetIterator;

import java.util.Set;

public class PreferencesUtils {

    private static SharedPreferences preferences;

    private PreferencesUtils(){
    }

    public static void initSharedPreferences(Context context){
        preferences = context.getSharedPreferences("", Context.MODE_PRIVATE);
    }

    public static long getLongData(String key){
        if(preferences == null) return 0;
        return preferences.getLong(key, 0);
    }

    public static int getIntData(String key){
        if(preferences == null) return 0;
        return preferences.getInt(key, 0);
    }

    public static String getStringData(String key){
        if(preferences == null) return "";
        return preferences.getString(key, "");
    }

    public static void putData(String key, int data){
        if(preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, data);
        editor.apply();
    }

    public static void putData(String key, String data){
        if(preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public static void putData(String key, Boolean data){
        if(preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, data);
        editor.apply();
    }

    public static void putData(String key, float data){
        if(preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, data);
        editor.apply();
    }

    public static void putData(String key, Set<String> data){
        if(preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(key, data);
        editor.apply();
    }

    public static void putData(String key, long data){
        if(preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, data);
        editor.apply();
    }

}
