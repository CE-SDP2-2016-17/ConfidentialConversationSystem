package com.example.imdhv.blumed;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DisplayContactActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<String> arrlistnames = new ArrayList<String>();
    ArrayList<String> arrlistphonenumbers = new ArrayList<String>();
    ArrayList<String> commonNames=new ArrayList<String>();
    ArrayList<String> commonNumbers=new ArrayList<String>();
    ArrayList<String> commonNames1=new ArrayList<String>();
    ArrayList<String> commonNumbers1=new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);
        lv = (ListView) findViewById(R.id.lv1);
        new MyTask().execute();
    }

    class MyTask extends AsyncTask<String, String, String>{
        ProgressDialog pd;
        String s1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= new ProgressDialog(DisplayContactActivity.this);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.setTitle("Loading...");
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(pd!=null){
                pd.dismiss();
            }
            Toast.makeText(DisplayContactActivity.this, s, Toast.LENGTH_LONG).show();

            try {
                JSONArray arr = new JSONArray(s);
                for(int i = 0; i <arr.length(); i++) {
                    commonNames.add(arrlistnames.get(arr.getInt(i)));
                    commonNumbers.add(arrlistphonenumbers.get(arr.getInt(i)));
                }
                try{
                    SQLiteDatabase database = DisplayContactActivity.this.openOrCreateDatabase("userlists",MODE_PRIVATE,null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS USERS (Name varchar(),Number varchar(10))");
                    int size=commonNames.size();
                    for(int i=0;i<size;i++)
                    {
                        ContentValues cv = new ContentValues();
                        cv.put("Name",commonNames.get(i));
                        cv.put("Number",commonNumbers.get(i));
                        database.insertOrThrow("USERS",null,cv);
                    }
                    Cursor resultSet = database.rawQuery("Select * from USERS",null);
                    if(resultSet.moveToFirst()){
                        do{
                            commonNames1.add(resultSet.getString(0));
                            commonNumbers1.add(resultSet.getString(1));
                        }while (resultSet.moveToNext());
                    }
                }
                catch(Exception e1){
                    Log.e("",e1+"");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> aa = new ArrayAdapter<String>(DisplayContactActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, commonNames1);
            lv.setAdapter(aa);
        }

        @Override
        protected String doInBackground(String... params) {
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
                            // Toast.makeText(this, "Name: " + name
                            //       + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                            //arrlistnames.add(name.replaceAll("\\s+","").trim());
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


            //arrlistphonenumbers.add(Integer.toString(arrlistphonenumbers.size()));
            //arrlistphonenumbers.add(Integer.toString(arrlistnames.size()));
            JSONArray jsArray = new JSONArray(arrlistphonenumbers);
            s1 = jsArray.toString();
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
