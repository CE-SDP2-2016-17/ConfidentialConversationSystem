package com.example.imdhv.blumed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView lv;
    ArrayList<String> arrlistnames = new ArrayList<String>();
    ArrayList<String> arrlistids = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        lv = (ListView) findViewById(R.id.lv1);
        lv.setOnItemClickListener(this);
        new MyTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id12) {
        String id = arrlistids.get(position);
        Intent i = new Intent(this, UserDetailActivity.class);
        i.putExtra("extraid", id);
        startActivity(i);

    }

    class MyTask extends AsyncTask<String, String, String>{
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= new ProgressDialog(UserListActivity.this);
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
            //Toast.makeText(UserListActivity.this, s, Toast.LENGTH_LONG).show();
            parseAndShowJSON(s);
        }

        private void parseAndShowJSON(String json) {
            try {
                JSONArray arr = new JSONArray(json);
                for(int i = 0; i <arr.length(); i++){
                    JSONObject obj = arr.getJSONObject(i);
                    String name =  obj.get("name").toString();
                    arrlistnames.add(name);
                    String id =  obj.get("id").toString();
                    arrlistids.add(id);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayAdapter<String> aa = new ArrayAdapter<String>(UserListActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, arrlistnames);
            lv.setAdapter(aa);



        }

        @Override
        protected String doInBackground(String... params) {
            RequestPackage rp = new RequestPackage();
            rp.setUri(Utility.serverurl);
            rp.setParam("type", "getuserlist");
            rp.setMethod("POST");
            String ans = HttpManager.getData(rp);
            return ans;
        }


    }
}
