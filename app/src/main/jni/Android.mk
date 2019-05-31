LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := CustomJNI
LOCAL_SRC_FILES := CustomJNI.cpp
include $(BUILD_SHARED_LIBRARY)