package com.application.homeaccountancy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;

import java.util.Calendar;

public class SingleTransactionActivity extends AppCompatActivity {
    TextView currentDate, currentTime;
    Spinner categoriesSpinner, accountsSpinner;
    EditText transactionSum, note;

    SQLiteHandler handler;
    SQLiteDatabase db;
    Cursor cursor;

    DatePickerDialog.OnDateSetListener onDateSetListener;
    TimePickerDialog.OnTimeSetListener onTimeSetListener;

    Calendar dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_transaction_activity);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoriesSpinner = (Spinner)findViewById(R.id.transaction_category);
        accountsSpinner = (Spinner)findViewById(R.id.transaction_account);
        currentDate = (TextView)findViewById(R.id.transaction_date);
        currentTime = (TextView)findViewById(R.id.transaction_time);
        transactionSum = (EditText)findViewById(R.id.transaction_sum);
        note = (EditText)findViewById(R.id.transaction_note);

        dateTime = Calendar.getInstance();
        handler = new SQLiteHandler(getApplicationContext());
        db = handler.getReadableDatabase();

        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateTime.set(Calendar.MINUTE, minute);
                setInitialDateTime();
            }
        };

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTime.set(Calendar.YEAR, year);
                dateTime.set(Calendar.MONTH, monthOfYear);
                dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime();
            }
        };

        setInitialDateTime();
        InitializeSpinners();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        db.close();
        cursor.close();
    }

    private void setInitialDateTime() {
        currentDate.setText(String.format("%td.%tm.%tY", dateTime, dateTime, dateTime));
        currentTime.setText(String.format("%tH:%tM", dateTime, dateTime));
    }

    private void InitializeSpinners() {
        SimpleCursorAdapter categoriesAdapter, accountsAdapter;

        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Category.TABLE_NAME, null);
        categoriesAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor,
                new String[] {AccountancyContract.Category.COLUMN_NAME_TITLE}, new int[] {android.R.id.text1}, 0);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(categoriesAdapter);


        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Account.TABLE_NAME, null);
        accountsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor,
                new String[] {AccountancyContract.Category.COLUMN_NAME_TITLE}, new int[] {android.R.id.text1}, 0);
        accountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountsSpinner.setAdapter(accountsAdapter);
    }

    public void setDate(View view) {
        new DatePickerDialog(this, onDateSetListener,
                dateTime.get(Calendar.YEAR),
                dateTime.get(Calendar.MONTH),
                dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void setTime(View view) {
        new TimePickerDialog(this, onTimeSetListener,
                dateTime.get(Calendar.HOUR_OF_DAY),
                dateTime.get(Calendar.MINUTE), true).show();
    }

    public void saveTransactionCloseActivity(View view) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AccountancyContract.Transaction.COLUMN_NAME_DATE, getTimeString());
        contentValues.put(AccountancyContract.Transaction.COLUMN_NAME_AMOUNT,
                Integer.parseInt(transactionSum.getText().toString()));

        contentValues.put(AccountancyContract.Transaction.COLUMN_NAME_ACCOUNT_ID, accountsSpinner.getSelectedItemId());
        contentValues.put(AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID, categoriesSpinner.getSelectedItemId());
        contentValues.put(AccountancyContract.Transaction.COLUMN_NAME_NOTE, note.getText().toString());

        db.insert(AccountancyContract.Transaction.TABLE_NAME, null, contentValues);
        finish();
    }

    public void saveTransaction(View view) {
    }

    private String getTimeString() {
        return String.format("%tY-%tm-%td %tH:%tM",
                dateTime, dateTime, dateTime, dateTime, dateTime);
    }
}
