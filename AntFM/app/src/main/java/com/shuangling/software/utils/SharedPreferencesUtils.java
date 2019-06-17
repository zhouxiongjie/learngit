package com.shuangling.software.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.shuangling.software.MyApplication;


public class SharedPreferencesUtils {

    public static final String FILE_NAME = "AntFM";

    private static String USER_NAME = "username";

    private static String PASS_WORD = "password";



    public static boolean saveUser(String username, String password) {

        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER_NAME, username);
        editor.putString(PASS_WORD, password);
        return editor.commit();
    }




    public static String getUserName() {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(USER_NAME, "");
    }

    public static String getPassWord() {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(PASS_WORD, "");
    }






    public static boolean resetUser() {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        return editor.commit();
    }




}
