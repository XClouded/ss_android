LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := r128-stream
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_CFLAGS += -I$(LOCAL_PATH)/../include -fsigned-char
LOCAL_CFLAGS += -march=armv6 -marm -mfloat-abi=softfp -mfpu=vfp

LOCAL_SRC_FILES := \
	ebu_r128.c \
	itu-1770-filter.c \
	r128-fileinputstream.c \

include $(BUILD_SHARED_LIBRARY)
