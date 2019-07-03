package com.gpufast.recorder.audio;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;

import java.io.IOException;

public class HwAudioEncoder implements AudioEncoder{

    private static final String audioMime = "audio/mp4a-latm";

    private MediaCodec mAudioCodec;
    private AudioRecord mAudioRecorder;
    private int minBufferSize;
    private long prevOutputPTSUs;
    private int mSampleRate = 44100;
    private int mBitRate = 64 * 1024;
    private boolean running = true;
    private Thread outputThread;
    public HwAudioEncoder() {

    }

//    public void prepare() {
//        initCodec();
//        initRecorder();
//    }






    private boolean initCodec() {
        try {
            MediaFormat format = MediaFormat.createAudioFormat(audioMime, mSampleRate, 1);
            format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, mSampleRate);
            mAudioCodec = MediaCodec.createEncoderByType(audioMime);
            mAudioCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mAudioCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public void initEncoder() {
        outputThread = createOutputThread();
        outputThread.start();
    }

    @Override
    public void encodePcm(byte[] bufferBytes, int len, long presentationTimeUs) {

    }

    private Thread createOutputThread() {
        return new Thread() {
            @Override
            public void run() {
                while (running) {
                    deliverEncodedAudio();
                }
                releaseCodecOnOutputThread();
            }
        };
    }

    private void deliverEncodedAudio() {

    }

    private void releaseCodecOnOutputThread() {

    }
}