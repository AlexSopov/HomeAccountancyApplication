package com.application.homeaccountancy;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;

import java.util.Calendar;

public class FilterActivity extends AppCompatActivity {
    TextView fromDateTextView, tillDateTextView;
    CheckBox fromDateCheckBox, tillDateCheckBox, categoryCheckBox, accountCheckBox;
    Spinner categoriesSpinner, accountsSpinner;

    SQLiteHandler handler;
    SQLiteDatabase db;
    Cursor cursor;

    DatePickerDialog.OnDateSetListener onDateFromSetListener, onDateTillSetListener;

    Calendar dateFrom, dateTill;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InitializeViews();

        dateFrom = Calendar.getInstance();
        dateTill = Calendar.getInstance();
        handler = new SQLiteHandler(getApplicationContext());
        db = handler.getReadableDatabase();

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

        fromDateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fromDateTextView.setEnabled(isChecked);
            }
        });

        tillDateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tillDateTextView.setEnabled(isChecked);
            }
        });

        categoryCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                categoriesSpinner.setEnabled(isChecked);
            }
        });

        accountCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                accountsSpinner.setEnabled(isChecked);
            }
        });

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
        fromDateTextView = (TextView) findViewById(R.id.filter_date_from);
        tillDateTextView = (TextView) findViewById(R.id.filter_date_till);
        fromDateCheckBox = (CheckBox) findViewById(R.id.filter_date_from_check);
        tillDateCheckBox = (CheckBox) findViewById(R.id.filter_date_till_check);
        categoryCheckBox = (CheckBox) findViewById(R.id.filter_category_check);
        accountCheckBox = (CheckBox) findViewById(R.id.filter_account_check);
        categoriesSpinner = (Spinner) findViewById(R.id.filter_category);
        accountsSpinner = (Spinner) findViewById(R.id.filter_account);

        fromDateTextView.setEnabled(false);
        tillDateTextView.setEnabled(false);
        categoriesSpinner.setEnabled(false);
        accountsSpinner.setEnabled(false);
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

    private void setInitialDateTime() {
        fromDateTextView.setText(String.format("%td.%tm.%tY", dateFrom, dateFrom, dateFrom));
        tillDateTextView.setText(String.format("%td.%tm.%tY", dateTill, dateTill, dateTill));
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
