package com.jtxdriggers.android.glass.glasslauncher.manager;

import android.content.Context;

import com.jtxdriggers.android.glass.glasslauncher.component.VoiceDetection;
import com.jtxdriggers.android.glass.glasslauncher.constant.VoiceConstant;

/**
 * Created by roy on 2015/5/7.
 */
public class VoiceControlManager {
    static VoiceDetection mVoiceDetection = null;
    static String[] mPhrases = new String[] { "call", "take", "record", "music", "video", "map", "cancel" };
    public static  void init(Context ctx){
        if(mVoiceDetection==null) {
            mVoiceDetection = new VoiceDetection(ctx, VoiceConstant.CMD_ACTIVATE, null, false, mPhrases);
        }
    }
    public static void start(VoiceDetection.VoiceDetectionListener listener){
        //mVoiceDetection.setListener(listener);
        if(mVoiceDetection!=null) {
            mVoiceDetection.start(listener);
        }
    }
    public static void stop(){
        if(mVoiceDetection!=null) {
            mVoiceDetection.stop();
        }
    }

}
