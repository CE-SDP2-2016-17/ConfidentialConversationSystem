package com.example.imdhv.blumed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by imdhv on 07-Mar-17.
 */

public class ChatListAdapter  extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    String searchText = "";
    ArrayList<ChatList> arraylist = new ArrayList<ChatList>();
    private final List<ChatList> mValues;
    //  private final OnListFragmentInteractionListener mListener;
    // ContactFragment cf;
    Context context;


    public ChatListAdapter(List<ChatList> items, Context context) {
        mValues = items;
        this.arraylist.addAll(items);
        // this.cf = cf;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.tvName.setText(mValues.get(position).name);
        String Name = holder.mItem.name.toLowerCase(Locale.getDefault());
        if (Name.contains(searchText)) {

            int startPos = Name.indexOf(searchText);
            int endPos = startPos + searchText.length();

            Spannable spanText = Spannable.Factory.getInstance().newSpannable(holder.tvName.getText()); // <- EDITED: Use the original string, as `country` has been converted to lowercase.
            spanText.setSpan(new BackgroundColorSpan(Color.YELLOW), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.tvName.setText(spanText, TextView.BufferType.SPANNABLE);

        }



    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView tvName;
        public ChatList mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            tvName = (TextView) view.findViewById(R.id.tvNam1);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        MyTask2 task2 = new MyTask2();
                        task2.execute();
                    Intent i = new Intent(context, ChatActivity.class);
                    i.putExtra("number", mItem.number);
                    i.putExtra("name", mItem.name);
                    context.startActivity(i);
                    //Toast.makeText(context,""+mItem.name,Toast.LENGTH_LONG).show();
                }
            });
        }

        class MyTask2 extends AsyncTask<String, String, String> {
            ProgressDialog pd;
            int value;
            @Override
            protected void onPreExecute() {
                try {
                    super.onPreExecute();
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                    sp.edit().putInt("Online",0).apply();
                }catch (Exception e)
                {
                    Log.e("exception contact","In chatlist adapter");
                }
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if(s.length()>0) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                    sp.edit().putString("public_key",s).apply();
                    sp.edit().putInt("Online",1).apply();


                    //Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                    }
                else {
                    //Toast.makeText(context, "user is not logged in", Toast.LENGTH_LONG).show();
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                    sp.edit().putInt("Online",0).apply();
                }
            }
            @Override
            protected String doInBackground(String... params) {
                RequestPackage rp=new RequestPackage();
                String ans="";
                rp.setUri(Utility.serverurl);
                rp.setParam("type","fcmcheck");
                rp.setParam("number",mItem.number);
                rp.setMethod("POST");
                ans = HttpManager.getData(rp);
                    return ans.trim();
            }
        }

        @Override
        public String toString() {
            return "";
        }
    }
    public void filter(String searchText) throws ParseException {
        searchText = searchText.toLowerCase(Locale.getDefault());
        this.searchText = searchText;
        mValues.clear();
        if (searchText.length() == 0) {
            mValues.addAll(arraylist);
        } else {
            for (ChatList m : arraylist) {
                if (m.name.toLowerCase(Locale.getDefault()).contains(searchText) ||
                        m.number.contains(searchText)) {
                    mValues.add(m);
                }
            }
        }
        notifyDataSetChanged();
    }
}
