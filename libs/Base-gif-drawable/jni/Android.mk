LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := gif
LOCAL_SRC_FILES := giflib/dgif_lib.c giflib/gif_lib_private.h giflib/gif_lib.h \
				giflib/gifalloc.c gif.c gif.h
LOCAL_LDLIBS    := -lm -llog -ljnigraphics

include $(BUILD_SHARED_LIBRARY)
