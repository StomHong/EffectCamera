package com.gpufast.recorder.file;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;


public class H264Wirter extends Thread{

    private Handler mHandler;


    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
               // writeToFile();
            }
        };
        Looper.loop();
    }

    public void write(byte[] data){

        Message.obtain(mHandler,0,data).sendToTarget();

    }


    private void writeToFile(byte[] data){



    }

}
