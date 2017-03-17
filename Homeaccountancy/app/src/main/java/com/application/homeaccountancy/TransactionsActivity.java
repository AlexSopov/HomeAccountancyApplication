package com.application.homeaccountancy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.Toast;

import com.application.homeaccountancy.Data.AccountancyContract;
//import com.application.homeaccountancy.Data.Adapter.TransactionAdapter;
import com.application.homeaccountancy.Data.Entity.Transaction;
import com.application.homeaccountancy.Data.SQLiteHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionsActivity extends IncludeMenuActivity {
    ListView transactionsList;

    SQLiteHandler sqLiteHandler;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.transactions_activity);
        setTitle("Транзакции");

        transactionsList = (ListView) findViewById(R.id.all_transactions_list);
        sqLiteHandler = new SQLiteHandler(getApplicationContext());
        db = sqLiteHandler.getReadableDatabase();

        TabHost tabHost = (TabHost) findViewById(R.id.tabs_transactions);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tabs_transactions_tab1");
        tabSpec.setIndicator("Все");
        tabSpec.setContent(R.id.tabs_transactions_tab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tabs_transactions_tab2");
        tabSpec.setIndicator("Пополнения");
        tabSpec.setContent(R.id.tabs_transactions_tab2);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tabs_transactions_tab3");
        tabSpec.setIndicator("Затраты");
        tabSpec.setContent(R.id.tabs_transactions_tab3);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SimpleCursorAdapter transactionsCursorAdapter;

        String query = "SELECT " + AccountancyContract.Transaction.TABLE_NAME + "." +
                AccountancyContract.Transaction._ID + AccountancyContract.COMMA_SEPARATOR +
                AccountancyContract.Category.COLUMN_NAME_ICON + AccountancyContract.COMMA_SEPARATOR +
                AccountancyContract.Category.COLUMN_NAME_TITLE + AccountancyContract.COMMA_SEPARATOR +
                "strftime('%d.%m.%Y %H:%M'" + AccountancyContract.COMMA_SEPARATOR +
                AccountancyContract.Transaction.COLUMN_NAME_DATE  + ") as " +
                "'" + AccountancyContract.Transaction.COLUMN_NAME_DATE + "'" + AccountancyContract.COMMA_SEPARATOR +
                AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + AccountancyContract.COMMA_SEPARATOR +
                AccountancyContract.Transaction.COLUMN_NAME_NOTE +
                " FROM " + AccountancyContract.Transaction.TABLE_NAME +
                " INNER JOIN " + AccountancyContract.Category.TABLE_NAME + " " +
                " ON " + AccountancyContract.Category.TABLE_NAME + "." + AccountancyContract.Category._ID + " = " +
                AccountancyContract.Transaction.TABLE_NAME + "." +
                AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID;


        cursor =  db.rawQuery(query, null);

        String[] from = new String[] {
                AccountancyContract.Category.COLUMN_NAME_ICON,
                AccountancyContract.Category.COLUMN_NAME_TITLE,
                AccountancyContract.Transaction.COLUMN_NAME_DATE,
                AccountancyContract.Transaction.COLUMN_NAME_AMOUNT,
                AccountancyContract.Transaction.COLUMN_NAME_NOTE
        };

        int[] to = new int[] {
                R.id.transaction_list_item_icon,
                R.id.transaction_list_item_category,
                R.id.transaction_list_item_date,
                R.id.transaction_list_item_sum,
                R.id.transaction_list_item_description
        };

        transactionsCursorAdapter = new SimpleCursorAdapter(this, R.layout.transaction_list_item, cursor, from, to, 0);
        transactionsList.setAdapter(transactionsCursorAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        db.close();
        cursor.close();
    }

    /*private void setInitialData(){
        Date d = new Date();
        long f = d.getTime();
        for (int i = 0; i < 5; i++) {
            transactions.add(new Transaction(i * 100, f,
                    "Description dfsafsd fdsa afsdaf fdsaf fdas " + i, R.drawable.auto));
            transactions.add(new Transaction(i * 100, f,
                    "Description dfsafsd afsdaf fdsaf fdas " + i, R.drawable.food));
            transactions.add(new Transaction(i * 100, f,
                    "Description dfsafsd afsdaf fdsaf fdas " + i, R.drawable.house));
            transactions.add(new Transaction(i * 100, f,
                    "Descrip fdsa tion dfsafsdafsdaf fdsaf fdas " + i, R.drawable.fuel));
            transactions.add(new Transaction(i * 100, f,
                    "Description dfsafsdafsdaf fdsaf fdas " + i, R.drawable.debt));
        }
    }*/

    public void addNewTransaction(View view) {
        Intent intent = new Intent(TransactionsActivity.this, SingleTransactionActivity.class);
        startActivity(intent);
    }
}
