package com.audit.pass.app;

public class JniLibrary {

    static {
        System.loadLibrary("igle");
    }

    public static native int add(int x, int y);

    public static native int remove(int x, int y);

    public static native String getData(String input);

}
