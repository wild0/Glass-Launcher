package com.wild0.android.glasslauncher.component;

/**
 * Created by roy on 2015/2/18.
 */
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.jtxdriggers.android.glass.glasslauncher.R;

public class SoundManager {
    private SoundPool mSoundPool;
    private int mDismiss, mTap, mVideoStart, mVideoStop, mVolumeChange;

    public enum SoundId { TAP, DISMISS, VIDEO_START, VIDEO_STOP, VOLUME_CHANGE }

    public SoundManager(Context context) {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
        mDismiss = mSoundPool.load(context, R.raw.sound_dismiss, 1);
        mTap = mSoundPool.load(context, R.raw.sound_tap, 1);
        mVideoStart = mSoundPool.load(context, R.raw.sound_video_start, 1);
        mVideoStop = mSoundPool.load(context, R.raw.sound_video_stop, 1);
        mVolumeChange = mSoundPool.load(context, R.raw.sound_volume_change, 1);
    }

    public void playSound(SoundId soundId) {
        int id = -1;
        switch(soundId) {
            case TAP:
                id = mTap;
                break;
            case DISMISS:
                id = mDismiss;
                break;
            case VIDEO_START:
                id = mVideoStart;
                break;
            case VIDEO_STOP:
                id = mVideoStop;
                break;
            case VOLUME_CHANGE:
                id = mVolumeChange;
        }

        if(id != -1) {
            mSoundPool.play(id, 1f, 1f, 0, 0, 1);
        }
    }

    public void close() {
        mSoundPool.unload(mDismiss);
        mSoundPool.unload(mTap);
        mSoundPool.unload(mVideoStart);
        mSoundPool.unload(mVideoStop);
    }

}
