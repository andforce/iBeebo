#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <stdio.h>
#include "org_zarroboogs_weibo_auth_BeeboAuthUtils.h"

/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getAppKey
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getAppKey(
		JNIEnv * env, jclass clss) {
	jstring str = env->NewStringUTF("3101880425");
	return str;
}

/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getAppSecret
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getAppSecret(
		JNIEnv * env, jclass clss) {
	jstring str = env->NewStringUTF("160b420662fce58e0065fadd7c114e09");
	return str;
}

/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getRedirectUrl
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getRedirectUrl(
		JNIEnv * env, jclass clss) {
	jstring str = env->NewStringUTF("http://weibo.com/andforce");
	return str;
}

//==================================HACK======================================//

/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getHackAppKey
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getHackAppKey(
		JNIEnv * env, jclass clss) {
	jstring str = env->NewStringUTF("211160679");
	return str;
}

/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getHackAppSecret
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getHackAppSecret(
		JNIEnv * env, jclass clss) {
	jstring str = env->NewStringUTF("63b64d531b98c2dbff2443816f274dd3");
	return str;
}

/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getHackRedirectUrl
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getHackRedirectUrl(
		JNIEnv * env, jclass clss) {
	jstring str = env->NewStringUTF("http://oauth.weico.cc");
	return str;
}

/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getHackPackageName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getHackPackageName(
		JNIEnv * env, jclass clss) {
	jstring str = env->NewStringUTF("com.eico.weico");
	return str;
}

/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getHackKeyHash
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getHackKeyHash(
		JNIEnv * env, jclass clss) {
	jstring str = env->NewStringUTF("1e6e33db08f9192306c4afa0a61ad56c");
	return str;
}
