package com.application.homeaccountancy.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.Adapter.AccountCursorAdapter;
import com.application.homeaccountancy.R;

public class AccountsActivity extends UsingDataBaseActivity {
    private ListView accountsListView;
    private SimpleCursorAdapter accountsCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accountsListView = (ListView)findViewById(R.id.accounts);
        registerForContextMenu(accountsListView);

        FloatingActionButton addNewAccount = (FloatingActionButton) findViewById(R.id.fab);
        addNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SingleAccountActivity.class);
                startActivity(intent);
            }
        });

        initializeAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requeryCursor();
        accountsCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final long id = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).id;

        switch (item.getItemId()) {
            case R.id.change:
                Intent intent = new Intent(getApplicationContext(), SingleAccountActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                return true;
            case R.id.delete:
                AlertDialog.Builder dialog = new AlertDialog.Builder(AccountsActivity.this);
                dialog
                        .setTitle("Подтверждение действия")
                        .setMessage("Вы действительно хотите удалить счёт? Все записи данного " +
                                "счёта будут стерты.")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.delete(AccountancyContract.Account.TABLE_NAME,
                                        AccountancyContract.Account._ID + "=?",
                                        new String[] {String.valueOf(id)}
                                );

                                requeryCursor();
                                accountsCursorAdapter.changeCursor(cursor);
                            }
                        })
                        .setNegativeButton("Нет", null)
                        .create();
                dialog.show();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void initializeAdapter() {
        String[] from = new String[] {
                AccountancyContract.Account.A_TITLE,
                AccountancyContract.Account.START_BALANCE,
        };

        int[] to = new int[] {
                R.id.account_list_item_title,
                R.id.account_list_item_start,
        };

        requeryCursor();
        accountsCursorAdapter = new AccountCursorAdapter(getApplicationContext(),
                R.layout.account_list_item, cursor, from, to, 0);
        accountsListView.setAdapter(accountsCursorAdapter);
    }
    private void requeryCursor() {
        String query = "SELECT " +
                AccountancyContract.Account.TABLE_NAME + "." + AccountancyContract.Account._ID + AccountancyContract.COMMA +
                AccountancyContract.Account.A_TITLE + AccountancyContract.COMMA +
                AccountancyContract.Account.START_BALANCE + AccountancyContract.COMMA +
                "SUM(" + AccountancyContract.Transaction.AMOUNT + ") as '" +
                AccountancyContract.Transaction.AMOUNT + "' FROM " +
                AccountancyContract.Account.TABLE_NAME +
                " LEFT JOIN " + AccountancyContract.Transaction.TABLE_NAME + " ON " +
                AccountancyContract.Account.TABLE_NAME + "." + AccountancyContract.Account._ID + "=" +
                AccountancyContract.Transaction.ACCOUNT_ID +
                " GROUP BY " +
                AccountancyContract.Account.TABLE_NAME + "." + AccountancyContract.Account._ID;

        cursor =  db.rawQuery(query, null);
    }
}