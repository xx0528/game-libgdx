#include <jni.h>
#include "libadd.h"

JNIEXPORT jint JNICALL
Java_com_audit_pass_app_JniLibrary_add(JNIEnv *env, jclass clazz, jint x, jint y) {
    return add(x, y);
}

JNIEXPORT jint JNICALL
Java_com_audit_pass_app_JniLibrary_remove(JNIEnv *env, jclass clazz, jint x, jint y) {
    return remove_int(x, y);
}
