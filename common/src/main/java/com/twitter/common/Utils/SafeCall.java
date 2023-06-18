package com.twitter.common.Utils;


public class SafeCall {
    public static Boolean DEBUG = true;

    public interface SafeRunnable {
        void run() throws Exception;
    }

    public static boolean safe(SafeRunnable runnable) {
        try {
            runnable.run();
            return true;
        } catch (Exception e) {
            reportException(e);
            return false;
        }
    }

    public interface TypeSafeRunnable<T> {
        T run() throws Exception;
    }

    public static <T> T safe(TypeSafeRunnable<T> runnable, T defaultValue) {
        try {
            return runnable.run();
        } catch (Exception e) {
            reportException(e);
        }
        return defaultValue;
    }

    private static void reportException(Exception e) {
        if (DEBUG) {
            e.printStackTrace();
        }
    }
}
