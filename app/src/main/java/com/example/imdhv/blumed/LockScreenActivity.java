package com.example.imdhv.blumed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        sp= PreferenceManager.getDefaultSharedPreferences(this);
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
        passCode = (EditText)findViewById(R.id.passCode);
        EnterPass = (Button)findViewById(R.id.Enter);
        EnterPass.setOnClickListener(this);
    }

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
