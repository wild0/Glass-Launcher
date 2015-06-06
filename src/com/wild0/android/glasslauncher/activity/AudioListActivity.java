package com.wild0.android.glasslauncher.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.wild0.android.glasslauncher.adapter.AudioAdapter;
import com.wild0.android.glasslauncher.component.SoundManager;
import com.wild0.android.glasslauncher.component.VoiceDetection;
import com.wild0.android.glasslauncher.manager.VoiceControlManager;
import com.wild0.android.glasslauncher.service.PlayAudioService;

/**
 * Created by roy on 2015/2/21.
 */
public class AudioListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>,  GestureDetector.BaseListener, GestureDetector.FingerListener, VoiceDetection.VoiceDetectionListener {

    public static final String EXTRA_AUDIO_BUCKET = "audio bucket";

    private static final int URL_LOADER = 0;
    String TAG = "glass";
    private Cursor mAudioCursor;
    //private ListView mList;
    private CardScrollView mList;
    //private GestureDetector mTouchDetector;
    private AudioAdapter mAdapter;

    SoundManager soundManager = null;

    private View mEmptyMessage;
    private int mLength;
    private ProgressBar mDeleteProgress;

    private PlayAudioService audioService;
    GestureDetector mGestureDetector;
    static AudioListActivity instance = null;

    Intent audioServiceIntent;

