package com.example.imdhv.blumed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by imdhv on 26-Feb-17.
 */

public class Utility {
    public static final String serverurl = "https://blumed.000webhostapp.com/androidsupport.php";

    public static void createdb(Context c){
        try{
            SQLiteDatabase database = c.openOrCreateDatabase("/sdcard/userlists.db",SQLiteDatabase.CREATE_IF_NECESSARY,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS USERS (Name TEXT,Number TEXT);");
            database.execSQL("CREATE TABLE IF NOT EXISTS MESSAGE (id integer primary key autoincrement,frommobile TEXT, tomobile text, data text, creationtime text,senderttl int,status text,action text);");
            database.execSQL("CREATE TABLE IF NOT EXISTS CHATLIST (Name TEXT,Number TEXT);");
        }
        catch(Exception e1){
            Log.e("",e1+"");
  //          Toast.makeText(getActivity(), "ERROR "+e1.toString(), Toast.LENGTH_LONG).show();
        }

    }

}
