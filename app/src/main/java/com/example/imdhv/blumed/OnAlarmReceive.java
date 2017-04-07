package com.example.imdhv.blumed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by imdhv on 07-Apr-17.
 */

public class OnAlarmReceive extends BroadcastReceiver {

    protected String TAG = "Bound";
    LocalBroadcastManager broadcaster;
    final static public String COPA_RESULT = "com.example.imdhv.blumed.MyFirebaseMessagingService.REQUEST_PROCESSED";


    @Override
    public void onReceive(Context context, Intent intent) {

        String id = intent.getStringExtra("id");
        Log.e(TAG,"On the verge of deleting the message with id: "+id);
        SQLiteDatabase database = context.openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        database.execSQL("DELETE FROM " + "MESSAGE" + " WHERE " + "id" + "= '" + id + "'");

        broadcaster = LocalBroadcastManager.getInstance(context);
        intent = new Intent(COPA_RESULT);
        broadcaster.sendBroadcast(intent);
    }
}
