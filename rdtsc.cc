#include "biochemie_util_CPUCycleTimer.h"
#include <jni.h>
JNIEXPORT jlong JNICALL Java_biochemie_util_CPUCycleTimer_rdtsc
  (JNIEnv *, jobject) {
  asm("rdtsc");
}
