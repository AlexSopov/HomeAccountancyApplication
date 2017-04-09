package com.application.homeaccountancy.Fragment;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.application.homeaccountancy.Data.SQLiteHandler;

public class UsingDataBaseListFragment extends ListFragment {
    protected SQLiteHandler handler;
    protected SQLiteDatabase db;
    protected Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new SQLiteHandler(getActivity().getApplicationContext());
        db = handler.getReadableDatabase();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (db != null)
            db.close();

        if (cursor != null)
            cursor.close();
    }
}
