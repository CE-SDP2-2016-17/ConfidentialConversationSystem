package com.example.imdhv.blumed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.spec.X509EncodedKeySpec;

import com.example.imdhv.blumed.Utility;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etName, etEmail, etMobile, etUsername, etPassword, passwordrepeat;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findAllViews();
        btnRegister.setOnClickListener(this);
    }

    private void findAllViews() {
        etName = (EditText) findViewById(R.id.etRName);
        etUsername = (EditText) findViewById(R.id.etRUsername);
        etEmail = (EditText) findViewById(R.id.etREmail);
        etMobile = (EditText) findViewById(R.id.etRMobile);
        etPassword = (EditText) findViewById(R.id.etRPassword);
        passwordrepeat = (EditText) findViewById(R.id.passwordrepeat);
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }

    @Override
    public void onClick(View v) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        // now connect with php and pass un, pw to server, server will decide whether correct or not
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected())
        {
            new MyTask().execute();
        }
        else
        {
            Toast.makeText(RegisterActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
    }


    class MyTask extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String un, pw, name, email, mobile, pb_key, pr_key,pwrep;

        KeyPair kp;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= new ProgressDialog(RegisterActivity.this);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.setTitle("Loading...");
            pd.setMessage("Please Wait...");
            pd.show();

            un = etUsername.getText().toString();
            pw = etPassword.getText().toString();
            name = etName.getText().toString();
            email = etEmail.getText().toString();
            mobile = etMobile.getText().toString();
            pwrep = passwordrepeat.getText().toString();
            try {
                kp = Utility.getKeys();
                pb_key=Utility.publicKeyToString(kp.getPublic());
                pr_key=Utility.privateKeyToString(kp.getPrivate());
            }
            catch (Exception e)
            {
                Toast.makeText(RegisterActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(pd!=null){
                pd.dismiss();
            }
            int value = Integer.parseInt(s.trim());
            if(value==1){
                Toast.makeText(RegisterActivity.this, "Registration Successfull !!", Toast.LENGTH_LONG).show();
                finish();

            }else if(value==2)
            {
                Toast.makeText(RegisterActivity.this, "Username already exist !!", Toast.LENGTH_LONG).show();
            }
            else if(value==3)
            {
                Toast.makeText(RegisterActivity.this, "mobile no already exist !!", Toast.LENGTH_LONG).show();
            }
            else if(value==4)
            {
                Toast.makeText(RegisterActivity.this, "email already exist !!", Toast.LENGTH_LONG).show();
            }
            else if(value==5){
                Toast.makeText(RegisterActivity.this, "Username must be atleast 5 characters !!", Toast.LENGTH_LONG).show();
            }
            else if(value==6){
                Toast.makeText(RegisterActivity.this, "Password & repeat password don't match !!", Toast.LENGTH_LONG).show();
            }
            else if(value==7){
                Toast.makeText(RegisterActivity.this, "password is too short use atleast 8 characters !!", Toast.LENGTH_LONG).show();
            }
            else if(value==8){
                Toast.makeText(RegisterActivity.this, "Wrong mobile no !!", Toast.LENGTH_LONG).show();
            }
            else if(value==9){
                Toast.makeText(RegisterActivity.this,"password is too long maximum length is 16 !!",Toast.LENGTH_LONG).show();
            }
            else if(value==10){
                Toast.makeText(RegisterActivity.this,"Enter valid email address",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(RegisterActivity.this, "Registration failed. Please try again !!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(email);
            if(mobile.length()!=10) {
                return "8";
            }
            else if(!m.matches()) {
                return "10";
            }
            else if(un.length()<5) {
                return "5";
            }
            else if(pw.length()<8) {
                return "7";
            }
            else if(!pw.equals(pwrep)){
                return "6";
            }
            else if(pw.length()>16){
                return "9";
            }
            else {
                RequestPackage rp = new RequestPackage();
                rp.setUri(Utility.serverurl);
                rp.setParam("type", "register");
                rp.setParam("un", un);
                rp.setParam("pw", pw);
                rp.setParam("name", name);
                rp.setParam("email", email);
                rp.setParam("mobile", mobile);
                rp.setParam("public_key", pb_key);
                rp.setParam("private_key", pr_key);
                rp.setMethod("POST");
                String ans = HttpManager.getData(rp);
                return ans;
            }
        }
    }
}
