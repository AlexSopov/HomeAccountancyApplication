package com.application.homeaccountancy.activity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PieCharActivity extends AppCompatActivity {
    static final int[] colors = new int[] {Color.parseColor("#e53935"), Color.parseColor("#26C6DA"),  Color.parseColor("#FFCA28"),
            Color.parseColor("#EC407A"),  Color.parseColor("#26A69A"),  Color.parseColor("#FFA726"),
            Color.parseColor("#AB47BC"),  Color.parseColor("#66BB6A"),  Color.parseColor("#FF7043"),
            Color.parseColor("#7E57C2"),  Color.parseColor("#9CCC65"),  Color.parseColor("#8D6E63"),
            Color.parseColor("#5C6BC0"),  Color.parseColor("#D4E157"),  Color.parseColor("#BDBDBD"),
            Color.parseColor("#42A5F5"),  Color.parseColor("#FFEE58"),  Color.parseColor("#78909C"),
            Color.parseColor("#29B6F6")};

    String fromDate, tillDate, fromJoinSelector, whereSelector;
    PieChart pieChart;

    TextView fromDateTextView, tillDateTextView, categoryTextView,
            totalTextView, percentTextView;
    Spinner categoriesSpinner;

    SQLiteHandler handler;
    SQLiteDatabase db;
    Cursor cursor;

    int changeField;

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
        dateTill.set(Calendar.DAY_OF_MONTH, dateTill.getMaximum(Calendar.DAY_OF_MONTH));

        dateFrom = Calendar.getInstance();
        dateFrom.set(Calendar.DAY_OF_MONTH, 1);
        dateFrom.set(Calendar.MONTH, dateTill.get(Calendar.MONTH));
        changeField = Calendar.MONTH;

        initializeViews();
        initializeListeners();
        initializeGraphics();

        pieChart.setNoDataText("Не найдено данных для отображения.");
        pieChart.setNoDataTextColor(Color.BLACK);
        setInitialDateTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.chart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(!item.isChecked())
            item.setChecked(true);

        switch(id){
            case R.id.chart_day :
                changeField = Calendar.DAY_OF_YEAR;
                return true;
            case R.id.chart_week :
                changeField = Calendar.WEEK_OF_YEAR;
                return true;
            case R.id.chart_month :
                changeField = Calendar.MONTH;
                return true;
            case R.id.chart_year :
                changeField = Calendar.YEAR;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (db != null)
            db.close();

        if (cursor != null)
            cursor.close();
    }

    private void initializeStrings() {
        int isOutgo = categoriesSpinner.getSelectedItemPosition() == 0 ? 1 : 0;

        fromDate = String.format("%tY-%tm-%td 00:00", dateFrom, dateFrom, dateFrom);
        tillDate = String.format("%tY-%tm-%td 23:59", dateTill, dateTill, dateTill);

        fromJoinSelector = " FROM " + AccountancyContract.Transaction.TABLE_NAME + " INNER JOIN " +
                AccountancyContract.Category.TABLE_NAME + " ON " + AccountancyContract.Category.TABLE_NAME +
                "." + AccountancyContract.Category._ID + " = " + AccountancyContract.Transaction.TABLE_NAME + "." +
                AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID ;

        whereSelector = " WHERE " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " >= '" +
                fromDate + "' AND " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " <= '" +
                tillDate + "'" + " AND " + AccountancyContract.Category.COLUMN_NAME_IS_OUTGO + " = " + isOutgo ;
    }
    private void initializeGraphics() {
        final int totalSum;
        List<PieEntry> entries = new ArrayList<>();

        initializeStrings();

        String query = "SELECT SUM(" + AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + ") " +
                fromJoinSelector + whereSelector;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
            totalSum = cursor.getInt(0);
        else return;

        query = "SELECT " + AccountancyContract.Category.COLUMN_NAME_TITLE +
                AccountancyContract.COMMA_SEPARATOR + " SUM (" +
                AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + ") " +
                fromJoinSelector + whereSelector +
                " GROUP BY " + AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID;

        cursor = db.rawQuery(query, null);

        int currentSum;
        if (cursor.moveToFirst()) {
            do {
                currentSum = cursor.getInt(1);
                entries.add(new PieEntry(Math.abs(currentSum), cursor.getString(0)));
            }
            while (cursor.moveToNext());
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Entries");
        pieDataSet.setValueTextColor(Color.argb(180, 0, 0, 0));
        pieDataSet.setValueTextSize(14);
        pieDataSet.setColors(colors);
        pieDataSet.setValueFormatter(new NegativeValueFormatter());
        pieDataSet.setDrawValues(true);
        pieDataSet.setHighlightEnabled(true);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText("Итого:\n " + String.valueOf(totalSum));
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
    private void initializeViews() {
        pieChart = (PieChart) findViewById(R.id.chart);
        pieChart.getLegend().setEnabled(false);;
        pieChart.setCenterTextSize(20);
        pieChart.setHoleRadius(30);
        pieChart.getContentDescription();
        pieChart.getDescription().setEnabled(false);
        pieChart.setTransparentCircleRadius(35);


        fromDateTextView = (TextView) findViewById(R.id.graph_date_from);
        tillDateTextView = (TextView) findViewById(R.id.graph_date_till);

        categoryTextView = (TextView) findViewById(R.id.category);
        totalTextView = (TextView) findViewById(R.id.total);
        percentTextView = (TextView) findViewById(R.id.percent);

        categoriesSpinner = (Spinner) findViewById(R.id.spinner_category_type);
    }
    private void initializeListeners() {
        onDateFromSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateFrom.set(Calendar.YEAR, year);
                dateFrom.set(Calendar.MONTH, monthOfYear);
                dateFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime();
                initializeGraphics();
            }
        };

        onDateTillSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTill.set(Calendar.YEAR, year);
                dateTill.set(Calendar.MONTH, monthOfYear);
                dateTill.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime();
                initializeGraphics();
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

        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initializeGraphics();
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

    private void dateChange(int delta) {
        switch (changeField) {
            case Calendar.DAY_OF_YEAR:
                dateTill.set(Calendar.DAY_OF_YEAR, dateTill.get(Calendar.DAY_OF_YEAR) + delta);
                dateFrom.set(Calendar.DAY_OF_YEAR, dateTill.get(Calendar.DAY_OF_YEAR));
                dateFrom.set(Calendar.YEAR, dateTill.get(Calendar.YEAR));
                break;
            case Calendar.WEEK_OF_YEAR:
                dateTill.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                dateTill.set(Calendar.WEEK_OF_YEAR, dateTill.get(Calendar.WEEK_OF_YEAR) + delta);
                dateFrom.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                dateFrom.set(Calendar.WEEK_OF_YEAR, dateTill.get(Calendar.WEEK_OF_YEAR));
                dateFrom.set(Calendar.YEAR, dateTill.get(Calendar.YEAR));
                break;
            case Calendar.MONTH:
                dateTill.set(Calendar.DAY_OF_MONTH, dateTill.getActualMinimum(Calendar.DAY_OF_MONTH));
                dateTill.set(Calendar.MONTH, dateTill.get(Calendar.MONTH) + delta);
                dateTill.set(Calendar.DAY_OF_MONTH, dateTill.getActualMaximum(Calendar.DAY_OF_MONTH));
                dateFrom.set(Calendar.DAY_OF_MONTH, dateFrom.getActualMinimum(Calendar.DAY_OF_MONTH));
                dateFrom.set(Calendar.MONTH, dateTill.get(Calendar.MONTH));
                dateFrom.set(Calendar.YEAR, dateTill.get(Calendar.YEAR));
                break;
            case Calendar.YEAR:
                dateTill.set(Calendar.DAY_OF_YEAR, dateTill.getActualMinimum(Calendar.DAY_OF_YEAR));
                dateTill.set(Calendar.YEAR, dateTill.get(Calendar.YEAR) + delta);
                dateTill.set(Calendar.DAY_OF_YEAR, dateTill.getActualMaximum(Calendar.DAY_OF_YEAR));
                dateFrom.set(Calendar.DAY_OF_YEAR, dateFrom.getActualMinimum(Calendar.DAY_OF_YEAR));
                dateFrom.set(Calendar.YEAR, dateTill.get(Calendar.YEAR));
                break;
        }
        setInitialDateTime();
        initializeViews();
    }
    public void decreaseDate(View view) {
        dateChange(-1);
        initializeGraphics();
    }
    public void increaseDate(View view) {
        dateChange(1);
        initializeGraphics();
    }

    private class NegativeValueFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.format("-%.2f", value);
        }
    }
}
