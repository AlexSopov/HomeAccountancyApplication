package com.application.homeaccountancy.activity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;

import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PieCharActivity extends AppCompatActivity {
    PieChart pieChart;

    TextView fromDateTextView, tillDateTextView, categoryTextView,
            totalTextView, percentTextView;
    Spinner categoriesSpinner;

    SQLiteHandler handler;
    SQLiteDatabase db;
    Cursor cursor;

    DatePickerDialog.OnDateSetListener onDateFromSetListener, onDateTillSetListener;
    public static Calendar dateFrom, dateTill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_char_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handler = new SQLiteHandler(getApplicationContext());
        db = handler.getReadableDatabase();

        dateTill = Calendar.getInstance();
        dateFrom = Calendar.getInstance();
        dateFrom.set(Calendar.DAY_OF_YEAR, dateTill.get(Calendar.DAY_OF_YEAR) - 30);

        InitializeViews();
        InitializeListeners();
        InitializeGraphics();

        pieChart.setNoDataText("Не найдено данные для отображения.");
        pieChart.setNoDataTextColor(Color.BLACK);
        setInitialDateTime();
    }

    private void InitializeGraphics() {
        List<PieEntry> entries = new ArrayList<>();
        final int totalSum;

        String fromTime = String.format("%tY-%tm-%td", dateFrom, dateFrom, dateFrom);
        String tillTime = String.format("%tY-%tm-%td", dateTill, dateTill, dateTill);
        int isOuntgo = categoriesSpinner.getSelectedItemPosition() == 0 ? 1 : 0;

        String fromJoinSelector = " FROM " + AccountancyContract.Transaction.TABLE_NAME + " INNER JOIN " +
                AccountancyContract.Category.TABLE_NAME + " ON " + AccountancyContract.Category.TABLE_NAME +
                "." + AccountancyContract.Category._ID + " = " + AccountancyContract.Transaction.TABLE_NAME + "." +
                AccountancyContract.Transaction._ID ;

        String whereSelector = " WHERE " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " >= '" +
                fromTime + "' AND " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " <= '" +
                tillTime + "'" + " AND " + AccountancyContract.Category.COLUMN_NAME_IS_OUTGO + " = " +
                isOuntgo;

        String query = "SELECT SUM(" + AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + ") " +
                fromJoinSelector + whereSelector;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            totalSum = cursor.getInt(0);
            if (totalSum == 0)
                return;
        }
        else {
            return;
        }

        query = "SELECT " +
                AccountancyContract.Category.COLUMN_NAME_TITLE + AccountancyContract.COMMA_SEPARATOR +
                " SUM (" + AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + ") " +
                fromJoinSelector + whereSelector +
                " GROUP BY " + AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID;

        cursor = db.rawQuery(query, null);

        int currentSum = 0;
        List<String> a = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                currentSum = cursor.getInt(1);
                if (currentSum != 0) {
                    entries.add(new PieEntry(currentSum, cursor.getString(0)));
                    a.add(cursor.getString(0));
                }
            }
            while (cursor.moveToNext());
        }
        else {
            return;
        }

        PieDataSet set = new PieDataSet(entries, "Entries");
        set.setValueTextColor(Color.argb(180, 0, 0, 0));
        set.setValueTextSize(14);

        int[] colors = new int[] {Color.parseColor("#e53935"), Color.parseColor("#26C6DA"),  Color.parseColor("#FFCA28"),
                Color.parseColor("#EC407A"),  Color.parseColor("#26A69A"),  Color.parseColor("#FFA726"),
                Color.parseColor("#AB47BC"),  Color.parseColor("#66BB6A"),  Color.parseColor("#FF7043"),
                Color.parseColor("#7E57C2"),  Color.parseColor("#9CCC65"),  Color.parseColor("#8D6E63"),
                Color.parseColor("#5C6BC0"),  Color.parseColor("#D4E157"),  Color.parseColor("#BDBDBD"),
                Color.parseColor("#42A5F5"),  Color.parseColor("#FFEE58"),  Color.parseColor("#78909C"),
                Color.parseColor("#29B6F6")};
        set.setColors(colors);
        set.setDrawValues(true);
        set.setHighlightEnabled(true);


        Legend l = pieChart.getLegend();
        l.setEnabled(false);


        PieData data = new PieData(set);
        pieChart.setCenterText("Итого:\n " + String.valueOf(totalSum));
        pieChart.setCenterTextSize(20);
        pieChart.setHoleRadius(30);
        pieChart.getContentDescription();
        Description description = pieChart.getDescription();
        description.setEnabled(false);
        pieChart.setTransparentCircleRadius(35);
        pieChart.setData(data);


        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                categoryTextView.setText(((PieEntry)e).getLabel());
                totalTextView.setText("Сумма: " + String.valueOf(e.getY()));
                percentTextView.setText(String.format("Процент: %.2f%%", Math.abs((e.getY() * 100) / totalSum)));
            }

            @Override
            public void onNothingSelected() {
                categoryTextView.setText("Ничего не выбрано");
                totalTextView.setText("Сумма: не определено");
                percentTextView.setText("Процент: не определено");
            }
        });

        pieChart.invalidate();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (db != null)
            db.close();

        if (cursor != null)
            cursor.close();
    }

    private void InitializeViews() {
        pieChart = (PieChart) findViewById(R.id.chart);

        fromDateTextView = (TextView) findViewById(R.id.graph_date_from);
        tillDateTextView = (TextView) findViewById(R.id.graph_date_till);

        categoryTextView = (TextView) findViewById(R.id.category);
        totalTextView = (TextView) findViewById(R.id.total);
        percentTextView = (TextView) findViewById(R.id.percent);

        categoriesSpinner = (Spinner) findViewById(R.id.spinner_category_type);
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
                InitializeGraphics();
            }
        });
        tillDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateTill(v);
                InitializeGraphics();
            }
        });

        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                InitializeGraphics();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
