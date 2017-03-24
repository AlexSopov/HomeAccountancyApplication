package com.application.homeaccountancy.activity;

import android.app.DatePickerDialog;
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

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.R;

import java.util.Calendar;

public class GraphicActivity extends AppCompatActivity {
    TextView fromDateTextView, tillDateTextView;
    Spinner categoriesSpinner;

    SQLiteHandler handler;
    SQLiteDatabase db;
    Cursor cursor;

    DatePickerDialog.OnDateSetListener onDateFromSetListener, onDateTillSetListener;
    public static Calendar dateFrom, dateTill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphic_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handler = new SQLiteHandler(getApplicationContext());
        db = handler.getReadableDatabase();

        dateTill = Calendar.getInstance();
        dateFrom = Calendar.getInstance();
        dateFrom.set(Calendar.DAY_OF_YEAR, -30);

        InitializeViews();
        InitializeListeners();
        InitializeSpinners();

        setInitialDateTime();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        db.close();
        cursor.close();
    }

    private void InitializeViews() {
        fromDateTextView = (TextView) findViewById(R.id.graph_date_from);
        tillDateTextView = (TextView) findViewById(R.id.graph_date_till);
        //categoriesSpinner = (Spinner) findViewById(R.id.filter_category);
    }
    private void InitializeSpinners() {
        /*SimpleCursorAdapter categoriesAdapter, accountsAdapter;

        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Category.TABLE_NAME, null);
        categoriesAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor,
                new String[] {AccountancyContract.Category.COLUMN_NAME_TITLE}, new int[] {android.R.id.text1}, 0);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(categoriesAdapter);*/
    }
    private void InitializeListeners() {
        onDateFromSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateFrom.set(Calendar.YEAR, year);
                dateFrom.set(Calendar.MONTH, monthOfYear);
                dateFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime();
            }
        };

        onDateTillSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTill.set(Calendar.YEAR, year);
                dateTill.set(Calendar.MONTH, monthOfYear);
                dateTill.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime();
            }
        };

        fromDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateFrom(v);
            }
        });
        tillDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateTill(v);
            }
        });
    }

    private void setInitialDateTime() {
        fromDateTextView.setText(String.format("%td.%tm.%tY",
                dateFrom, dateFrom, dateFrom));
        tillDateTextView.setText(String.format("%td.%tm.%tY",
                dateTill, dateTill, dateTill));
    }

    public void setDateFrom(View view) {
        new DatePickerDialog(this, onDateFromSetListener,
                dateFrom.get(Calendar.YEAR),
                dateFrom.get(Calendar.MONTH),
                dateFrom.get(Calendar.DAY_OF_MONTH)).show();
    }
    public void setDateTill(View view) {
        new DatePickerDialog(this, onDateTillSetListener,
                dateTill.get(Calendar.YEAR),
                dateTill.get(Calendar.MONTH),
                dateTill.get(Calendar.DAY_OF_MONTH)).show();
    }
}
