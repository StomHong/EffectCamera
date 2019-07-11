package com.gpufast.recorder;

public class RecorderEngine {

    private static IRecorder recorder;

    public static IRecorder create() {
        if (recorder == null) {
            recorder = new EffectRecorder();
        }
        return recorder;
    }

}