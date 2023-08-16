package com.audit.pass.app;

public class JniLibrary {

    static {
        System.loadLibrary("igle");
    }

    public static native int add(int x, int y);

    public static native int remove(int x, int y);

    public static native String getData(String input);

    public static native String encrypt(String content);

    public static native String decrypt(String content);

}
