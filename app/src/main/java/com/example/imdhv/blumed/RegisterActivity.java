package com.example.imdhv.blumed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

    EditText etName, etEmail, etMobile, etUsername, etPassword;
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
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }

    @Override
    public void onClick(View v) {
        new MyTask().execute();
    }


    class MyTask extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String un, pw, name, email, mobile, pb_key, pr_key;
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

            }else{
                Toast.makeText(RegisterActivity.this, "Registration failed. Please try again !!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            RequestPackage rp = new RequestPackage();
            rp.setUri(Utility.serverurl);
            rp.setParam("type", "register");
            rp.setParam("un", un);
            rp.setParam("pw", pw);
            rp.setParam("name", name);
            rp.setParam("email", email);
            rp.setParam("mobile", mobile);
            rp.setParam("public_key",pb_key);
            rp.setParam("private_key",pr_key);


            rp.setMethod("POST");
            String ans = HttpManager.getData(rp);
            return ans;
        }
    }
}
