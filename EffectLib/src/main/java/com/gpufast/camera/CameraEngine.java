package com.gpufast.camera;

import android.os.Build;
import android.view.SurfaceView;

/**
 * @author Sivin 2018/10/26
 * Description:管理和操作Camera
 */
public class CameraEngine {

    private static CameraEngine mInstance = null;

    private ICamera mICamera;

    //本地预览的surfaceView
    private SurfaceView mLocalPreview = null;


    private CameraEngine() {
        if(Build.VERSION.SDK_INT >Build.VERSION_CODES.LOLLIPOP){

        }else{
            mICamera = new Camera19();
        }
    }

    public static CameraEngine getInstance() {
        if (mInstance == null) {
            synchronized (CameraEngine.class) {
                if (mInstance == null) {
                    mInstance = new CameraEngine();
                }
            }
        }
        return mInstance;
    }

    public void setLocalPreview(SurfaceView view){
        mLocalPreview = view;
    }


    /**
     * 开始预览摄像头数据
     */
    public void startPreview(){

    }

    /**
     * 切换摄像头
     */
    public void switchCamera(){

    }


    public void release(){

    }

}
