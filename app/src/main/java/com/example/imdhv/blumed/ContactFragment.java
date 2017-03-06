package com.example.imdhv.blumed;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {


    ListView lv;
    EditText et;
    ArrayList<String> arrlistnames = new ArrayList<String>();
    ArrayList<String> arrlistphonenumbers = new ArrayList<String>();
    ArrayList<String> commonNames=new ArrayList<String>();
    ArrayList<String> commonNumbers=new ArrayList<String>();
    ArrayList<String> commonNames1=new ArrayList<String>();
    ArrayList<String> commonNumbers1=new ArrayList<String>();
    ArrayAdapter<String> aa;



    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact, container, false);

        lv = (ListView) v.findViewById(R.id.lvContacts);
        et = (EditText) v.findViewById(R.id.searchbox);
        aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, commonNames1);
        SQLiteDatabase database = getActivity().openOrCreateDatabase("userlists",SQLiteDatabase.CREATE_IF_NECESSARY,null);
        //database.execSQL("delete from USERS");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int caid = sp.getInt("caid", 0);
        if(caid>0){
            Cursor resultSet = database.rawQuery("Select * from USERS",null);
            if(resultSet.moveToFirst()){
                do{
                    commonNames1.add(resultSet.getString(0));
                    commonNumbers1.add(resultSet.getString(1));
                }while (resultSet.moveToNext());
            }
            aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, commonNames1);
            lv.setAdapter(aa);

        }
        else {
            new MyTask().execute();
        }




        return v;
    }




    class MyTask extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String s1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= new ProgressDialog(getActivity());
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
            //Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();

            try {
                JSONArray arr = new JSONArray(s);
                for(int i = 0; i <arr.length(); i++) {
                    commonNames.add(arrlistnames.get(arr.getInt(i)));
                    commonNumbers.add(arrlistphonenumbers.get(arr.getInt(i)));
                }
                try{
                    SQLiteDatabase database = getActivity().openOrCreateDatabase("userlists",SQLiteDatabase.CREATE_IF_NECESSARY,null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS USERS (Name TEXT,Number TEXT);");
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
                    Toast.makeText(getActivity(), "ERROR "+e1.toString(), Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Toast.makeText(getActivity(), "ERROR "+e.toString(), Toast.LENGTH_LONG).show();
            }

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sp.edit().putInt("caid",2 ).apply();




            ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, commonNames1);
            lv.setAdapter(aa);
        }

        @Override
        protected String doInBackground(String... params) {
            ContentResolver cr = getActivity().getContentResolver();
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