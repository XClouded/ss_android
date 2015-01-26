/*
 *  Loudness analyze library
 *  implementing EBU R.128  
 *
 *  Example program with simple wave/pcm library
 *
 *  Copyright (C) 2011 Staale Helleberg, Radio Nova, Oslo, Norway
 *  Released under  GNU GENERAL PUBLIC LICENSE (GPL) Version 3
 *
 */

#ifndef	__ANDROID_LOG_H__
#define	__ANDROID_LOG_H__

#include <android/log.h>

#define	logI(...)	__android_log_print(ANDROID_LOG_INFO, "libnav", __VA_ARGS__)
#define	logE(...)	__android_log_print(ANDROID_LOG_ERROR, "libnav", __VA_ARGS__)


#endif /* __ANDROID_LOG_H__ */

#include <jni.h>
#include <stdlib.h>
#include <stdio.h>

#include <r128/ebu_r128.h>

typedef enum { false, true } bool;
static s_ebu_r128 config;
static short * pcmShorts;
static bool isArrayInit;

jint Java_com_myandb_singsong_audio_AutoGainController_create(
        JNIEnv* env,
        jobject this,
        jint channels,
        jint resolution,
        jint sample_rate,
        jint mode
        )
{
	unsigned char ebu_mode = EBU_MODE_INTEGRATED;

	switch (mode) {

	case 1:
		ebu_mode = EBU_MODE_INTEGRATED;
		break;

	case 2:
		ebu_mode=EBU_MODE_MOMENTARY;
		break;

	case 3:
		ebu_mode=EBU_MODE_SHORT_TERM;
		break;
	}

	if (!ebu_r128_init(&config, channels, resolution, sample_rate, ebu_mode )) {
		ebu_r128_destroy(&config);

		return 0;
	}

	isArrayInit = false;
}

jfloat Java_com_myandb_singsong_audio_AutoGainController_process(
        JNIEnv* env,
        jobject this,
        jfloatArray pcm,
        jint offset,
        jint length
        )
{
    jfloat * pcmFloats 			= (*env)->GetFloatArrayElements(env, pcm, NULL);

    if (isArrayInit == false) {
    	pcmShorts = malloc(length * sizeof(short));
		memset(pcmShorts, 0, length * sizeof(short));

		isArrayInit = true;
    }

    unsigned int i;
	for (i = 0; i < length; i++) {
		pcmShorts[i] = (short) (32767. * pcmFloats[offset + i]);
	}

	(*env)->ReleaseFloatArrayElements(env, pcm, pcmFloats, 0);

    int r = ebu_r128_process_samples(&config, pcmShorts, length);

    if (!r) {
    	ebu_r128_destroy(&config);
		return -1;
    }

    if (r == 2) {
		float lk = config.lk;

		if (config.mode == EBU_MODE_INTEGRATED) {
			// In file mode, we're usually only in to the final Lk value, so skip this calculation to save time
		    // lk=ebu_r128_get_integrated_lufs(&config);
		    // fprintf(stdout, "Current Lk=%.2f LUFS,  %.2f LU\n", lk,  lk + 23);
		} else {

		}

		return lk;
    }

    return 0;

}

void Java_com_myandb_singsong_audio_AutoGainController_destroy(
        JNIEnv* env,
        jobject this
        )
{
	ebu_r128_destroy(&config);

	free(pcmShorts);
}


