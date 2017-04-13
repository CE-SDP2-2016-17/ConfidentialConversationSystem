package com.example.imdhv.blumed;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String frommobile,tomobile,data,status,decText;
    int senderttl;
    long creationtime;
    private Handler h=new Handler();
    LocalBroadcastManager broadcaster;
    Context context;

    final static public String COPA_RESULT = "com.example.imdhv.blumed.MyFirebaseMessagingService.REQUEST_PROCESSED";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
        int caid=sp.getInt("caid",0);

            broadcaster = LocalBroadcastManager.getInstance(this);

        Intent intent = new Intent(this, LockScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.ic_shortcut_chat_bubble);
            mBuilder.setContentTitle("Blumed");
            mBuilder.setContentText("New Message");
            mBuilder.setAutoCancel(true);
            mBuilder.setSound(notificationSound);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());


            Log.e("FCM MSG", "From: " + remoteMessage.getData());

            String msgobj = remoteMessage.getData().get("newmessage");

            try {
                JSONArray arr = new JSONArray(msgobj);
                SQLiteDatabase database = openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                database.execSQL("CREATE TABLE IF NOT EXISTS MESSAGE (id integer primary key autoincrement,frommobile TEXT, tomobile text, data text, creationtime text,senderttl int,status text,action text);");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    frommobile = obj.get("frommobile").toString();
                    tomobile = obj.get("tomobile").toString();
                    data = obj.get("data").toString();
                    //try{
                      //  data=Utility.ServerDecrypt("data",sp.getString("private_key",""));
                    //}
                    //catch(Exception e1){
                      //  data=e1.toString();
                    //}
                    byte[] enc = Utility.encryptClient(data);
                    String a = obj.get("creationtime").toString();
                    creationtime = Long.parseLong(a);
                    String b = obj.get("senderttl").toString();
                    senderttl = Integer.parseInt(b);
                    String status = obj.get("status").toString();
                    boolean chatresult;

                    Log.e("FCM MSG", "Data Received");

                    chatresult = checkforchatlist();

                    Log.e("FCM MSG", "Result of chatresult" + chatresult);

                    if (chatresult == true) {
                        ContentValues cv = new ContentValues();
                        cv.put("frommobile", frommobile);
                        cv.put("tomobile", tomobile);
                        cv.put("data", enc);
                        cv.put("creationtime", creationtime);
                        cv.put("senderttl", senderttl);
                        cv.put("status", status);
                        cv.put("action", "r");
                        database.insertOrThrow("MESSAGE", null, cv);
                    } else if (chatresult == false) {

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();


            }


            intent = new Intent(COPA_RESULT);
            broadcaster.sendBroadcast(intent);
    }
    @Override
    public void onDestroy() {
        h.removeCallbacksAndMessages(null);
    }


    boolean checkforchatlist()
    {
        SQLiteDatabase database = openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        Cursor resultSet = database.rawQuery("Select * from CHATLIST WHERE Number = '" + frommobile + "'", null);
        if (resultSet.moveToFirst()) {
            Log.e("FCM MSG", "Inside First Resultset" );
            //while (resultSet.moveToNext())
            //{
                Log.e("FCM MSG", "Inside While" );
                String name = resultSet.getString(0);
                String number = resultSet.getString(1);
                Log.e("FCM MSG", "Name and Number " + name + number );
                if (number.equals(frommobile))
                    return true;

            //}
        }
        else
        {
            Log.e("FCM MSG", "First resultset skipped" );
            Cursor resultSet2 = database.rawQuery("Select * from USERS WHERE Number='" + frommobile.trim() + "'", null);
            Log.e("FCM MSG", "Name and number and fromnumber from Users"+frommobile );

            if (resultSet2.moveToFirst())
            {
                Log.e("FCM MSG", "Inside 2nd resultset" );
                //while (resultSet2.moveToNext())
                //{
                    Log.e("FCM MSG", "Inside while loop" );
                    String name2 = resultSet2.getString(0);
                    String number2 = resultSet2.getString(1);
                    Log.e("FCM MSG", "Name and number and fromnumber from Users"+name2+" "+number2+" "+frommobile );
                    if(number2.equals(frommobile))
                    {
                        database.execSQL("CREATE TABLE IF NOT EXISTS CHATLIST (Name TEXT,Number TEXT);");
                        ContentValues cv = new ContentValues();
                        cv.put("Name", name2);
                        cv.put("Number",number2);
                        database.insertOrThrow("CHATLIST", null, cv);
                        return true;
                    }
                    else
                        return false;
                //}

            }
            else
            {

                Log.e("FCM MSG", "Second resultset skipped" );
                return false;
            }

        }

        return false;

    }
}
