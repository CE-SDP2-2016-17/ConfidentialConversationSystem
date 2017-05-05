package com.example.imdhv.blumed;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private TextView chatName;
    BroadcastReceiver receiver;
    int id = 1;
    String ID="0";
    String rpdata, rpttl, encText;
    ChatMessage chatMessage;
    protected String TAG = "Bound";

    SharedPreferences sp;
    String number, name, mynumber;

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(MyFirebaseMessagingService.COPA_RESULT));
    }

    @Override
    public void onStop(){
        System.exit(0);
        super.onStop();
    }
    @Override
    public void onPause(){
        System.exit(0);
        super.onPause();
    }
    void doit() {
        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        Log.e(TAG,"other: "+ number);
        Log.e(TAG,"my: "+ mynumber);
        SQLiteDatabase database = openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String p = "Select * from MESSAGE WHERE (status = 'Pending' and frommobile ='" + number.trim() + "' and tomobile ='" + mynumber.trim() + "')" + " or (status = 'Pending' and tomobile ='" + number.trim() + "' and frommobile ='" + mynumber.trim() + "')" + " or (status = 'timerstart' and frommobile ='" + number.trim() + "')";
        Cursor resultSet = database.rawQuery(p, null);
        id = 1;
        if (resultSet.moveToFirst()) {
            do {
                Log.e(TAG,"dummy log");
                ID = resultSet.getString(0);
                String frommobile = resultSet.getString(1);
                String tomobile = resultSet.getString(2);
                byte[] c = resultSet.getBlob(3);
                String creationtime = resultSet.getString(4);
                String senderttl = resultSet.getString(5);
                String status = resultSet.getString(6);
                String action = resultSet.getString(7);
                ChatMessage chatMessage = new ChatMessage();


                chatMessage.setId(id);
                String text = "";
                try {
                    text = Utility.decryptClient(c);
                    //Toast.makeText(ChatActivity.this,text,Toast.LENGTH_LONG).show();
                } catch (GeneralSecurityException x) {
                    //Toast.makeText(ChatActivity.this,x.toString(),Toast.LENGTH_LONG).show();
                }
                //chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMessage(text);

                Date d1 = new Date();
                d1.setTime((long) (Long.parseLong(creationtime) * 1000));
                chatMessage.setDate((d1.toString()).substring(0, 20));


                if (action.equalsIgnoreCase("s"))
                {

                    chatMessage.setMe(false);
                }
                else
                {
                    chatMessage.setMe(true);
                    if(!status.equalsIgnoreCase("timerstart"))
                    {
                        setupAlarm(Integer.parseInt(senderttl));
                        String temp = "UPDATE MESSAGE SET status = 'timerstart' WHERE id ='"+ ID+"'";
                        database.execSQL(temp);
                    }
                }



                displayMessage(chatMessage);
                id++;

            }
            while (resultSet.moveToNext());


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"on Create");
        setContentView(R.layout.activity_chat);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        findAllViews();

        //Retrieving the Sender's number and name
        number = getIntent().getStringExtra("number");
        name = getIntent().getStringExtra("name");

        //Retrieving owner number
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        mynumber = sp.getString("mynumber", "");
        //Setting Sender's ttl to 50
        rpttl = sp.getString("pref_sender_ttl", "");


        Log.e("ttl","The original ttl is : "+ rpttl);
        //rpttl = "5";

        doit();
        // Toast.makeText(this,rpttl,Toast.LENGTH_SHORT).show();
        // Toast.makeText(this,mynumber,Toast.LENGTH_SHORT).show();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (this != null) {

                    //Log.e(TAG,"dummy log");

                    adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
                    messagesContainer.setAdapter(adapter);

                    SQLiteDatabase database = openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                    String p = "Select * from MESSAGE WHERE (status = 'Pending' and frommobile ='" + number.trim() + "' and tomobile ='" + mynumber.trim() + "')" + " or (status = 'Pending' and tomobile ='" + number.trim() + "' and frommobile ='" + mynumber.trim() + "')" + " or (status = 'timerstart' and frommobile ='" + number.trim() + "' and tomobile = '" +mynumber.trim()+"')";
                    //String p = "Select * from MESSAGE WHERE (status = 'Pending' and frommobile ='" + number + "')" + " or (status = 'Pending' and tomobile ='" + number + "')" + " or (status = 'timerstart' and tomobile ='" + number + "')";

                    Cursor resultSet = database.rawQuery(p, null);
                    id = 1;
                    if (resultSet.moveToFirst()) {
                        do {
                            Log.e(TAG,"resultset broadcast");
                            ID = resultSet.getString(0);
                            String frommobile = resultSet.getString(1);
                            String tomobile = resultSet.getString(2);
                            byte[] c = resultSet.getBlob(3);
                            String creationtime = resultSet.getString(4);
                            String senderttl = resultSet.getString(5);
                            String status = resultSet.getString(6);
                            String action = resultSet.getString(7);
                            ChatMessage chatMessage = new ChatMessage();


                            chatMessage.setId(id);
                            String text = "";
                            try {
                                text = Utility.decryptClient(c);
                            } catch (Exception x) {
                                Toast.makeText(ChatActivity.this, x.toString(), Toast.LENGTH_LONG).show();
                            }
                            //chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                            chatMessage.setMessage(text);
                            Date d1 = new Date();
                            d1.setTime((long) (Long.parseLong(creationtime) * 1000));
                            chatMessage.setDate((d1.toString()).substring(0, 20));

                            if (action.equalsIgnoreCase("s"))
                            {

                                chatMessage.setMe(false);
                            }
                            else
                            {
                                chatMessage.setMe(true);
                                if(!status.equalsIgnoreCase("timerstart"))
                                {
                                    Log.e(TAG,"broadcast set alarm");
                                    setupAlarm(Integer.parseInt(senderttl));
                                    String temp = "UPDATE MESSAGE SET status = 'timerstart' WHERE id ='"+ ID+"'";
                                    database.execSQL(temp);
                                }
                            }
                            displayMessage(chatMessage);
                            id++;
                        }
                        while (resultSet.moveToNext());
                    }
                }
            }
        };
        chatName.setText(name);
        // loadDummyHistory();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                // now connect with php and pass un, pw to server, server will decide whether correct or not
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    if(sp.getInt("Online",0)==1)
                    {
                        chatMessage = new ChatMessage();
                        chatMessage.setId(id);
                        chatMessage.setMessage(messageText);
                        rpdata = messageText;
                        try {
                            encText = Utility.ServerEncrypt(rpdata, sp.getString("public_key", ""));
                        } catch (Exception e) {
                            Toast.makeText(ChatActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                        chatMessage.setMe(false);
                        id++;
                        messageET.setText("");
                        MyTask t = new MyTask();
                        t.execute();
                        //Toast.makeText(ChatActivity.this, sp.getString("private_key",""), Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(ChatActivity.this, "User is not logged in", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void findAllViews() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);
        chatName = (TextView) findViewById(R.id.chatName);
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }


    class MyTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(ChatActivity.this,s,Toast.LENGTH_SHORT).show();

            if (s != "0") {
                displayMessage(chatMessage);
                //Toast.makeText(ChatActivity.this,"Done yeah",Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(ChatActivity.this, "Other User is not logged in", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            RequestPackage rp = new RequestPackage();
            String ans;
            rp.setUri(Utility.serverurl);
            rp.setParam("type", "sendmessage");
            rp.setParam("data", encText);
            rp.setParam("frommobile", mynumber.trim());
            rp.setParam("tomobile", number);
            rp.setParam("senderttl", rpttl);
            rp.setMethod("POST");
            ans = HttpManager.getData(rp);
            try {
                SQLiteDatabase database = openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                database.execSQL("CREATE TABLE IF NOT EXISTS MESSAGE (id integer primary key autoincrement,frommobile TEXT, tomobile text, data text, creationtime text,senderttl int,status text,action text);");
                ContentValues cv = new ContentValues();
                cv.put("frommobile", mynumber.trim());
                cv.put("tomobile", number);
                byte[] enc = Utility.encryptClient(rpdata);
                cv.put("data", enc);
                Date ddd = new Date();
                cv.put("creationtime", ddd.getTime() / 1000);
                cv.put("senderttl", rpttl);
                cv.put("status", "Pending");
                cv.put("action", "s");
                database.insertOrThrow("MESSAGE", null, cv);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return ans;
        }
    }
    private void setupAlarm(int seconds) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), OnAlarmReceive.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        intent.putExtra("id", ID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ChatActivity.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Log.e(TAG, "Setup the Alarm");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, seconds);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}