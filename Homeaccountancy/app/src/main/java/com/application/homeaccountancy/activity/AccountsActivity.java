package com.application.homeaccountancy.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.CircularArray;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.Adapter.AccountCursorAdapter;
import com.application.homeaccountancy.Data.Adapter.TransactionCursorAdapter;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.R;

public class AccountsActivity extends AppCompatActivity {
    SQLiteHandler sqLiteHandler;
    SQLiteDatabase db;
    Cursor cursor;

    ListView accountsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sqLiteHandler = new SQLiteHandler(getApplicationContext());
        db = sqLiteHandler.getReadableDatabase();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SingleAccountActivity.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        accountsList = (ListView)findViewById(R.id.accounts);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SimpleCursorAdapter accountsCursorAdapter;
        String query = "SELECT " +
                AccountancyContract.Account.TABLE_NAME + "." + AccountancyContract.Account._ID + AccountancyContract.COMMA_SEPARATOR +
                AccountancyContract.Account.COLUMN_NAME_TITLE + AccountancyContract.COMMA_SEPARATOR +
                AccountancyContract.Account.COLUMN_NAME_START_BALANCE + AccountancyContract.COMMA_SEPARATOR +
                "SUM(" + AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + ") as '" +
                AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + "' FROM " +
                AccountancyContract.Account.TABLE_NAME +
                " LEFT JOIN " + AccountancyContract.Transaction.TABLE_NAME + " ON " +
                AccountancyContract.Account.TABLE_NAME + "." + AccountancyContract.Account._ID + "=" +
                AccountancyContract.Transaction.COLUMN_NAME_ACCOUNT_ID +
                " GROUP BY " +
                AccountancyContract.Account.TABLE_NAME + "." + AccountancyContract.Account._ID;

        cursor =  db.rawQuery(query, null);

        String[] from = new String[] {
                AccountancyContract.Account.COLUMN_NAME_TITLE,
                AccountancyContract.Account.COLUMN_NAME_START_BALANCE,
        };

        int[] to = new int[] {
                R.id.account_list_item_title,
                R.id.account_list_item_start,
        };

        accountsCursorAdapter = new AccountCursorAdapter(getApplicationContext(),
                R.layout.account_list_item, cursor, from, to, 0);
        accountsList.setAdapter(accountsCursorAdapter);
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
