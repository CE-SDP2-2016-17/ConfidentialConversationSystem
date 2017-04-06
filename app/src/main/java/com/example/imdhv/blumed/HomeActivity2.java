package com.example.imdhv.blumed;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.WindowManager;
import android.widget.Toast;

import java.util.Date;

public class HomeActivity2 extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_home2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }
    @Override
    protected void onRestart() {

        // TODO Auto-generated method stub
        super.onRestart();
        finish();
        Intent i= new Intent(this,HomeActivity2.class);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    class MyTask extends AsyncTask<String,String,String> {
        ProgressDialog pd;
        String un, pw;
        int value;
        @Override
        protected void onPreExecute() {
                super.onPreExecute();
                pd= new ProgressDialog(HomeActivity2.this);
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.setTitle("Loading...");
                pd.setMessage("Please Wait...");
                pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(HomeActivity2.this,"Logout Successful",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(HomeActivity2.this);
            un=sp.getString("username","");
            RequestPackage rp = new RequestPackage();
            String ans;
            rp.setUri(Utility.serverurl);
            rp.setParam("type", "logout");
            rp.setParam("un",un);
            rp.setMethod("POST");
            ans = HttpManager.getData(rp);
            return ans;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_logout:

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.edit().putInt("userid", 0).apply();
                sp.edit().putInt("caid", 0).apply();
                SQLiteDatabase database = HomeActivity2.this.openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                database.execSQL("delete from USERS");
                database.execSQL("delete from MESSAGE");
                MyTask t=new MyTask();
                t.execute();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.menuitem_settings:
                  Intent i2 = new Intent(this, CustomSettings.class);
                     startActivity(i2);

                break;
            case R.id.menuitem_change_passcode:
                Intent i3 = new Intent(this,ChangePasscode.class);
                startActivity(i3);
                break;

            case R.id.menuitem_clear_chats:
                SQLiteDatabase database1 = HomeActivity2.this.openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                database1.execSQL("delete from CHATLIST");
                database1.execSQL("delete from MESSAGE");
                Toast.makeText(HomeActivity2.this,"All Chats are cleared",Toast.LENGTH_LONG).show();
                Intent i1 = new Intent(this, HomeActivity2.class);
                finish();
                startActivity(i1);

            default:
                break;

        }
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if(position==0){
                return new ChatListFragment();
            }
            else if(position==1){
                return new ContactFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Chats";
                case 1:
                    return "Contacts";
            }
            return null;
        }
    }
}
