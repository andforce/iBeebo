#include <jni.h>

extern "C" {
/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getAppKey
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getAppKey
  (JNIEnv *, jobject);

/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getAppSecret
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getAppSecret
  (JNIEnv *, jobject);

/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getRedirectUrl
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getRedirectUrl
  (JNIEnv *, jobject);

}
