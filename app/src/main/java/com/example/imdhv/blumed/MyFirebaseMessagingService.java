package com.example.imdhv.blumed;

import android.app.Service;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
    String frommobile,tomobile,data,status;
    int senderttl;
    long creationtime;

    final static public String COPA_RESULT = "com.example.imdhv.blumed.MyFirebaseMessagingService.REQUEST_PROCESSED";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);



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
                String a = obj.get("creationtime").toString();
                creationtime = Long.parseLong(a);
                String b = obj.get("senderttl").toString();
                senderttl = Integer.parseInt(b);
                String status = obj.get("status").toString();
                boolean chatresult;

                Log.e("FCM MSG", "Data Received" );

                chatresult = checkforchatlist();

                Log.e("FCM MSG", "Result of chatresult" + chatresult );

                if(chatresult == true)
                {
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
                else if(chatresult == false)
                {

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();




        }



        Intent intent = new Intent(COPA_RESULT);
        broadcaster.sendBroadcast(intent);


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
