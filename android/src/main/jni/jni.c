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
Java_com_audit_pass_app_JniLibrary_getData(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF(get_data());
}
