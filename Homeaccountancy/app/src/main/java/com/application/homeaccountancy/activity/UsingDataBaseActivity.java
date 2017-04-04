package com.application.homeaccountancy.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.application.homeaccountancy.Data.SQLiteHandler;

public class UsingDataBaseActivity extends AppCompatActivity {
    SQLiteHandler handler;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new SQLiteHandler(getApplicationContext());
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