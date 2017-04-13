package com.example.imdhv.blumed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LockScreenActivity extends AppCompatActivity implements View.OnClickListener{

    EditText passCode;
    Button EnterPass;
    SharedPreferences sp;
    int pass=1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        passCode = (EditText)findViewById(R.id.passCode);
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        int caid;
        caid = sp.getInt("caid",0);
        if(caid==0)
        {
            Intent i1=new Intent(LockScreenActivity.this,LoginActivity.class);
            startActivity(i1);
            finish();
        }
        else{
        int pCode;
        pCode=sp.getInt("passCode",0);
        if(pCode!=0)
        {
            pass=pCode;
            //Toast.makeText(this,""+pass,Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this,"Initial passcode is 1234",Toast.LENGTH_LONG).show();
        }
        passCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(passCode.getText().toString().isEmpty())
                    {
                        Toast.makeText(LockScreenActivity.this,"Enter Passcode",Toast.LENGTH_LONG).show();
                    }
                    else
                    if(Integer.parseInt(passCode.getText().toString())==pass){
                        Intent i=new Intent(LockScreenActivity.this,LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(LockScreenActivity.this,"Wrong passcode",Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });
        EnterPass = (Button)findViewById(R.id.Enter);
        EnterPass.setOnClickListener(this);
    }}

    @Override
    public void onClick(View v) {
        if(passCode.getText().toString().isEmpty())
        {
            Toast.makeText(this,"Enter Passcode",Toast.LENGTH_LONG).show();
        }
        else
        if(Integer.parseInt(passCode.getText().toString())==pass){
            Intent i=new Intent(LockScreenActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
            Toast.makeText(this,"Wrong passcode",Toast.LENGTH_LONG).show();
        }
    }
}
