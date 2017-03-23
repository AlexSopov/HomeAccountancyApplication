package com.application.homeaccountancy.activity;

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
import com.application.homeaccountancy.FilterSettings;
import com.application.homeaccountancy.R;

import java.util.Calendar;

public class FilterActivity extends AppCompatActivity {
    TextView fromDateTextView, tillDateTextView;
    CheckBox fromDateCheckBox, tillDateCheckBox, categoryCheckBox, accountCheckBox;
    Spinner categoriesSpinner, accountsSpinner;

    SQLiteHandler handler;
    SQLiteDatabase db;
    Cursor cursor;

    static boolean isFirst = true;
    DatePickerDialog.OnDateSetListener onDateFromSetListener, onDateTillSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handler = new SQLiteHandler(getApplicationContext());
        db = handler.getReadableDatabase();

        InitializeViews();
        InitializeListeners();
        InitializeSpinners();

        ResetState();
        setInitialDateTime();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        db.close();
        cursor.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SaveData();
    }

    private void ResetState() {
        if (isFirst) {
            FilterSettings.dateFrom = Calendar.getInstance();
            FilterSettings.dateTill = Calendar.getInstance();
            isFirst = false;
        }
        else
        {
            fromDateCheckBox.setChecked(FilterSettings.fromDateCheckBoxChecked);
            tillDateCheckBox.setChecked(FilterSettings.tillDateCheckBoxChecked);
            categoryCheckBox.setChecked(FilterSettings.categoryCheckBoxChecked);
            accountCheckBox.setChecked(FilterSettings.accountCheckBoxChecked);
            fromDateTextView.setEnabled(FilterSettings.fromDateCheckBoxChecked);
            tillDateTextView.setEnabled(FilterSettings.tillDateCheckBoxChecked);
            categoriesSpinner.setEnabled(FilterSettings.categoryCheckBoxChecked);
            accountsSpinner.setEnabled(FilterSettings.accountCheckBoxChecked);
        }
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


        for (int i = 0; i < categoriesAdapter.getCount() && FilterSettings.categoryID > 0; i++) {
            if (categoriesAdapter.getItemId(i) == FilterSettings.categoryID) {
                categoriesSpinner.setSelection(i);
                break;
            }
        }

        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Account.TABLE_NAME, null);
        accountsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor,
                new String[] {AccountancyContract.Category.COLUMN_NAME_TITLE}, new int[] {android.R.id.text1}, 0);
        accountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountsSpinner.setAdapter(accountsAdapter);

        for (int i = 0; i < accountsAdapter.getCount() && FilterSettings.accountID > 0; i++) {
            if (accountsAdapter.getItemId(i) == FilterSettings.accountID) {
                accountsSpinner.setSelection(i);
                break;
            }
        }
    }
    private void InitializeListeners() {
        onDateFromSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                FilterSettings.dateFrom.set(Calendar.YEAR, year);
                FilterSettings.dateFrom.set(Calendar.MONTH, monthOfYear);
                FilterSettings.dateFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime();
            }
        };

        onDateTillSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                FilterSettings.dateTill.set(Calendar.YEAR, year);
                FilterSettings.dateTill.set(Calendar.MONTH, monthOfYear);
                FilterSettings.dateTill.set(Calendar.DAY_OF_MONTH, dayOfMonth);
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
                FilterSettings.fromDateCheckBoxChecked = isChecked;
            }
        });

        tillDateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tillDateTextView.setEnabled(isChecked);
                FilterSettings.tillDateCheckBoxChecked = isChecked;
            }
        });

        categoryCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                categoriesSpinner.setEnabled(isChecked);
                FilterSettings.categoryCheckBoxChecked = isChecked;
            }
        });

        accountCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                accountsSpinner.setEnabled(isChecked);
                FilterSettings.accountCheckBoxChecked = isChecked;
            }
        });
    }

    private void setInitialDateTime() {
        fromDateTextView.setText(String.format("%td.%tm.%tY",
                FilterSettings.dateFrom, FilterSettings.dateFrom, FilterSettings.dateFrom));
        tillDateTextView.setText(String.format("%td.%tm.%tY",
                FilterSettings.dateTill, FilterSettings.dateTill, FilterSettings.dateTill));
    }

    public void setDateFrom(View view) {
        new DatePickerDialog(this, onDateFromSetListener,
                FilterSettings.dateFrom.get(Calendar.YEAR),
                FilterSettings.dateFrom.get(Calendar.MONTH),
                FilterSettings.dateFrom.get(Calendar.DAY_OF_MONTH)).show();
    }
    public void setDateTill(View view) {
        new DatePickerDialog(this, onDateTillSetListener,
                FilterSettings.dateTill.get(Calendar.YEAR),
                FilterSettings.dateTill.get(Calendar.MONTH),
                FilterSettings.dateTill.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void SaveData() {
        FilterSettings.categoryID = categoriesSpinner.getSelectedItemId();
        FilterSettings.accountID = accountsSpinner.getSelectedItemId();

        FilterSettings.isFilter = (FilterSettings.fromDateCheckBoxChecked || FilterSettings.accountCheckBoxChecked ||
                FilterSettings.categoryCheckBoxChecked || FilterSettings.tillDateCheckBoxChecked);
    }
    public void saveFilter(View view) {
        SaveData();
        finish();
    }
}