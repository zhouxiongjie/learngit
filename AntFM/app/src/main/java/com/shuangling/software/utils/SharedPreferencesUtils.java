package com.shuangling.software.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shuangling.software.MyApplication;
import com.shuangling.software.entity.User;


public class SharedPreferencesUtils {

    public static final String FILE_NAME = "AntFM";

    private static String USER = "user";


    public enum PreferenceType {
        Boolean("布尔型"), String("字符串型"), Int("整型"), Float("浮点型"), Long("长整型");

        private final java.lang.String svalue;

        public java.lang.String getValue() {
            return svalue;
        }

        PreferenceType(java.lang.String value) {
            this.svalue = value;
        }
    }


    public static boolean saveUser(User user) {

        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER, JSON.toJSONString(user));
        return editor.commit();
    }




    public static User getUser() {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String str=sp.getString(USER, "");
        if(!TextUtils.isEmpty(str)){
            return JSON.parseObject(str,User.class);
        }else {
            return null;
        }

    }



    public static boolean resetUser() {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(USER);
        return editor.commit();
    }


    public static int getIntValue(String key,int defValue){
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static boolean getBooleanValue(String key,boolean defValue){
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    public static boolean putIntValue(String key,int value){
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key,value);
        return editor.commit();
    }



    public static boolean putPreferenceTypeValue(String key, PreferenceType pType, String value){
        try{
            SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            switch(pType){
                case Boolean :
                    editor.putBoolean(key, Boolean.parseBoolean(value));
                    break;
                case Int:
                    editor.putInt(key, Integer.parseInt(value));
                    break;
                case Float:
                    editor.putFloat(key, Float.parseFloat(value));
                    break;
                case Long:
                    editor.putLong(key, Long.parseLong(value));
                    break;
                case String:
                    editor.putString(key, value);
                    break;
                default:
                    break;
            }
            editor.commit();
            return true;
        }catch(Exception ex){
            return false;
        }
    }






}
