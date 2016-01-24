package com.hf.heavyprockiller;

/**
 * Created by Fan on 2016/1/24.
 *
 * Log
 */
public class Log {
    public static final String TAG = "HeavyProcKiller";

    @SuppressWarnings("unused")
    private static String makeMsg(String msg) {
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();

        if (stack.length >= 5) {
            return stack[4].getClassName() + "." + stack[4].getMethodName() + "()# " + msg;
        }
        return  "<Invalid Stack>";
    }

    @SuppressWarnings("unused")
    public static void i(String msg) {
        android.util.Log.i(TAG, makeMsg(msg));
    }

    @SuppressWarnings("unused")
    public static void i(String msg, Exception e) {
        android.util.Log.i(TAG, makeMsg(msg), e);
    }

    @SuppressWarnings("unused")
    public static void w(String msg) {
        android.util.Log.i(TAG, makeMsg(msg));
    }

    @SuppressWarnings("unused")
    public static void w(String msg, Exception e) {
        android.util.Log.i(TAG, makeMsg(msg), e);
    }

    @SuppressWarnings("unused")
    public static void e(String msg) {
        android.util.Log.i(TAG, makeMsg(msg));
    }

    @SuppressWarnings("unused")
    public static void e(String msg, Exception e) {
        android.util.Log.i(TAG, makeMsg(msg), e);
    }

    @SuppressWarnings("unused")
    public static void v(String msg) {
        android.util.Log.i(TAG, makeMsg(msg));
    }

    @SuppressWarnings("unused")
    public static void v(String msg, Exception e) {
        android.util.Log.i(TAG, makeMsg(msg), e);
    }

    @SuppressWarnings("unused")
    public static void d(String msg) {
        android.util.Log.i(TAG, makeMsg(msg));
    }

    @SuppressWarnings("unused")
    public static void d(String msg, Exception e) {
        android.util.Log.i(TAG, makeMsg(msg), e);
    }
}
