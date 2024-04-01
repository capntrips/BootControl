#define LOG_TAG "libbootcontrol"

#include <android/log.h>

#include <cmath>
#include <string>

#include <jni.h>

#define ALOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define ALOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define ALOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// https://cs.android.com/android/platform/superproject/+/android-14.0.0_r28:system/extras/bootctl/bootctl.cpp;l=20
#include <BootControlClient.h>
#include <android/hardware/boot/1.2/IBootControl.h>

using android::hal::BootControlClient;
using android::hal::BootControlVersion;
using android::hal::CommandResult;

class BootCtl {
public:
    // https://cs.android.com/android/platform/superproject/+/android-14.0.0_r28:system/extras/bootctl/bootctl.cpp;l=229
    std::unique_ptr<BootControlClient> client;
    BootControlVersion bootVersion = BootControlVersion::BOOTCTL_V1_0;

    BootCtl() {
        client = android::hal::BootControlClient::WaitForService();
        if (client == nullptr) {
            ALOGE("Failed to get bootctl module.");
            return;
        }
        bootVersion = client->GetVersion();
    }
};

jobject commandResult(JNIEnv *env, bool b, std::string s) {
   auto cls = env->FindClass("com/github/capntrips/bootcontrol/CommandResult");
   auto constructor = env->GetMethodID(cls, "<init>", "()V");
   auto success = env->GetFieldID(cls , "success", "Z");
   auto errMsg = env->GetFieldID(cls , "errMsg", "Ljava/lang/String;");
   auto ret = env->NewObject(cls, constructor);
   env->SetBooleanField(ret, success, b);
   jstring js = env->NewStringUTF(s.c_str());
   env->SetObjectField(ret, errMsg, js);
   return ret;
}

jobject boolResult(JNIEnv *env, bool b) {
    auto cls = env->FindClass("com/github/capntrips/bootcontrol/BoolResult");
    auto constructor = env->GetMethodID(cls, "<init>", "()V");
    auto value = env->GetFieldID(cls , "value", "Z");
    auto ret = env->NewObject(cls, constructor);
    env->SetBooleanField(ret, value, b);
    return ret;
}

jobject handleReturn(JNIEnv *env, const std::optional<bool>& ret) {
    if (!ret.has_value()) {
        return nullptr;
    }
    return boolResult(env, ret.value());
}

// https://cs.android.com/android/platform/superproject/+/android-14.0.0_r28:system/extras/bootctl/bootctl.cpp;l=64
static constexpr auto ToString(BootControlVersion ver) {
    switch (ver) {
        case BootControlVersion::BOOTCTL_V1_0:
            return "android.hardware.boot@1.0::IBootControl";
        case BootControlVersion::BOOTCTL_V1_1:
            return "android.hardware.boot@1.1::IBootControl";
        case BootControlVersion::BOOTCTL_V1_2:
            return "android.hardware.boot@1.2::IBootControl";
        case BootControlVersion::BOOTCTL_AIDL:
            return "android.hardware.boot@aidl::IBootControl";
    }
}

static constexpr auto ToInt(BootControlVersion ver) {
    switch (ver) {
        case BootControlVersion::BOOTCTL_V1_0:
            return 0;
        case BootControlVersion::BOOTCTL_V1_1:
            return 1;
        case BootControlVersion::BOOTCTL_V1_2:
            return 2;
        case BootControlVersion::BOOTCTL_AIDL:
            return 3;
    }
}


