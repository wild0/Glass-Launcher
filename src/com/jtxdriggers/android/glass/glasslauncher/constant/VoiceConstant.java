package com.jtxdriggers.android.glass.glasslauncher.constant;

/**
 * Created by roy on 2015/2/28.
 */
public class VoiceConstant {
    public static final String EXTRA_KEY_VOICE_ACTION = "extra_action";

    // Action commands. (One word)
    public static final String ACTION_START_DICTATION = "dictate";
    public static final String ACTION_STOP_VOICEDEMO = "stop";
    public static final String ACTION_VIDEO = "video";
    // ...

    // For onActivityResult()
    public static final int SPEECH_REQUEST = 0;

    //command
    public static final String CMD_ACTIVATE = "raytech";
    //public static final String CMD_MEDIA = "media";
    public static  String[] mPhrases = new String[] { "call", "take", "record", "music", "video", "map", "cancel" };

    public static final int TAKE_PICTURE_REQUEST = 60;
    public static final int CAPTURE_VIDEO_REQUEST_CODE = 61;
}
