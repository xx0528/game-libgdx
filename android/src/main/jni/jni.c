#include <jni.h>
#include <string.h>
#include "libadd.h"

JNIEXPORT jint JNICALL
Java_com_audit_pass_app_JniLibrary_add(JNIEnv *env, jclass clazz, jint x, jint y) {
    return add(x, y);
}

JNIEXPORT jint JNICALL
Java_com_audit_pass_app_JniLibrary_remove(JNIEnv *env, jclass clazz, jint x, jint y) {
    return remove_int(x, y);
}

JNIEXPORT jstring JNICALL
Java_com_audit_pass_app_JniLibrary_getData(JNIEnv *env, jclass clazz, jstring inputUrl) {
    const char *cInputUrl = (*env)->GetStringUTFChars(env, inputUrl, NULL);  // 将 jstring 转换为 C 字符串
    if (cInputUrl == NULL) {
        return NULL;
    }

    jclass mainActivityClass = (*env)->FindClass(env, "com/audit/pass/app/MainActivity");
    if (mainActivityClass == NULL) {
        return NULL;
    }

    // 获取 receiveData 方法的 ID
    jmethodID receiveDataMethod = (*env)->GetStaticMethodID(env, mainActivityClass, "receiveData", "(Ljava/lang/String;)V");
    if (receiveDataMethod == NULL) {
        return NULL;
    }

    // 调用 receiveData 方法
    (*env)->CallStaticVoidMethod(env, mainActivityClass, receiveDataMethod, (*env)->NewStringUTF(env, "传入内容-----"));

    // 释放引用
    (*env)->DeleteLocalRef(env, mainActivityClass);


    GoChan goResult = get_data(cInputUrl);
    // 释放 C 字符串
    (*env)->ReleaseStringUTFChars(env, inputUrl, cInputUrl);
    return (*env)->NewStringUTF(env, goResult);
}

//加密
JNIEXPORT jstring JNICALL
Java_com_audit_pass_app_JniLibrary_encrypt(JNIEnv *env, jclass clazz, jstring inputUrl) {
    const char *cInputUrl = (*env)->GetStringUTFChars(env, inputUrl, NULL);  // 将 jstring 转换为 C 字符串
    if (cInputUrl == NULL) {
        return NULL;
    }

    GoChan goResult = encrypt(cInputUrl);
    (*env)->ReleaseStringUTFChars(env, inputUrl, cInputUrl);
    return (*env)->NewStringUTF(env, goResult);
}

//解密
JNIEXPORT jstring JNICALL
Java_com_audit_pass_app_JniLibrary_decrypt(JNIEnv *env, jclass clazz, jstring inputUrl) {
    const char *cInputUrl = (*env)->GetStringUTFChars(env, inputUrl, NULL);  // 将 jstring 转换为 C 字符串
    if (cInputUrl == NULL) {
        return NULL;
    }

    GoChan goResult = decrypt(cInputUrl);
    (*env)->ReleaseStringUTFChars(env, inputUrl, cInputUrl);
    return (*env)->NewStringUTF(env, goResult);
}

JNIEXPORT void JNICALL
Java_com_audit_pass_app_JniLibrary_callReceiveData(JNIEnv *env, jclass clazz, jstring content) {
    // 获取 MainActivity 类的引用
    jclass mainActivityClass = (*env)->FindClass(env, "com/audit/pass/app/MainActivity");
    if (mainActivityClass == NULL) {
        return;
    }

    // 获取 receiveData 方法的 ID
    jmethodID receiveDataMethod = (*env)->GetStaticMethodID(env, mainActivityClass, "receiveData", "(Ljava/lang/String;)V");
    if (receiveDataMethod == NULL) {
        return;
    }

    // 调用 receiveData 方法
    (*env)->CallStaticVoidMethod(env, mainActivityClass, receiveDataMethod, content);

    // 释放引用
    (*env)->DeleteLocalRef(env, mainActivityClass);
}
