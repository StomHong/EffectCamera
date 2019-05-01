package com.gpufast.recorder.audio;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;

import java.io.IOException;

public class HwAudioEncoder {

    private static final String audioMime = "audio/mp4a-latm";

    private MediaCodec mCodec;
    private AudioRecord mAudioRecorder;
    private int minBufferSize;
    private long prevOutputPTSUs;


    public void prepare() {
//        initCodec();
//        initRecorder();
    }


//
//    private void initRecorder() {
//
//
//    }


//    private boolean initCodec() {
//        try {
//            MediaFormat format = MediaFormat.createAudioFormat(audioMime, mSampleRate, 1);
//            format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
//            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
//            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, mSampleRate);
//            mCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
//            mCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//            mCodec.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//
//
//    }
}