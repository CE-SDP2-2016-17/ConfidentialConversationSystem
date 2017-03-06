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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etUsername,etPassword;
    Button btnLogin;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // check sharedpreference
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int userid = sp.getInt("userid", 0);
        if(userid>0){
            Intent i = new Intent(this, HomeActivity2.class);
            startActivity(i);
            finish();
        }
        findAllViews();
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private void findAllViews() {
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
    }

    @Override
    public void onClick(View v) {
        // now connect with php and pass un, pw to server, server will decide whether correct or not
        MyTask t = new MyTask();
        t.execute();
    }
    class MyTask extends AsyncTask<String, String, String>{
        ProgressDialog pd;
        String un, pw;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= new ProgressDialog(LoginActivity.this);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.setTitle("Loading...");
            pd.setMessage("Please Wait...");
            pd.show();

            un = etUsername.getText().toString();
            pw = etPassword.getText().toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(pd!=null){
                pd.dismiss();
            }
            int value = Integer.parseInt(s.trim());
            if(value>0){
                // Obtain SharedPReference object for permenant storage
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                // edit it, put an int in it, and apply changes to store it permenantly
                sp.edit().putInt("userid", value).apply();

                Intent i = new Intent(LoginActivity.this, HomeActivity2.class);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            RequestPackage rp = new RequestPackage();
            rp.setUri(Utility.serverurl);
            rp.setParam("type", "login");
            rp.setParam("un", un);
            rp.setParam("pw", pw);
            rp.setMethod("GET");
            String ans = HttpManager.getData(rp);
            return ans;
        }
    }
}
