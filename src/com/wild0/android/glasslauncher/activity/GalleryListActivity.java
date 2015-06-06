package com.wild0.android.glasslauncher.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import com.wild0.android.glasslauncher.adapter.VideoAdapter;
import com.wild0.android.glasslauncher.component.SoundManager;
import com.wild0.android.glasslauncher.component.VoiceDetection;
import com.wild0.android.glasslauncher.manager.VoiceControlManager;

/**
 * Created by roy on 2015/4/18.
 */
public class GalleryListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, GestureDetector.BaseListener, GestureDetector.FingerListener,  VoiceDetection.VoiceDetectionListener{
    String TAG = "glass";


    public static final String EXTRA_IMAGE_BUCKET = "image bucket";
    public static final int RESULT_VIDEO = 1;
    private static final int URL_LOADER = 0;
    private Cursor mImageCursor;
    private CardScrollView mList;
    private GestureDetector mTouchDetector;
    private VideoAdapter mAdapter;
    private View mEmptyMessage;
    private int mLength;
    private ProgressBar mDeleteProgress;

    static SoundManager soundManager = null;
    static GalleryListActivity instance = null;

    public static GalleryListActivity getInstance(){
        return instance;
    }

    @Override
    protected void onPause() {
        super.onPause();
        VoiceControlManager.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VoiceControlManager.start(instance);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        instance = this;
        soundManager = new SoundManager(this);

        setContentView(R.layout.custom_gallery_view_layout);
        mLength = -1;
        mList = (CardScrollView)findViewById(R.id.gallery_list);
        mEmptyMessage = findViewById(R.id.empty);
        mDeleteProgress = (ProgressBar)findViewById(R.id.progress);

        mList.setOnItemClickListener(mItemClickListener);

        getLoaderManager().initLoader(URL_LOADER, null, this);

        mList.activate();

        mTouchDetector = new GestureDetector(this).setBaseListener(this).setFingerListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_picker, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //if(mLength > 0) {
        //    menu.findItem(R.id.play).setVisible(true).setEnabled(true);
        //    menu.findItem(R.id.delete).setVisible(true).setEnabled(true);
        //} else {
        //    menu.findItem(R.id.play).setVisible(false).setEnabled(false);
        //    menu.findItem(R.id.delete).setVisible(false).setEnabled(false);
        //}

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            //case R.id.play:
            //    play();
            //    return true;
            //case R.id.auto_play:
            //    autoPlay();
            //    return true;
            case R.id.delete:
                delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            getSoundManager().playSound(SoundManager.SoundId.TAP);
            openOptionsMenu();
        }
    };





    private void delete() {
        int position = mList.getSelectedItemPosition();

        if(mLength > 0 && position != -1) {
            mImageCursor.moveToPosition(position);

            int idIndex = mImageCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            long imageId = mImageCursor.getLong(idIndex);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri itemUri = ContentUris.withAppendedId(contentUri, imageId);

            DeleteHandler deleteHandler = new DeleteHandler(getContentResolver(), mDeleteProgress);
            deleteHandler.startDelete(0, this, itemUri, null, null);
        }
    }

    @Override
    public void onHotwordDetected() {

    }

    @Override
    public void onPhraseDetected(int index, String phrase) {

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
                Toast.makeText(context, "Error. Could not delete video.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Video deleted", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public SoundManager getSoundManager()
    {
        //return ((GlassApplication)getApplication()).getSoundManager();
        return soundManager;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch(loaderId) {
            case URL_LOADER:
                String[] proj = { MediaStore.Images.Media._ID,

                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.SIZE };

                long bucketId = getIntent().getLongExtra(EXTRA_IMAGE_BUCKET, 0L);
                String selection = null;
                String[] selectionArgs = null;

                if(bucketId != 0) {
                    selection = MediaStore.Images.Media.DATA + " not like ? and " + MediaStore.Images.Media.BUCKET_ID + " =? " ;
                    selectionArgs = new String[] {"%sdcard/glass_cached_files%", Long.toString(bucketId) };
                } else {
                    selection = MediaStore.Images.Media.DATA + " not like ? ";
                    selectionArgs = new String[] { "%sdcard/glass_cached_files%" };
                }
                Log.d("glass", "load video:" + selection + "," + selectionArgs);
                return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        proj, selection, selectionArgs, MediaStore.Images.Media.DISPLAY_NAME);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mImageCursor = cursor;

        mAdapter = new VideoAdapter(this, cursor);
        mList.setAdapter(mAdapter);

        //mAdapter.swapCursor(cursor);

        mLength = cursor.getCount();
        invalidateOptionsMenu();
        //mLength惟資料數量

        if(mLength == 0) {
            mEmptyMessage.setVisibility(View.VISIBLE);
            long bucketId = getIntent().getLongExtra(EXTRA_IMAGE_BUCKET, 0L);
            if(bucketId != 0) {
                finish();
            }
        } else {
            mEmptyMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        //mAdapter.swapCursor(null);
        mAdapter = new VideoAdapter(this, mImageCursor);
        mList.setAdapter(mAdapter);
    }



    /**
     * Overridden to allow the gesture detector to process motion events that occur anywhere within
     * the activity.
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mTouchDetector.onMotionEvent(event);
    }

    /**
     * This method includes special behavior to handle SWIPE_DOWN gestures. The first time the user
     * swipes down, we return true so that the user can still see the feedback in the gesture
     * label, and we fade in an instructional tip label. The second time the user swipes down, we
     * return false so that the activity can handle the event and return to the previous activity.
     */
    @Override
    public boolean onGesture(Gesture gesture) {
        //mLastGesture.setText(gesture.name());
        Log.d(TAG, "gesture:" + gesture);

        if(gesture==Gesture.SWIPE_DOWN){
            finish();
        }
        /*
        if(gesture==Gesture.SWIPE_LEFT){
            //Intent homeIntent = new Intent(instance, HomeActivity.class);
           instance.startActivity(homeIntent);
            finish();
        }
        else if(gesture==Gesture.SWIPE_RIGHT){
            Intent musicIntent = new Intent(instance, AudioIndexActivity.class);
            instance.startActivity(musicIntent);
            finish();
        }
        */
        return true;

        /*
        if (gesture == Gesture.SWIPE_DOWN) {
            if (!mSwipedDownOnce) {
                mSwipeAgainTip.animate().alpha(1.0f);
                mSwipedDownOnce = true;
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
        */
    }

    @Override
    public void onFingerCountChanged(int previousCount, int currentCount) {
        //mFingerCount.setText(Integer.toString(currentCount));
    }

}