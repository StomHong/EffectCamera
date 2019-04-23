package com.gpufast.recorder;

public class RecorderFactory {

    private static IRecorder recorder;

    public static IRecorder factory() {
        if (recorder == null) {
            recorder = new EffectRecorder();
        }
        return recorder;
    }

}
