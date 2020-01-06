package com.shuangling.software.utils;

import io.sentry.Sentry;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {

        Sentry.capture(e);

    }
}
