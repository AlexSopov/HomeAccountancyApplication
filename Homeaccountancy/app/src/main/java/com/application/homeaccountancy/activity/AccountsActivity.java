package com.application.homeaccountancy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.Adapter.AccountCursorAdapter;
import com.application.homeaccountancy.R;

public class AccountsActivity extends UsingDataBaseActivity {
    ListView accountsList;
    SimpleCursorAdapter accountsCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        String[] from = new String[] {
                AccountancyContract.Account.COLUMN_NAME_TITLE,
                AccountancyContract.Account.COLUMN_NAME_START_BALANCE,
        };

        int[] to = new int[] {
                R.id.account_list_item_title,
                R.id.account_list_item_start,
        };

        setCursor();
        accountsCursorAdapter = new AccountCursorAdapter(getApplicationContext(),
                R.layout.account_list_item, cursor, from, to, 0);
        accountsList.setAdapter(accountsCursorAdapter);

        registerForContextMenu(accountsList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCursor();
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

                                setCursor();
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

    private void setCursor() {
        String query = "SELECT " +
                AccountancyContract.Account.TABLE_NAME + "." + AccountancyContract.Account._ID + AccountancyContract.COMMA +
                AccountancyContract.Account.COLUMN_NAME_TITLE + AccountancyContract.COMMA +
                AccountancyContract.Account.COLUMN_NAME_START_BALANCE + AccountancyContract.COMMA +
                "SUM(" + AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + ") as '" +
                AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + "' FROM " +
                AccountancyContract.Account.TABLE_NAME +
                " LEFT JOIN " + AccountancyContract.Transaction.TABLE_NAME + " ON " +
                AccountancyContract.Account.TABLE_NAME + "." + AccountancyContract.Account._ID + "=" +
                AccountancyContract.Transaction.COLUMN_NAME_ACCOUNT_ID +
                " GROUP BY " +
                AccountancyContract.Account.TABLE_NAME + "." + AccountancyContract.Account._ID;

        cursor =  db.rawQuery(query, null);
    }
}
