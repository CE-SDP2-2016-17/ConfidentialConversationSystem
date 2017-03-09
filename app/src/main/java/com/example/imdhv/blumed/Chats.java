package com.example.imdhv.blumed;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class Chats extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters


    public Chats() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();

        SQLiteDatabase database = getActivity().openOrCreateDatabase("userlists", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        Cursor resultSet = database.rawQuery("Select * from MESSAGE", null);

        if (resultSet.moveToFirst()) {
            do {
                String a = resultSet.getString(0);
                String ba = resultSet.getString(1);
                String b = resultSet.getString(2);
                String c = resultSet.getString(3);
                String d = resultSet.getString(4);
                String e = resultSet.getString(5);
                String f = resultSet.getString(6);
                Toast.makeText(getActivity(), a + " " + ba + b + c + d + e + f, Toast.LENGTH_LONG).show();
            }
            while (resultSet.moveToNext());



        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chats, container, false);

        SQLiteDatabase database = getActivity().openOrCreateDatabase("userlists", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        Cursor resultSet = database.rawQuery("Select * from MESSAGE", null);

        if (resultSet.moveToFirst()) {
            do {
                String a = resultSet.getString(0);
                String ba = resultSet.getString(1);
                String b = resultSet.getString(2);
                String c = resultSet.getString(3);
                String d = resultSet.getString(4);
                String e = resultSet.getString(5);
                String f = resultSet.getString(6);
                Toast.makeText(getActivity(), a + " " + ba + b + c + d + e + f, Toast.LENGTH_LONG).show();
            }
            while (resultSet.moveToNext());



        }


        return v;
    }



}
