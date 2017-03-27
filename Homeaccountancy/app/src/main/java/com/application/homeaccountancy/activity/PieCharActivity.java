package com.application.homeaccountancy.activity;

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

    String[] humanizeMonthSingle = new String[] {"Январь", "Февраль", "Март", "Апрель", "Май",
            "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    String[] humanizeMonthPersonable = new String[] {"Января", "Февраля", "Марта",
            "Апреля", "Июня", "Июля", "Августа", "Сентября", "Октября",
            "Ноября", "Декабря" };

    String fromDate, tillDate, fromJoinSelector, whereSelector;
    PieChart pieChart;

    TextView humanizeDate, formatDate, categoryTextView,
            totalTextView, percentTextView;
    Spinner categoriesSpinner;

    SQLiteHandler handler;
    SQLiteDatabase db;

    int changeFieldInterval;

    public static Calendar calendarFrom, calendarTill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_char_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handler = new SQLiteHandler(getApplicationContext());
        db = handler.getReadableDatabase();

        calendarTill = Calendar.getInstance();
        calendarTill.set(Calendar.DAY_OF_MONTH, calendarTill.getMaximum(Calendar.DAY_OF_MONTH));

        calendarFrom = Calendar.getInstance();
        calendarFrom.set(Calendar.DAY_OF_MONTH, 1);
        calendarFrom.set(Calendar.MONTH, calendarTill.get(Calendar.MONTH));
        changeFieldInterval = Calendar.MONTH;

        initializeViews();
        initializeListeners();
        initializeGraphics();

        pieChart.setNoDataText("Не найдено данных для отображения.");
        pieChart.setNoDataTextColor(Color.BLACK);
        setDateTimeView();
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
                changeFieldInterval = Calendar.DAY_OF_YEAR;
                break;
            case R.id.chart_week :
                changeFieldInterval = Calendar.WEEK_OF_YEAR;
                break;
            case R.id.chart_month :
                changeFieldInterval = Calendar.MONTH;
                break;
            case R.id.chart_year :
                changeFieldInterval = Calendar.YEAR;
                break;
        }
        calendarFrom = Calendar.getInstance();
        calendarTill = Calendar.getInstance();
        dateChange(0);

        initializeGraphics();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (db != null)
            db.close();
    }

    private void initializeStrings() {
        int isOutgo = categoriesSpinner.getSelectedItemPosition() == 0 ? 1 : 0;

        fromDate = String.format("%tY-%tm-%td 00:00", calendarFrom, calendarFrom, calendarFrom);
        tillDate = String.format("%tY-%tm-%td 23:59", calendarTill, calendarTill, calendarTill);

        fromJoinSelector = " FROM " + AccountancyContract.Transaction.TABLE_NAME + " INNER JOIN " +
                AccountancyContract.Category.TABLE_NAME + " ON " + AccountancyContract.Category.TABLE_NAME +
                "." + AccountancyContract.Category._ID + " = " + AccountancyContract.Transaction.TABLE_NAME + "." +
                AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID ;

        whereSelector = " WHERE " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " >= '" +
                fromDate + "' AND " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " <= '" +
                tillDate + "'" + " AND " + AccountancyContract.Category.COLUMN_NAME_IS_OUTGO + " = " + isOutgo ;
    }
    private void initializeGraphics() {
        int totalSum = 0;
        final int totalSumEx;

        List<PieEntry> entries = new ArrayList<>();

        initializeStrings();
        String query = "SELECT SUM(" + AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + ") " +
                fromJoinSelector + whereSelector;

        Cursor cursor = null;
        try {
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
        }
        catch (Exception ignored) { }
        finally {
            if (cursor != null)
                cursor.close();
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Entries");
        pieDataSet.setValueTextColor(Color.argb(180, 0, 0, 0));
        pieDataSet.setValueTextSize(14);
        pieDataSet.setColors(colors);

        if (totalSum < 0)
            pieDataSet.setValueFormatter(new NegativeValueFormatter());
        pieDataSet.setDrawValues(true);
        pieDataSet.setHighlightEnabled(true);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText("Итого:\n " + String.valueOf(totalSum));

        totalSumEx = totalSum;
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                categoryTextView.setText(((PieEntry)e).getLabel());
                totalTextView.setText("Сумма: " + String.valueOf(e.getY()));
                percentTextView.setText(String.format("Процент: %.2f%%", Math.abs((e.getY() * 100) / totalSumEx)));
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

        humanizeDate = (TextView) findViewById(R.id.humanize_date);
        formatDate = (TextView) findViewById(R.id.format_date);

        categoryTextView = (TextView) findViewById(R.id.category);
        totalTextView = (TextView) findViewById(R.id.total);
        percentTextView = (TextView) findViewById(R.id.percent);

        categoriesSpinner = (Spinner) findViewById(R.id.spinner_category_type);
    }
    private void initializeListeners() {
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

    private void setDateTimeView() {
        formatDate.setText(String.format("%td.%tm.%tY - %td.%tm.%tY",
                calendarFrom, calendarFrom, calendarFrom, calendarTill, calendarTill, calendarTill));

        switch (changeFieldInterval) {
            case Calendar.DAY_OF_YEAR:
                humanizeDate.setText(String.format("%d %s %d",
                        calendarFrom.get(Calendar.DAY_OF_MONTH),
                        humanizeMonthPersonable[calendarFrom.get(Calendar.MONTH)],
                        calendarFrom.get(Calendar.YEAR)));
                break;
            case Calendar.WEEK_OF_YEAR:
                humanizeDate.setText(String.format("%d неделя %d",
                        calendarFrom.get(Calendar.WEEK_OF_YEAR),
                        calendarFrom.get(Calendar.YEAR)));
                break;
            case Calendar.MONTH:
                humanizeDate.setText(String.format("%s %d",
                        humanizeMonthSingle[calendarFrom.get(Calendar.MONTH)],
                        calendarFrom.get(Calendar.YEAR)));
                break;
            case Calendar.YEAR:
                humanizeDate.setText(String.format("%d год",
                        calendarFrom.get(Calendar.YEAR)));
                break;
        }
    }

    private void dateChange(int delta) {
        switch (changeFieldInterval) {
            case Calendar.DAY_OF_YEAR:
                calendarTill.set(Calendar.DAY_OF_YEAR, calendarTill.get(Calendar.DAY_OF_YEAR) + delta);
                calendarFrom.set(Calendar.DAY_OF_YEAR, calendarTill.get(Calendar.DAY_OF_YEAR));
                calendarFrom.set(Calendar.YEAR, calendarTill.get(Calendar.YEAR));
                break;
            case Calendar.WEEK_OF_YEAR:
                calendarTill.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                calendarTill.set(Calendar.WEEK_OF_YEAR, calendarTill.get(Calendar.WEEK_OF_YEAR) + delta);
                calendarFrom.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                calendarFrom.set(Calendar.WEEK_OF_YEAR, calendarTill.get(Calendar.WEEK_OF_YEAR));
                calendarFrom.set(Calendar.YEAR, calendarTill.get(Calendar.YEAR));
                break;
            case Calendar.MONTH:
                calendarTill.set(Calendar.DAY_OF_MONTH, calendarTill.getActualMinimum(Calendar.DAY_OF_MONTH));
                calendarTill.set(Calendar.MONTH, calendarTill.get(Calendar.MONTH) + delta);
                calendarTill.set(Calendar.DAY_OF_MONTH, calendarTill.getActualMaximum(Calendar.DAY_OF_MONTH));
                calendarFrom.set(Calendar.DAY_OF_MONTH, calendarFrom.getActualMinimum(Calendar.DAY_OF_MONTH));
                calendarFrom.set(Calendar.MONTH, calendarTill.get(Calendar.MONTH));
                calendarFrom.set(Calendar.YEAR, calendarTill.get(Calendar.YEAR));
                break;
            case Calendar.YEAR:
                calendarTill.set(Calendar.DAY_OF_YEAR, calendarTill.getActualMinimum(Calendar.DAY_OF_YEAR));
                calendarTill.set(Calendar.YEAR, calendarTill.get(Calendar.YEAR) + delta);
                calendarTill.set(Calendar.DAY_OF_YEAR, calendarTill.getActualMaximum(Calendar.DAY_OF_YEAR));
                calendarFrom.set(Calendar.DAY_OF_YEAR, calendarFrom.getActualMinimum(Calendar.DAY_OF_YEAR));
                calendarFrom.set(Calendar.YEAR, calendarTill.get(Calendar.YEAR));
                break;
        }
        setDateTimeView();
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
            return String.format("-%.1f", value);
        }
    }
}
