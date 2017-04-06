package com.example.imdhv.blumed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
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

public class ContactAdapter  extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    String searchText = "";
    ArrayList<UserList> arraylist = new ArrayList<>();
    private final List<UserList> mValues;
    //  private final OnListFragmentInteractionListener mListener;
   // ContactFragment cf;
    Context context;

    public ContactAdapter(List<UserList> items, Context context) {
        mValues = items;
        this.arraylist.addAll(items);
       // this.cf = cf;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact_item, parent, false);
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
        public UserList mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            tvName = (TextView) view.findViewById(R.id.tvNam);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MyTask2 task2 = new MyTask2();
                    task2.execute();

                    //Toast.makeText(context,""+mItem.number,Toast.LENGTH_LONG).show();



                        //database.execSQL("CREATE TABLE IF NOT EXISTS CHATLIST (Name TEXT,Number TEXT);");
                        //ContentValues cv = new ContentValues();
                        //cv.put("Name", mItem.name);
                        //cv.put("Number",mItem.number);
                        //database.insertOrThrow("CHATLIST", null, cv);
                        //Intent i = new Intent(context,ChatActivity.class);
                        //i.putExtra("number", mItem.number);
                        //context.startActivity(i);

                }
            });
        }

        class MyTask2 extends AsyncTask<String, String, String> {
            ProgressDialog pd;
            int value;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd= new ProgressDialog(context);
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.setTitle("Loading...");
                pd.setMessage("Please Wait...");
                pd.show();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(!s.isEmpty()) {

                    SQLiteDatabase database = context.openOrCreateDatabase("/sdcard/userlists.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                    Cursor resultSet = database.rawQuery("Select * from CHATLIST WHERE Number = '" + mItem.number + "'", null);
                    if(resultSet.moveToFirst()) {
                    }
                    else
                    {
                        database.execSQL("CREATE TABLE IF NOT EXISTS CHATLIST (Name TEXT,Number TEXT);");
                        ContentValues cv = new ContentValues();
                        cv.put("Name", mItem.name);
                        cv.put("Number",mItem.number);
                        database.insertOrThrow("CHATLIST", null, cv);
                        //((Activity)context).finish();
                    }
                    Intent i = new Intent(context, ChatActivity.class);
                    i.putExtra("number", mItem.number);
                    i.putExtra("name", mItem.name);
                    i.putExtra("key", s);
                    //Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                    context.startActivity(i);
                }
                else {
                    Intent i = new Intent(context, ChatActivity.class);
                    i.putExtra("number", mItem.number);
                    i.putExtra("name", mItem.name);
                    context.startActivity(i);
                    Toast.makeText(context, "user is not logged in", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            protected String doInBackground(String... params) {
                RequestPackage rp=new RequestPackage();
                String ans;
                rp.setUri(Utility.serverurl);
                rp.setParam("type","fcmcheck");
                rp.setParam("number",mItem.number);
                rp.setMethod("POST");
                ans = HttpManager.getData(rp);
                return ans;
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
            for (UserList m : arraylist) {
                if (m.name.toLowerCase(Locale.getDefault()).contains(searchText) ||
                        m.number.contains(searchText)) {
                    mValues.add(m);
                }
            }
        }
        notifyDataSetChanged();
    }
}
