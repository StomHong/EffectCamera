package com.gpufast.camera;

import android.hardware.Camera;

import java.util.List;

/**
 * @author Sivin 2018/10/26
 * Description:使用旧版本camera类API实现实现
 */
public class Camera19 implements ICamera {

    private static final int FRONT_FACE = 0;
    private static final int BACK_FACE = 1;

    private Camera mCamera = null;
    private int mCameraFace;
    private boolean isPreviewing = false;

    @Override
    public boolean openFrontCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCamera = Camera.open(i);
                mCameraFace = FRONT_FACE;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean openBackCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCamera = Camera.open(i);
                mCameraFace = FRONT_FACE;
                return true;
            }
        }
        return false;
    }

    @Override
    public void switchCamera() {
        if (mCamera == null) return;
        stopCamera();
        if (mCameraFace == BACK_FACE) {
            openFrontCamera();//打开当前选中的摄像头
        } else {
            openBackCamera();//打开当前选中的摄像头
        }
    }


    @Override
    public void startPreview() {
        if (mCamera == null) return;
        if (isPreviewing) {
            stopPreview();
        }


    }

    @Override
    public void stopPreview() {


    }


    private void initCamera(int surfaceWidth,int surfaceHeight,float ratio){

        if(mCamera == null) return;

        Camera.Parameters parameters = mCamera.getParameters();

        //获取最佳预览尺寸
        Camera.Size preViewSize = CameraUtils.chooseOptimalSize(parameters.getSupportedPreviewSizes(),
                surfaceWidth,surfaceHeight,ratio);

        parameters.setPreviewSize(preViewSize.width, preViewSize.height);

        // 设置摄像头为自动聚焦
        List<String> focusModes = parameters.getSupportedFocusModes();

        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
    }


    @Override
    public void stopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }

    }


}
