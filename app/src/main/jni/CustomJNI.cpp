//
// Created by Aleksa on 31-May-19.
//

#include "CustomJNI.h"

extern "C" JNIEXPORT jdouble JNICALL Java_project_weatherforecast_CustomJNI_convert_1temperature
  (JNIEnv *, jclass type, jdouble x, jint unit) {
    {
        //0 JE KONVERZIJA IZ FARENHAJT U CELZIJUS
        if(unit == 0){

            return (x - 32) * 5.0 / 9;
        }
        //1 JE KONVERZIJA IZ CELZIJUSA U FARENHAJT
        return (x * 9) / 5.0 + 32;
    }
}