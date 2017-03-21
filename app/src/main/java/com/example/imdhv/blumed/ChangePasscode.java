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

public class ChangePasscode extends AppCompatActivity implements View.OnClickListener{

    EditText oldPass,newPass,newPassRepeat;
    Button submitPassCode;
    int oldpass1;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passcode);
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        oldPass=(EditText)findViewById(R.id.oldPass);
        newPass=(EditText)findViewById(R.id.newPass);
        newPassRepeat=(EditText)findViewById(R.id.newPassRepeat);
        submitPassCode=(Button)findViewById(R.id.submitPasscode);
        oldpass1=sp.getInt("passCode",0);
        submitPassCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (oldpass1==0)
        {
            oldpass1=1234;
        }
        if (oldpass1==Integer.parseInt(oldPass.getText().toString()))
        {
            if(Integer.parseInt(newPass.getText().toString())==Integer.parseInt(newPassRepeat.getText().toString()))
            {
                if(newPass.getText().toString().length()>8)
                {
                    Toast.makeText(this,"maximum length of passcode is 8",Toast.LENGTH_LONG).show();
                }
                else if(newPass.getText().toString().length()<4)
                {
                    Toast.makeText(this,"minimum length of passcode is 4",Toast.LENGTH_LONG).show();
                }
                else
                {
                        sp.edit().putInt("passCode",Integer.parseInt(newPass.getText().toString())).apply();
                        Toast.makeText(this,"passcode changed",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(this,HomeActivity2.class);
                    startActivity(i);
                }
            }
            else{
                Toast.makeText(this,"Repeat passcode does not match",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(this,"Wrong passcode",Toast.LENGTH_LONG).show();
        }
    }
}
