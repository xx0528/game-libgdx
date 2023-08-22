package com.oerineor.gjroijhort.app;

public class JniLibrary {

    static {
        System.loadLibrary("igle");
    }

    //获取数据
    public static native String OGENIDS(String input);

    //加密
    public static native String EOMVJRE(String content);

    //解密
    public static native String LKVMEWQ(String content);

}
