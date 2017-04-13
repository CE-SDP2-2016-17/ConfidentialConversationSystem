package com.example.imdhv.blumed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSATestActivity extends AppCompatActivity implements View.OnClickListener{
    TextView t1,t2;
    Button b1,b2;
    KeyPair kp;
    PublicKey pb;
    PrivateKey pr;
    String pub,pri;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsatest);
        t1=(TextView)findViewById(R.id.textView14);
        t2=(TextView)findViewById(R.id.textView15);
        b1=(Button)findViewById(R.id.button3);
        b2=(Button)findViewById(R.id.button4);
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        try{
            kp=Utility.getKeys();
            pb=kp.getPublic();
            pr=kp.getPrivate();
            pub=Utility.publicKeyToString(pb);
            pri=sp.getString("private_key","");
        }
        catch (Exception e1)
        {

        }
        b1.setOnClickListener(this);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String s = t2.getText().toString();
                    String s2 = Utility.ServerDecrypt(s, pri);
                    t2.setText(s2);
                }catch (Exception e1)
                {
                    Toast.makeText(RSATestActivity.this,e1.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
        }
    class MyTask extends AsyncTask<String,String,String> {
        ProgressDialog pd;
        String un, pw;
        int value;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= new ProgressDialog(RSATestActivity.this);
            pd.setIndeterminate(true);
            pd.setCancelable(true);
            pd.setTitle("Loading...");
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pd != null) pd.dismiss();
            try {
                String s1 = t1.getText().toString();
                String s2 = Utility.ServerEncrypt(s1, s);
                t2.setText(s2);
            }catch (Exception e)
            {

            }
        }

        @Override
        protected String doInBackground(String... params) {
            un=sp.getString("username","");
            RequestPackage rp = new RequestPackage();
            String ans;
            rp.setUri(Utility.serverurl);
            rp.setParam("type", "fcmcheck");
            rp.setParam("number","8469623054");
            rp.setMethod("POST");
            ans = HttpManager.getData(rp);
            return ans;
        }
    }

    @Override
    public void onClick(View v)
    {
        String d="";
        try {
            MyTask t=new MyTask();
            t.execute();
        }catch (Exception e1)
        {
            Toast.makeText(RSATestActivity.this,d,Toast.LENGTH_LONG).show();
        }
    }
}
