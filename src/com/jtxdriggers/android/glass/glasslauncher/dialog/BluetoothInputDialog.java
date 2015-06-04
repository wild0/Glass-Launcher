package com.jtxdriggers.android.glass.glasslauncher.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jtxdriggers.android.glass.glasslauncher.R;
import com.jtxdriggers.android.glass.glasslauncher.callback.BluetoothTextWriterCallBack;
import com.jtxdriggers.android.glass.glasslauncher.manager.BluetoothManager;

/**
 * Created by roy on 2015/4/15.
 */
public class BluetoothInputDialog extends Dialog implements
        android.view.View.OnClickListener{

    BluetoothTextWriterCallBack callback = null;
        //public Activity c;
        //public Dialog d;
        //public Button yes, no;

        public BluetoothInputDialog(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.setContentView(R.layout.bt_input_dialog);


        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            /*
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);
            */
        }
        public void setText(String text){


            Log.d("glass", "BluetoothInputDialog:setText:" + text);
            callback.setText(text);
        }


        public void show(BluetoothTextWriterCallBack callback) {
            super.show();
            this.callback = callback;
            BluetoothManager.setCurrentBluetoothDialog(this);
        }

    @Override
        public void onClick(View v) {
            /*
            switch (v.getId()) {
                case R.id.btn_yes:
                    c.finish();
                    break;
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
            */
        }

}
