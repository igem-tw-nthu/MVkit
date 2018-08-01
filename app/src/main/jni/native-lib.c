#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_example_samsung_sustainableaquaculture_MainActivity_stringFromJNI(JNIEnv *env,
                                                                           jobject instance) {

    // TODO


    const char *returnValue;
    return (*env)->NewStringUTF(env, returnValue);
}