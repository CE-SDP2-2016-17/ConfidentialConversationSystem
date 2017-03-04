package com.example.imdhv.blumed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuitem_logout:

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.edit().putInt("userid", 0).apply();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.menuitem_settings:
              //  Intent i2 = new Intent(this, SettingsActivity.class);
               // startActivity(i2);

                break;
            default:
                break;

        }


        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnShowUsers = (Button) findViewById(R.id.btnShowUsers);
        btnShowUsers.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        //Intent i =  new Intent(this, UserListActivity.class);
        Intent i =  new Intent(this, DisplayContactActivity.class);
        startActivity(i);
    }


}
