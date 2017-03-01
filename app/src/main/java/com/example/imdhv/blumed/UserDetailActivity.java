package com.example.imdhv.blumed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserDetailActivity extends AppCompatActivity {

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        id = getIntent().getStringExtra("extraid");

    }
}
