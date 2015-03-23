LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := BeeboAuthUtils

LOCAL_C_INCLUDES := $(LOCAL_PATH)

LOCAL_SRC_FILES  := BeeboAuthUtils.cc

LOCAL_PRELINK_MODULE := false 
include $(BUILD_SHARED_LIBRARY)
