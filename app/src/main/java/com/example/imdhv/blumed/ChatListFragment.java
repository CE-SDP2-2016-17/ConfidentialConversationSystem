package com.example.imdhv.blumed;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ChatListFragment extends Fragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters





    RecyclerView recyclerView;
    EditText et;
    ChatListAdapter aa;
    String number;
    BroadcastReceiver receiver;

    List<ChatList> lists = new ArrayList<ChatList>();

    public ChatListFragment() {

    }


    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(MyFirebaseMessagingService.COPA_RESULT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.lvChats);
        et = (EditText) v.findViewById(R.id.searchbox1);

        SQLiteDatabase database = getActivity().openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int caid = sp.getInt("caid", 0);
        if (caid > 0) {
            Cursor resultSet = database.rawQuery("Select * from CHATLIST", null);
            if (resultSet.moveToFirst()) {
                do {
                    ChatList obj = new ChatList();
                    obj.name = resultSet.getString(0);
                    obj.number = resultSet.getString(1);
                    lists.add(obj);
                } while (resultSet.moveToNext());
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            aa = new ChatListAdapter(lists,getActivity());
            recyclerView.setAdapter(aa);
        }



        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<ChatList> lists1 = new ArrayList<>();
                SQLiteDatabase database = context.openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                int caid = sp.getInt("caid", 0);
                if (caid > 0) {
                    Cursor resultSet = database.rawQuery("Select * from CHATLIST", null);
                    if (resultSet.moveToFirst()) {
                        do {
                            ChatList obj = new ChatList();
                            obj.name = resultSet.getString(0);
                            obj.number = resultSet.getString(1);
                            lists1.add(obj);
                        } while (resultSet.moveToNext());
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    aa = new ChatListAdapter(lists1,getActivity());
                    recyclerView.setAdapter(aa);
                }
            }
        };




        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Listview name of the class

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    aa.filter(et.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // TODO Auto-generated method stub
            }
        });
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        number = (String) parent.getItemAtPosition(position);
        //MyTask t=new MyTask();
        //t.execute();
    }
}