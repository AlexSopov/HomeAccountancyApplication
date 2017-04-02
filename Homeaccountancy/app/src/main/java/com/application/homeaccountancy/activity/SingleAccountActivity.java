package com.application.homeaccountancy.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.R;

import java.util.Calendar;

public class SingleAccountActivity extends AppCompatActivity {
    TextView titleTextView, startBalanceTextView;
    long accountId;

    SQLiteHandler handler;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_account_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handler = new SQLiteHandler(getApplicationContext());
        db = handler.getReadableDatabase();

        InitializeViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountId = extras.getLong("id");
        }

        if (accountId > 0) {
            cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Account.TABLE_NAME +
                    " WHERE " + AccountancyContract.Account._ID + "=?", new String[] {String.valueOf(accountId)});

            cursor.moveToFirst();
            titleTextView.setText(cursor.getString(cursor.getColumnIndex(AccountancyContract.Account.COLUMN_NAME_TITLE)));
            startBalanceTextView.setText(cursor.getString(cursor.getColumnIndex(AccountancyContract.Account.COLUMN_NAME_START_BALANCE)));
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (db != null)
            db.close();

        if (cursor != null)
            cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (accountId > 0) {
            getMenuInflater().inflate(R.menu.dalete_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(!item.isChecked())
            item.setChecked(true);

        if (id == R.id.delete) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SingleAccountActivity.this);
            dialog
                    .setTitle("Подтверждение действия")
                    .setMessage("Вы действительно хотите удалить счёт? Все записи данного " +
                            "счёта будут стерты.")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.delete(AccountancyContract.Account.TABLE_NAME,
                                    AccountancyContract.Account._ID + "=?",
                                    new String[] {String.valueOf(accountId)}
                            );
                            finish();
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitializeViews() {
        titleTextView = (TextView) findViewById(R.id.account_title);
        startBalanceTextView = (TextView) findViewById(R.id.start_balance);
    }

    public void saveAccount(View view) {
        String title = titleTextView.getText().toString();
        String startBalanceText = startBalanceTextView.getText().toString();


        if (title.isEmpty()){
            Toast toast = Toast.makeText(this, "Название счёта не может быть пустым",Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        if (startBalanceText.isEmpty()){
            Toast toast = Toast.makeText(this, "Необхоимо ввести начальный баланс счёта",Toast.LENGTH_LONG);
            toast.show();
            return;
        }


        double startBalance = Double.parseDouble(startBalanceText);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AccountancyContract.Account.COLUMN_NAME_TITLE, title);
            contentValues.put(AccountancyContract.Account.COLUMN_NAME_START_BALANCE, startBalance);

            if (accountId > 0) {
                db.update(AccountancyContract.Account.TABLE_NAME, contentValues,
                        AccountancyContract.Account._ID + "=?", new String[] {String.valueOf(accountId)});
                finish();
            }
            else {
                db.insertOrThrow(AccountancyContract.Account.TABLE_NAME, null, contentValues);
                finish();
            }
        }
        catch (SQLiteConstraintException exception) {
            Toast toast = Toast.makeText(this, "Счёт с таким именем уже существует", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