extern "C" {

// https://cs.android.com/android/platform/superproject/+/android-14.0.0_r28:system/extras/bootctl/bootctl.cpp;l=77
JNIEXPORT jstring JNICALL
Java_com_github_capntrips_bootcontrol_BootControlService_00024BootControlIPC_halInfo(
        JNIEnv *env,
        jobject /* this */) {
    BootCtl bootctl;
    if (bootctl.client == nullptr) {
        return nullptr;
    }
    std::string info = ToString(bootctl.client->GetVersion());
    return env->NewStringUTF(info.c_str());
}

JNIEXPORT jint JNICALL
Java_com_github_capntrips_bootcontrol_BootControlService_00024BootControlIPC_halVersion(
        JNIEnv * /* env */,
        jobject /* this */) {
    BootCtl bootctl;
    return ToInt(bootctl.client->GetVersion());
}

// https://cs.android.com/android/platform/superproject/+/android-14.0.0_r28:system/extras/bootctl/bootctl.cpp;l=82
JNIEXPORT jint JNICALL
Java_com_github_capntrips_bootcontrol_BootControlService_00024BootControlIPC_getNumberSlots(
        JNIEnv * /* env */,
        jobject /* this */) {
    BootCtl bootctl;
    return bootctl.client->GetNumSlots();
}

// https://cs.android.com/android/platform/superproject/+/android-14.0.0_r28:system/extras/bootctl/bootctl.cpp;l=88
JNIEXPORT jint JNICALL
Java_com_github_capntrips_bootcontrol_BootControlService_00024BootControlIPC_getCurrentSlot(
        JNIEnv * /* env */,
        jobject /* this */) {
    BootCtl bootctl;
    return bootctl.client->GetCurrentSlot();
}

// https://cs.android.com/android/platform/superproject/+/android-14.0.0_r28:system/extras/bootctl/bootctl.cpp;l=110
JNIEXPORT jint JNICALL
Java_com_github_capntrips_bootcontrol_BootControlService_00024BootControlIPC_getActiveBootSlot(
        JNIEnv * /* env */,
        jobject /* this */) {
    BootCtl bootctl;
    return bootctl.client->GetActiveBootSlot();
}

// https://cs.android.com/android/platform/superproject/+/android-14.0.0_r28:system/extras/bootctl/bootctl.cpp;l=116
JNIEXPORT jobject JNICALL
Java_com_github_capntrips_bootcontrol_BootControlService_00024BootControlIPC_setActiveBootSlot(
        JNIEnv *env,
        jobject /* this */,
        jint slot_number) {
    BootCtl bootctl;
    const auto cr = bootctl.client->SetActiveBootSlot(slot_number);
    return commandResult(env, cr.success, cr.errMsg);
}

// https://cs.android.com/android/platform/superproject/+/android-14.0.0_r28:system/extras/bootctl/bootctl.cpp;l=139
JNIEXPORT jobject JNICALL
Java_com_github_capntrips_bootcontrol_BootControlService_00024BootControlIPC_isSlotBootable(
        JNIEnv *env,
        jobject /* this */,
        jint slot_number) {
    BootCtl bootctl;
    return handleReturn(env, bootctl.client->IsSlotBootable(slot_number));
}

// https://cs.android.com/android/platform/superproject/+/android-14.0.0_r28:system/extras/bootctl/bootctl.cpp;l=144
JNIEXPORT jobject JNICALL
Java_com_github_capntrips_bootcontrol_BootControlService_00024BootControlIPC_isSlotMarkedSuccessful(
        JNIEnv *env,
        jobject /* this */,
        jint slot_number) {
    BootCtl bootctl;
    return handleReturn(env, bootctl.client->IsSlotMarkedSuccessful(slot_number));
}

// https://cs.android.com/android/platform/superproject/+/android-14.0.0_r28:system/extras/bootctl/bootctl.cpp;l=202
JNIEXPORT jstring JNICALL
Java_com_github_capntrips_bootcontrol_BootControlService_00024BootControlIPC_getSuffix(
        JNIEnv *env,
        jobject /* this */,
        jint slot_number) {
    BootCtl bootctl;
    const auto ret = bootctl.client->GetSuffix(slot_number);
    if (ret.empty()) {
        return nullptr;
    }
    return env->NewStringUTF(ret.c_str());
}

} // extern "C"
