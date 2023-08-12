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

    GoChan goResult = get_data(cInputUrl);  // 假设 get_data 是 Go 中的函数
    // 释放 C 字符串
    (*env)->ReleaseStringUTFChars(env, inputUrl, cInputUrl);
    // 将 Go 字符串转换为 jstring 返回
    return (*env)->NewStringUTF(env, goResult);
}
