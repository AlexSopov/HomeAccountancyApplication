package com.application.homeaccountancy;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;

public class TransactionsFragment extends Fragment {
    ListView transactionsList;
    FloatingActionButton floatingActionButton;

    SQLiteHandler sqLiteHandler;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transactions_content, container, false);

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.add_new_transaction);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTransaction(v);
            }
        });


        transactionsList = (ListView) view.findViewById(R.id.all_transactions_list);
        sqLiteHandler = new SQLiteHandler(getActivity().getApplicationContext());
        db = sqLiteHandler.getReadableDatabase();
        setHasOptionsMenu(true);

        TabHost tabHost = (TabHost) view.findViewById(R.id.tabs_transactions);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tabs_transactions_tab1");
        tabSpec.setIndicator("Все");
        tabSpec.setContent(R.id.tabs_transactions_tab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tabs_transactions_tab2");
        tabSpec.setIndicator("Траты");
        tabSpec.setContent(R.id.tabs_transactions_tab2);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tabs_transactions_tab3");
        tabSpec.setIndicator("Пополнения");
        tabSpec.setContent(R.id.tabs_transactions_tab3);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        return view;
    }

    @Override
    public void onResume() {
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

        transactionsCursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
                R.layout.transaction_list_item, cursor, from, to, 0);
        transactionsList.setAdapter(transactionsCursorAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        db.close();
        cursor.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter) {
            Intent intent = new Intent(getActivity().getApplicationContext(), FilterActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void addNewTransaction(View view) {
        Intent intent = new Intent(getActivity().getApplicationContext(), SingleTransactionActivity.class);
        startActivity(intent);
    }
}
