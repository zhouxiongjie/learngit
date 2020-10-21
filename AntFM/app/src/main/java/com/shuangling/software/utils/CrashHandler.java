package com.shuangling.software.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import io.sentry.Sentry;

public class CrashHandler implements Thread.UncaughtExceptionHandler {


    private static int sIsDebugMode = -1;

    public static boolean isDebugMode(Context context) {
        if (sIsDebugMode == -1) {
            boolean isDebug = context.getApplicationInfo() != null
                    && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            sIsDebugMode = isDebug ? 1 : 0;
        }
        return sIsDebugMode == 1;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        Sentry.capture(e);


        try {
            //给Toast留出时间
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
