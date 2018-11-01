package com.gpufast.camera;


import android.hardware.Camera;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Sivin 2018/3/24
 * @Description
 */
public class CameraUtils {

    private static final String TAG = "CameraUtils";

    /**
     *
     * @param list 相机支持的尺寸列表
     * @param surfaceWidth 预览视频view的宽度
     * @param surfaceHeight 预览视频的高度
     * @param rate 预览视频的宽高比
     * @return
     */
    public static Camera.Size chooseOptimalSize(List<Camera.Size> list, int surfaceWidth , int surfaceHeight ,float rate) {

        Collections.sort(list, new CameraSizeComparator());

        int i = 0;

        for (Camera.Size s : list) {
            if ((s.width >= surfaceWidth) && equalRate(s, rate)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            Log.e(TAG, "找不到合适的预览尺寸！！！");
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }



    public static class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }
    }


    public static boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

}
