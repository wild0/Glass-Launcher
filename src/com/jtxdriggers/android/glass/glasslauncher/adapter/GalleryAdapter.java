package com.jtxdriggers.android.glass.glasslauncher.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.jtxdriggers.android.glass.glasslauncher.R;

/**
 * Created by roy on 2015/4/18.
 */
public class GalleryAdapter  extends CardScrollAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private int mImageColumnIndex, mIdColumnIndex;
    private Cursor mCursor;

    public GalleryAdapter(Context context, Cursor c) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mCursor = c;
    }

    private class ImageLoader extends AsyncTask<Object, String, Bitmap> {

        private ImageView view;

        @Override
        protected Bitmap doInBackground(Object... parameters) {

            // Get the passed arguments here
            view = (ImageView) parameters[0];
            long id = (long)(Long)parameters[1];

            // Create bitmap from passed in Uri here
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(GalleryAdapter.this.mContext.getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, options);

            return curThumb;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null && view != null) {
                view.setColorFilter(Color.rgb(0xAA, 0xAA, 0xAA), android.graphics.PorterDuff.Mode.MULTIPLY);
                view.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        mCursor.moveToPosition(position);
        return mCursor;
    }

    @Override
    public int getPosition(Object item) {
        return ((Cursor)item).getPosition();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if(convertView == null) {
            view = mInflater.inflate(R.layout.gallery_row, parent, false);
        } else {
            view = convertView;
        }

        mImageColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        mIdColumnIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

        mCursor.moveToPosition(position);

        TextView name = (TextView)view.findViewById(R.id.name);
        ImageView thumbnail = (ImageView)view.findViewById(R.id.thumbnail);
        TextView count = (TextView)view.findViewById(R.id.count);
        count.setText(String.format("%d of %d", mCursor.getPosition() + 1, mCursor.getCount()));
        name.setText(mCursor.getString(mImageColumnIndex));

        long id = mCursor.getLong(mIdColumnIndex);

        new ImageLoader().execute(thumbnail, Long.valueOf(id));

        return view;
    }

}

