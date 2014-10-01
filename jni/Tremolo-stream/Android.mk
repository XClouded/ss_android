LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := vorbisidec-stream
LOCAL_CFLAGS += -I$(LOCAL_PATH)/../include -fsigned-char
LOCAL_CFLAGS += -I$(LOCAL_PATH)/../Tremolo -fsigned-char
LOCAL_CFLAGS += -march=armv6 -marm -mfloat-abi=softfp -mfpu=vfp

LOCAL_SHARED_LIBRARIES := libvorbisidec

LOCAL_SRC_FILES := \
	vorbisidec-fileinputstream.c \
	jni-util.c

include $(BUILD_SHARED_LIBRARY)
