package com.application.homeaccountancy;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Trans extends AppCompatActivity {
    TextView currentDateTime;
    Spinner categoriesSpinner;

    SQLiteHandler handler;
    SQLiteDatabase db;
    SimpleCursorAdapter adapter;
    Cursor cursor;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        date = Calendar.getInstance();

        categoriesSpinner = (Spinner)findViewById(R.id.transaction_category);
        currentDateTime = (TextView)findViewById(R.id.transaction_date);
        handler = new SQLiteHandler(getApplicationContext());

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, monthOfYear);
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        currentDateTime.setText(dateFormat.format(date.getTime()));
    }

    public void setDate(View view) {
        new DatePickerDialog(this, onDateSetListener,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)).show();
    }
}
