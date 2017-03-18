package com.example.imdhv.blumed;

import android.app.Service;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final static public String COPA_RESULT = "com.example.imdhv.blumed.MyFirebaseMessagingService.REQUEST_PROCESSED";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);

        //Chats c;
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.e("FCM MSG", "From: " + remoteMessage.getData());

        String msgobj = remoteMessage.getData().get("newmessage");
        //Log.e("FCM MSG", "Notification Message Body: " + msgobj);

        //ContentProvider
        //c.onCreateView()
        try {
            JSONArray arr = new JSONArray(msgobj);
            SQLiteDatabase database = openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS MESSAGE (id integer primary key autoincrement,frommobile TEXT, tomobile text, data text, creationtime text,senderttl int,status text,action text);");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String frommobile = obj.get("frommobile").toString();
                String tomobile = obj.get("tomobile").toString();
                String data = obj.get("data").toString();
                String a = obj.get("creationtime").toString();
                long creationtime = Long.parseLong(a);
                String b = obj.get("senderttl").toString();
                int senderttl = Integer.parseInt(b);
                String status = obj.get("status").toString();

                ContentValues cv = new ContentValues();
                cv.put("frommobile", frommobile);
                cv.put("tomobile", tomobile);
                cv.put("data", data);
                cv.put("creationtime", creationtime);
                cv.put("senderttl", senderttl);
                cv.put("status", status);
                cv.put("action","r");
                database.insertOrThrow("MESSAGE", null, cv);

            }

        } catch (JSONException e) {
            e.printStackTrace();




        }
        //c.onResume();


        Intent intent = new Intent(COPA_RESULT);
        broadcaster.sendBroadcast(intent);


    }
}
