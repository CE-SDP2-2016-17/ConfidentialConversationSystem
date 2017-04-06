package com.example.imdhv.blumed;

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
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
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
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private TextView chatName;
    BroadcastReceiver receiver;
    int id=1;
    String rpdata,rptype,rpttl="5";
    ChatMessage chatMessage;

    SharedPreferences sp;
    String number,name,mynumber;

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter(MyFirebaseMessagingService.COPA_RESULT));
    }

    void doit()
    {
        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        SQLiteDatabase database = openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String p ="Select * from MESSAGE WHERE (status = 'Pending' and frommobile ='" + number + "')" + " or (status = 'Pending' and tomobile ='" + number + "')" ;
        Cursor resultSet = database.rawQuery(p, null);
        id=1;
        if (resultSet.moveToFirst()) {
            do {
                String a = resultSet.getString(0);
                String ba = resultSet.getString(1);
                String b = resultSet.getString(2);
                byte[] c = resultSet.getBlob(3);
                String d = resultSet.getString(4);
                String e = resultSet.getString(5);
                String f = resultSet.getString(6);
                String g = resultSet.getString(7);
                ChatMessage chatMessage = new ChatMessage();


                chatMessage.setId(id);
                String text="";
                try {
                    text = Utility.decryptClient(c);
                    //Toast.makeText(ChatActivity.this,text,Toast.LENGTH_LONG).show();
                }catch (GeneralSecurityException x)
                {
                    //Toast.makeText(ChatActivity.this,x.toString(),Toast.LENGTH_LONG).show();
                }
                //chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMessage(text);

                Date d1 = new Date();
                d1.setTime((long)(Long.parseLong(d)*1000));
                chatMessage.setDate((d1.toString()).substring(0,20));
                if(g.equalsIgnoreCase("s"))
                    chatMessage.setMe(false);
                else
                    chatMessage.setMe(true);

                displayMessage(chatMessage);
                id++;

            }
            while (resultSet.moveToNext());



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        findAllViews();

        number = getIntent().getStringExtra("number");
        name = getIntent().getStringExtra("name");
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        mynumber = sp.getString("mynumber","");
        rpttl = sp.getString("pref_sender_ttl","");
        rpttl="50";

        doit();
       // Toast.makeText(this,rpttl,Toast.LENGTH_SHORT).show();
       // Toast.makeText(this,mynumber,Toast.LENGTH_SHORT).show();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (this != null) {
                    adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
                    messagesContainer.setAdapter(adapter);
                    SQLiteDatabase database = openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                    String p ="Select * from MESSAGE WHERE (status = 'Pending' and frommobile ='" + number + "')" + " or (status = 'Pending' and tomobile ='" + number + "')" ;
                    Cursor resultSet = database.rawQuery(p, null);
                    id=1;
                    if (resultSet.moveToFirst()) {
                        do {
                            String a = resultSet.getString(0);
                            String ba = resultSet.getString(1);
                            String b = resultSet.getString(2);
                            byte[] c = resultSet.getBlob(3);
                            String d = resultSet.getString(4);
                            String e = resultSet.getString(5);
                            String f = resultSet.getString(6);
                            String g = resultSet.getString(7);
                            ChatMessage chatMessage = new ChatMessage();


                            chatMessage.setId(id);
                            String text="";
                            try {
                                text = Utility.decryptClient(c);
                                Toast.makeText(ChatActivity.this,""+text,Toast.LENGTH_LONG);
                            }catch (GeneralSecurityException x)
                            {
                                Toast.makeText(ChatActivity.this,x.toString(),Toast.LENGTH_LONG);
                            }
                            //chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                            chatMessage.setMessage(text);
                            Date d1 = new Date();
                            d1.setTime((long)(Long.parseLong(d)*1000));
                            chatMessage.setDate((d1.toString()).substring(0,20));

                            if(g.equalsIgnoreCase("s"))
                                chatMessage.setMe(false);
                            else
                                chatMessage.setMe(true);



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

        sendBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                /*Uri s= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ChatActivity.this);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("Blumed");
                mBuilder.setContentText("New Message");
                mBuilder.setAutoCancel(true);
                mBuilder.setSound(s);
                mBuilder.setPriority(Notification.PRIORITY_HIGH);

                Intent intent = new Intent(ChatActivity.this, LockScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(ChatActivity.this,0,intent,PendingIntent.FLAG_ONE_SHOT);
                mBuilder.setContentIntent(pendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());*/
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                // now connect with php and pass un, pw to server, server will decide whether correct or not
                if(activeNetworkInfo != null && activeNetworkInfo.isConnected())
                {
                    chatMessage = new ChatMessage();
                    chatMessage.setId(id);
                    chatMessage.setMessage(messageText);
                    rpdata = messageText;
                    try {
                        rpdata = Utility.ServerEncrypt(rpdata, sp.getString("private_key", ""), getIntent().getStringExtra("key"));
                    }catch (Exception e){
                        //Toast.makeText(ChatActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                    }
                    chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setMe(false);
                    id++;
                    messageET.setText("");
                    MyTask t = new MyTask();
                    t.execute();
                }
                else
                {
                    Toast.makeText(ChatActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void findAllViews()
    {
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

    /*private void loadDummyHistory(){

        chatHistory = new ArrayList<ChatMessage>();

        ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setMe(false);
        msg.setMessage("Hi");
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);
        ChatMessage msg1 = new ChatMessage();
        msg1.setId(2);
        msg1.setMe(true);
        msg1.setMessage("How r u doing???");
        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg1);

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        for(int i=0; i<chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }
    }*/
class MyTask extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(ChatActivity.this,s,Toast.LENGTH_SHORT).show();

            if (s!="0") {
                displayMessage(chatMessage);
                //Toast.makeText(ChatActivity.this,"Done yeah",Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                Toast.makeText(ChatActivity.this,"Other User is not logged in",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            RequestPackage rp = new RequestPackage();
            String ans;
            rp.setUri(Utility.serverurl);
            rp.setParam("type", "sendmessage");
            rp.setParam("data", rpdata);
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
}

