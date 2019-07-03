package com.gpufast.effectcamera.recorder.presenter;

import android.view.SurfaceView;

import com.gpufast.camera.CameraEngine;
import com.gpufast.effectcamera.recorder.contract.RecorderContract;
import com.gpufast.recorder.RecorderEngine;
import com.gpufast.recorder.RecorderParams;
import com.gpufast.utils.ELog;

public class RecorderPresenter implements RecorderContract.Presenter {
    private static final String TAG = "RecorderPresenter";

    private RecorderContract.View mView;
    private CameraEngine mCameraEngine;


    public void attachView(RecorderContract.View view){
        mView = view;
    }


    public void init() {
        SurfaceView preview = mView.getPreview();
        if(preview == null){
            ELog.e(TAG,"preview == null : true");
            return;
        }
        mCameraEngine = CameraEngine.getInstance();
        mCameraEngine.setLocalPreview(preview);
    }

    @Override
    public void switchCamera() {
        mCameraEngine.switchCamera();
    }

    @Override
    public void setRecorderParameter(RecorderParams params) {
        RecorderEngine.create().setParams(params);
    }

    @Override
    public void startRecorder() {
        RecorderEngine.create().startRecorder();
    }

    @Override
    public void stopRecorder() {
        RecorderEngine.create().stopRecorder();
    }

    @Override
    public void jointVideo() {

    }

    @Override
    public boolean isRecording() {
        return RecorderEngine.create().isRecording();
    }
}
