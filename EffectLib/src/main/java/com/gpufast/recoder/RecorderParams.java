package com.gpufast.recoder;

public class RecorderParams {

        public enum SpeedType{
            hyperslow,slow,standard,fast,veryFast
        }

        //录制视频的宽度和高度
        private int videoWidth;

        private int videoHeight;

        //视频总录制时长
        private int allTime;

        //视频保存的位置
        private String videoPath;

         //是否将音频一起录制进去
         private boolean isAuido = true;

        private SpeedType speedType;

        //背景音乐
        private String backgoundMuisc;
    }