    public final static int CMD_UPDATE_LIST = 10;


    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder baBinder) {
            audioService = ((PlayAudioService.PlayAudioServiceBinder) baBinder)
                    .getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            audioService = null;
        }
    };

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case CMD_UPDATE_LIST:
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            getSoundManager().playSound(SoundManager.SoundId.TAP);
            openOptionsMenu();
        }
    };

    public SoundManager getSoundManager()
    {
        //return ((GlassApplication)getApplication()).getSoundManager();
        return soundManager;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.audio_picker, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mLength > 0) {
            menu.findItem(R.id.play).setVisible(true).setEnabled(true);
            menu.findItem(R.id.delete).setVisible(true).setEnabled(true);
        } else {
            menu.findItem(R.id.play).setVisible(false).setEnabled(false);
            menu.findItem(R.id.delete).setVisible(false).setEnabled(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        Log.d(TAG, "onCreateLoader:" + loaderId);
        switch(loaderId) {
            case URL_LOADER:
                String[] proj = { MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.SIZE };

                //long bucketId = getIntent().getLongExtra(EXTRA_MOVIE_BUCKET, 0L);
                long bucketId = getIntent().getLongExtra(EXTRA_AUDIO_BUCKET, 0L);
                String selection = null;
                String[] selectionArgs = null;

                //if(bucketId != 0) {
                //    selection = MediaStore.Video.Media.DATA + " not like ? and " + MediaStore.Video.Media.BUCKET_ID + " =? " ;
                //    selectionArgs = new String[] {"%sdcard/glass_cached_files%", Long.toString(bucketId) };
                //} else {
                    //selection = MediaStore.Video.Media.DATA + " not like ? ";
                    //selectionArgs = new String[] { "%sdcard/glass_cached_files%" };
                //}

                return new CursorLoader(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        proj, selection, selectionArgs, MediaStore.Audio.Media.DISPLAY_NAME);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished:" + cursor.getCount());


        mAudioCursor = cursor;

        mAdapter = new AudioAdapter(this, cursor);
        mList.setAdapter(mAdapter);

        //mAdapter.swapCursor(cursor);

        mLength = cursor.getCount();
        invalidateOptionsMenu();
        //mLength惟資料數量

        if(mLength == 0) {
            mEmptyMessage.setVisibility(View.VISIBLE);
            long bucketId = getIntent().getLongExtra(EXTRA_AUDIO_BUCKET, 0L);
            if(bucketId != 0) {
                finish();
            }
        } else {
            mEmptyMessage.setVisibility(View.GONE);
        }

        /*
        mAudioCursor = cursor;

        //mList = (ListView)findViewById(R.id.audio_listView);
        //mAdapter = new AudioAdapter(this, cursor);
       // mList.setAdapter(mAdapter);



        //mEmptyMessage = findViewById(R.id.empty);
        //mAdapter.swapCursor(cursor);

        mLength = cursor.getCount();
        invalidateOptionsMenu();
        //mLength惟資料數量

        //mList.setOnItemClickListener( instance);

        mList = (CardScrollView)findViewById(R.id.list);
        mEmptyMessage = findViewById(R.id.empty);
        mDeleteProgress = (ProgressBar)findViewById(R.id.progress);

        mList.setOnItemClickListener(mItemClickListener);

        getLoaderManager().initLoader(URL_LOADER, null, this);

        mList.activate();





        if(mLength == 0) {
            mEmptyMessage.setVisibility(View.VISIBLE);
            long bucketId = getIntent().getLongExtra(EXTRA_AUDIO_BUCKET, 0L);
            if(bucketId != 0) {
                finish();
            }
        } else {
            mEmptyMessage.setVisibility(View.GONE);
        }
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        VoiceControlManager.start(instance);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VoiceControlManager.stop();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        //mAdapter.swapCursor(null);
        Log.d(TAG, "onLoaderReset" );
        //mAdapter = new AudioAdapter(this, mAudioCursor);
        //mList.setAdapter(mAdapter);
        mAdapter = new AudioAdapter(this, mAudioCursor);
        mList.setAdapter(mAdapter);
    }

    @Override
    public boolean onGesture(Gesture gesture) {

        Log.d(TAG, "onGesture:" + gesture);

        if(gesture==Gesture.SWIPE_DOWN){
            finish();
        }
        /*
        if(gesture==Gesture.SWIPE_LEFT){
            mAdapter.moveNext();
            sendMessage(CMD_UPDATE_LIST);
        }
        else if(gesture==Gesture.SWIPE_RIGHT){
            mAdapter.movePrevious();
            sendMessage(CMD_UPDATE_LIST);

        }
        else if(gesture==Gesture.TAP){
            //if (v == startPlaybackButton) {
            if(!PlayAudioService.isPlaying()) {
                audioServiceIntent = new Intent(this, PlayAudioService.class);
                audioServiceIntent.setAction(PlayAudioService.ACTION_PLAY);
                startService(audioServiceIntent);
            }
            else{
                audioServiceIntent = new Intent(this, PlayAudioService.class);
                audioServiceIntent.setAction(PlayAudioService.ACTION_STOP);
                startService(audioServiceIntent);
            }
                //bindService(audioServiceIntent, serviceConnection,
                //        Context.BIND_AUTO_CREATE);
            //} else if (v == stopPlaybackButton) {
                //unbindService(serviceConnection);
                //stopService(playbackServiceIntent);
            //}

            //else if (v == haveFunButton) {
            //    baService.haveFun();
            //}
        }
        else if(gesture==Gesture.SWIPE_DOWN){
            finish();
        }
        */
        return true;

    }

    @Override
    public void onFingerCountChanged(int i, int i2) {

    }


    /**
     * Overridden to allow the gesture detector to process motion events that occur anywhere within
     * the activity.
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        instance = this;
        soundManager = new SoundManager(this);

        setContentView(R.layout.custom_audio_list_layout);
        mLength = -1;
        mList = (CardScrollView)findViewById(R.id.list);
        mEmptyMessage = findViewById(R.id.empty);
        mDeleteProgress = (ProgressBar)findViewById(R.id.progress);

        mList.setOnItemClickListener(mItemClickListener);

        getLoaderManager().initLoader(URL_LOADER, null, this);

        mList.activate();

        mGestureDetector = new GestureDetector(this).setBaseListener(this).setFingerListener(this);

        /*
        super.onCreate(savedInstanceState);

        instance = this;


        setContentView(R.layout.custom_audio_list_layout);
        //mLength = -1;

        //mEmptyMessage = findViewById(R.id.empty);
        //mDeleteProgress = (ProgressBar)findViewById(R.id.progress);

        //mList.setOnItemClickListener(mItemClickListener);

        getLoaderManager().initLoader(URL_LOADER, null, this);
        mEmptyMessage = findViewById(R.id.empty);

        //mList.activate();


        mGestureDetector = new GestureDetector(this).setBaseListener(this).setFingerListener(this);
        //mGestureDetector = new GestureDetector(this)
        //        .setOneFingerScrollListener(this).setTwoFingerScrollListener(this);

        Log.d(TAG,"AudioListActivity:onCreate:mGestureDetector:"+mGestureDetector.toString());
        */
    }
    public static AudioListActivity getInstance(){

        return instance;
    }


    public void sendMessage(int cmd, Bundle data) {
        Message msg = new Message();
        msg.what = cmd;
        if (data != null) {
            msg.setData(data);
        }
        handler.sendMessage(msg);

    }

    public void sendMessage(int cmd) {
        sendMessage(cmd, null);

    }


    @Override
    public void onHotwordDetected() {

    }

    @Override
    public void onPhraseDetected(int index, String phrase) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.play:
                play();
                return true;
            case R.id.auto_play:
                //autoPlay();
                return true;
            case R.id.delete:
                delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void delete() {
        int position = mList.getSelectedItemPosition();

        if(mLength > 0 && position != -1) {
            mAudioCursor.moveToPosition(position);

            int idIndex = mAudioCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            long audioId = mAudioCursor.getLong(idIndex);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri itemUri = ContentUris.withAppendedId(contentUri, audioId);

            DeleteHandler deleteHandler = new DeleteHandler(getContentResolver(), mDeleteProgress);
            deleteHandler.startDelete(0, this, itemUri, null, null);
        }
    }

    private void play() {
        int position = mList.getSelectedItemPosition();

        if(mLength > 0 && position != -1) {
            int index = mAudioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            mAudioCursor.moveToPosition(position);
            String audioLocationPath = mAudioCursor.getString(index);
            //Uri audioLocation = Uri.parse(audioLocationPath);
            Bundle data = new Bundle();
            data.putString("play_uri",audioLocationPath);


            if(!PlayAudioService.isPlaying()) {
                audioServiceIntent = new Intent(this, PlayAudioService.class);
                audioServiceIntent.putExtras(data);

                audioServiceIntent.setAction(PlayAudioService.ACTION_PLAY);
                startService(audioServiceIntent);
            }
            else{
                audioServiceIntent = new Intent(this, PlayAudioService.class);
                audioServiceIntent.setAction(PlayAudioService.ACTION_STOP);
                startService(audioServiceIntent);
            }


            //Intent intent = new Intent(Intent.ACTION_VIEW);
            //intent.setDataAndType(videoLocation, "video/*");
            //getSoundManager().playSound(SoundManager.SoundId.VIDEO_START);
            //startActivity(intent);
        }
    }

    private static class DeleteHandler extends AsyncQueryHandler {
        private ProgressBar mProgress;

        public DeleteHandler(ContentResolver cr, ProgressBar progress) {
            super(cr);
            mProgress = progress;
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);

            mProgress.setVisibility(View.INVISIBLE);

            Context context = (Context)cookie;

            if(result == 0)
            {
                Toast.makeText(context, "Error. Could not delete audio.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Audio deleted", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
