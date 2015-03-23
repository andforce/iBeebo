#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <stdio.h>
#include "org_zarroboogs_weibo_auth_BeeboAuthUtils.h"
jstring stoJstring(JNIEnv* env, const char* pat) {
	//定义java String类 strClass
	jclass strClass = (env)->FindClass("Ljava/lang/String;");
	//获取java String类方法String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
	jmethodID ctorID = (env)->GetMethodID(strClass, "<init>",
			"([BLjava/lang/String;)V");
	jbyteArray bytes = (env)->NewByteArray(strlen(pat)); //建立byte数组
	(env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*) pat); //将char* 转换为byte数组
	jstring encoding = (env)->NewStringUTF("GB2312"); // 设置String, 保存语言类型,用于byte数组转换至String时的参数
	return (jstring)(env)->NewObject(strClass, ctorID, bytes, encoding); //将byte数组转换为java String,并输出
}


/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getAppKey
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getAppKey(
		JNIEnv * env, jobject job){
	jstring str = env->NewStringUTF("HelloJNI");
	return str;
}

/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getAppSecret
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getAppSecret(
		JNIEnv * env, jobject job){
	return NULL;
}

/*
 * Class:     org_zarroboogs_weibo_auth_BeeboAuthUtils
 * Method:    getRedirectUrl
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zarroboogs_weibo_auth_BeeboAuthUtils_getRedirectUrl(
		JNIEnv * env, jobject job){
	return NULL;
}

