package com.application.homeaccountancy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SingleTransactionActivity extends AppCompatActivity {
    TextView currentDate, currentTime;
    Spinner categoriesSpinner;

    SQLiteHandler handler;
    SQLiteDatabase db;
    SimpleCursorAdapter adapter;
    Cursor cursor;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    TimePickerDialog.OnTimeSetListener onTimeSetListener;

    Calendar dateTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_transaction_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoriesSpinner = (Spinner)findViewById(R.id.transaction_category);
        currentDate = (TextView)findViewById(R.id.transaction_date);
        currentTime = (TextView)findViewById(R.id.transaction_time);

        dateTime = Calendar.getInstance();
        handler = new SQLiteHandler(getApplicationContext());

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = handler.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Category.TABLE_NAME, null);

        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor,
                new String[] {AccountancyContract.Category.COLUMN_NAME_TITLE}, new int[] {android.R.id.text1}, 0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(adapter);
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
}
