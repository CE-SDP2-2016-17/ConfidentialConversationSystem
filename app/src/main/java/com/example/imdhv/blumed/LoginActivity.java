package com.example.imdhv.blumed;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etUsername,etPassword;
    Button btnLogin;
    TextView tvRegister;
    ArrayList<String> arrlistnames = new ArrayList<String>();
    ArrayList<String> arrlistphonenumbers = new ArrayList<String>();
    ArrayList<String> commonNames=new ArrayList<String>();
    ArrayList<String> commonNumbers=new ArrayList<String>();
    ArrayList<String> commonNames1=new ArrayList<String>();
    ArrayList<String> commonNumbers1=new ArrayList<String>();
    ArrayAdapter<String> aa;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check sharedpreference
         sp = PreferenceManager.getDefaultSharedPreferences(this);

        int caid = sp.getInt("caid", 0);
        if(caid==2){
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
        int value;
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

            if(value!=0){

                handleSecondResponse(s);


                // Obtain SharedPReference object for permenant storage
              sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                // edit it, put an int in it, and apply changes to store it permenantly
                //sp.edit().putString("mobile", etUsername.getText().toString()).apply();

                Utility.createdb(LoginActivity.this);

                Intent i = new Intent(LoginActivity.this, HomeActivity2.class);
                startActivity(i);
                finish();
            }else{
               Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
            }
        }

        private void handleSecondResponse(String s) {
            try {
                JSONArray arr = new JSONArray(s);
                for(int i = 0; i <arr.length(); i++) {
                    commonNames.add(arrlistnames.get(arr.getInt(i)));
                    commonNumbers.add(arrlistphonenumbers.get(arr.getInt(i)));
                }
                try{
                    SQLiteDatabase database = openOrCreateDatabase("/sdcard/userlists.db",SQLiteDatabase.CREATE_IF_NECESSARY,null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS USERS (Name TEXT,Number TEXT);");
                    int size=commonNames.size();
                    for(int i=0;i<size;i++)
                    {
                        ContentValues cv = new ContentValues();
                        cv.put("Name",commonNames.get(i));
                        cv.put("Number",commonNumbers.get(i));
                        database.insertOrThrow("USERS",null,cv);
                    }
                }
                catch(Exception e1){
                    Log.e("",e1+"");
                    Toast.makeText(LoginActivity.this, "ERROR "+e1.toString(), Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Toast.makeText(LoginActivity.this, "ERROR "+e.toString(), Toast.LENGTH_LONG).show();
            }


            sp.edit().putInt("caid",2 ).apply();

        }

        @Override
        protected String doInBackground(String... params) {
            RequestPackage rp = new RequestPackage();
            rp.setUri(Utility.serverurl);
            rp.setParam("type", "login");
            rp.setParam("un", un);
            rp.setParam("pw", pw);
            rp.setMethod("GET");
            rp.setParam("fcmid",sp.getString("fcmid",""));
            String ans = HttpManager.getData(rp);

            Utility.createdb(LoginActivity.this);

            try {
                value = Integer.parseInt(ans.trim());
                if (value > 0) {

                    String newans = getSecondResponse();
                    return newans;
                }
            }catch(Exception ee){
                Toast.makeText(LoginActivity.this,"Login error",Toast.LENGTH_LONG).show();
            }

            return ans;
        }

        public String getSecondResponse(){
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    if (cur.getInt(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));

                            String p = phoneNo.replaceAll("\\s+","").replaceAll("-","").trim();
                            if(p.length() >= 10) {
                                if (!arrlistphonenumbers.contains(p.substring(p.length() - 10))) {
                                    arrlistnames.add(name);
                                    arrlistphonenumbers.add(p.substring(p.length() - 10));
                                }
                            }
                        }
                        pCur.close();
                    }
                }
            }



            JSONArray jsArray = new JSONArray(arrlistphonenumbers);
            String s1 = jsArray.toString();
            RequestPackage rp = new RequestPackage();
            rp.setUri(Utility.serverurl);
            rp.setParam("type", "getuserlist");
            rp.setParam("json1",s1);
            rp.setMethod("POST");
            String ans = HttpManager.getData(rp);
            return ans;
            //return null;
        }
    }
}
