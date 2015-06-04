package com.jtxdriggers.android.glass.glasslauncher.component;

/**
 * Created by roy on 2015/2/18.
 */
import java.util.Locale;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jtxdriggers.android.glass.glasslauncher.R;

public class VideoSeekBar {
    private ViewGroup mSeekView;
    private ViewGroup mAnchor;
    private SeekBar mSeekBar;
    private View mPositionView;
    private TextView mPosition;
    private boolean mShowing;
    private Handler mHandler;

    public VideoSeekBar(Context context) {
        mSeekView = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.video_seek_bar, null);
        mSeekBar = (SeekBar)mSeekView.findViewById(R.id.video_seekbar);

        mPositionView = LayoutInflater.from(context).inflate(R.layout.video_progress_view, null);
        Typeface robotoThin = Typeface.createFromAsset(context.getAssets(), "Roboto-Thin.ttf");
        mPosition = (TextView)mPositionView.findViewById(R.id.video_progress_view_position);
        mPosition.setTypeface(robotoThin);

        mHandler = new Handler();
    }

    public void setAnchorView(ViewGroup view) {
        mAnchor = view;
    }

    public boolean isShowing() {
        return mShowing;
    }

    public void show() {
        mHandler.removeCallbacks(mRunnable);

        if (!mShowing && mAnchor != null) {

            FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM
            );

            mAnchor.addView(mSeekView, tlp);
            mAnchor.addView(mPositionView);

            mShowing = true;
        }
    }

    public void hide() {
        if (mAnchor == null) {
            return;
        }

        mHandler.postDelayed(mRunnable, 2500);
    }

    public void hideNow() {
        if (mAnchor == null) {
            return;
        }

        removeViews();
    }

    public void stop() {
        mHandler.removeCallbacks(mRunnable);
        removeViews();
    }

    public void setDuration(int duration) {
        mSeekBar.setMax(duration);
    }

    public void setProgress(int progress) {
        mSeekBar.setProgress(progress);
        int totalSeconds = progress / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds - seconds) / 60;
        int hours = (totalSeconds - 60*minutes - seconds) / 60;
        String position = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        mPosition.setText(position);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            removeViews();
        }
    };

    private void removeViews() {
        try {
            mAnchor.removeView(mSeekView);
            mAnchor.removeView(mPositionView);
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }

        mShowing = false;
    }

}
