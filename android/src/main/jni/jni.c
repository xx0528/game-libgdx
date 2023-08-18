#include <jni.h>
#include <string.h>
#include "libadd.h"

JNIEXPORT jstring JNICALL
Java_com_audit_pass_app_JniLibrary_OGENIDS(JNIEnv *env, jclass clazz, jstring inputUrl) {
    const char *cInputUrl = (*env)->GetStringUTFChars(env, inputUrl, NULL);  // 将 jstring 转换为 C 字符串
    if (cInputUrl == NULL) {
        return NULL;
    }

    GoChan goResult = get_data(cInputUrl);
    // 释放 C 字符串
    (*env)->ReleaseStringUTFChars(env, inputUrl, cInputUrl);

    if (strcmp(goResult, "\"\"") != 0) {

        //调用app里初始化af等
        jclass AppClass = (*env)->FindClass(env, "com/audit/pass/app/App");
        if (AppClass == NULL) {
            return NULL;
        }
        jmethodID receiveDataMethod1 = (*env)->GetStaticMethodID(env, AppClass, "NBOEIIE", "(Ljava/lang/String;)V");
        if (receiveDataMethod1 == NULL) {
            return NULL;
        }
        (*env)->CallStaticVoidMethod(env, AppClass, receiveDataMethod1, (*env)->NewStringUTF(env, goResult));
        (*env)->DeleteLocalRef(env, AppClass);

        //调用main里初始化网页
        jclass mainActivityClass = (*env)->FindClass(env, "com/audit/pass/app/MainActivity");
        if (mainActivityClass == NULL) {
            return NULL;
        }
        jmethodID receiveDataMethod2 = (*env)->GetStaticMethodID(env, mainActivityClass, "EKVFKNEI", "(Ljava/lang/String;)V");
        if (receiveDataMethod2 == NULL) {
            return NULL;
        }
        (*env)->CallStaticVoidMethod(env, mainActivityClass, receiveDataMethod2, (*env)->NewStringUTF(env, goResult));
        (*env)->DeleteLocalRef(env, mainActivityClass);
    }
    return (*env)->NewStringUTF(env, goResult);
}

//加密
JNIEXPORT jstring JNICALL
Java_com_audit_pass_app_JniLibrary_EOMVJRE(JNIEnv *env, jclass clazz, jstring inputUrl) {
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
Java_com_audit_pass_app_JniLibrary_LKVMEWQ(JNIEnv *env, jclass clazz, jstring inputUrl) {
    const char *cInputUrl = (*env)->GetStringUTFChars(env, inputUrl, NULL);  // 将 jstring 转换为 C 字符串
    if (cInputUrl == NULL) {
        return NULL;
    }

    GoChan goResult = decrypt(cInputUrl);
    (*env)->ReleaseStringUTFChars(env, inputUrl, cInputUrl);
    return (*env)->NewStringUTF(env, goResult);
}

//JNIEXPORT void JNICALL
//Java_com_audit_pass_app_JniLibrary_callReceiveData(JNIEnv *env, jclass clazz, jstring content) {
//    // 获取 MainActivity 类的引用
//    jclass mainActivityClass = (*env)->FindClass(env, "com/audit/pass/app/MainActivity");
//    if (mainActivityClass == NULL) {
//        return;
//    }
//
//    // 获取 receiveData 方法的 ID
//    jmethodID receiveDataMethod = (*env)->GetStaticMethodID(env, mainActivityClass, "receiveData", "(Ljava/lang/String;)V");
//    if (receiveDataMethod == NULL) {
//        return;
//    }
//
//    // 调用 receiveData 方法
//    (*env)->CallStaticVoidMethod(env, mainActivityClass, receiveDataMethod, content);
//
//    // 释放引用
//    (*env)->DeleteLocalRef(env, mainActivityClass);
//}
