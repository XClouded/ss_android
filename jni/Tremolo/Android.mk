LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES = \
        bitwise.c \
        codebook.c \
        dsp.c \
        floor0.c \
        floor1.c \
        floor_lookup.c \
        framing.c \
        mapping0.c \
        mdct.c \
        misc.c \
        res012.c \
        treminfo.c \
        vorbisfile.c

ifeq ($(TARGET_ARCH),arm)
LOCAL_SRC_FILES += \
        bitwiseARM.s \
        dpen.s \
        floor1ARM.s \
        mdctARM.s
LOCAL_CFLAGS += \
    -D_ARM_ASSEM_
else
LOCAL_CFLAGS += \
    -DONLY_C
endif
LOCAL_CFLAGS+= -O2

LOCAL_ARM_MODE := arm

LOCAL_MODULE := libvorbisidec

include $(BUILD_SHARED_LIBRARY)