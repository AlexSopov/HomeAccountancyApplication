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

import android.widget.TextView;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BarChartActivity extends AppCompatActivity {
    String[] humanizeMonthSingle = new String[] {"Январь", "Февраль", "Март", "Апрель", "Май",
            "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    String[] humanizeMonthPersonable = new String[] {"Января", "Февраля", "Марта",
            "Апреля", "Июня", "Июля", "Августа", "Сентября", "Октября",
            "Ноября", "Декабря" };

    String fromDate, tillDate, fromJoinSelector, whereSelector;
    BarChart barChart;

    TextView humanizeDate, formatDate;

    SQLiteHandler handler;
    SQLiteDatabase db;

    int changeFieldInterval, changeFieldIteration;

    public static Calendar calendarFrom, calendarTill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_chart_activity);
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
        changeFieldIteration = Calendar.DAY_OF_YEAR;

        initializeViews();
        initializeGraphics();

        barChart.setNoDataText("Не найдено данных для отображения.");
        barChart.setNoDataTextColor(Color.BLACK);
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
                changeFieldIteration = Calendar.DAY_OF_YEAR;
                break;
            case R.id.chart_week :
                changeFieldInterval = Calendar.WEEK_OF_YEAR;
                changeFieldIteration = Calendar.DAY_OF_YEAR;
                break;
            case R.id.chart_month :
                changeFieldInterval = Calendar.MONTH;
                changeFieldIteration = Calendar.DAY_OF_YEAR;
                break;
            case R.id.chart_year :
                changeFieldInterval = Calendar.YEAR;
                changeFieldIteration = Calendar.MONTH;
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
        fromDate = String.format("%tY-%tm-%td 00:00", calendarFrom, calendarFrom, calendarFrom);
        tillDate = String.format("%tY-%tm-%td 23:59", calendarTill, calendarTill, calendarTill);

        fromJoinSelector = " FROM " + AccountancyContract.Transaction.TABLE_NAME + " INNER JOIN " +
                AccountancyContract.Category.TABLE_NAME + " ON " + AccountancyContract.Category.TABLE_NAME +
                "." + AccountancyContract.Category._ID + " = " + AccountancyContract.Transaction.TABLE_NAME + "." +
                AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID ;

        whereSelector = " WHERE " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " >= '" +
                fromDate + "' AND " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " <= '" +
                tillDate + "'";
    }
    private void initializeGraphics() {
        initializeStrings();

        List<BarEntry> outgoes = new ArrayList<>();
        List<BarEntry> incomes = new ArrayList<>();

        Calendar currentCalendarFrom = (Calendar)calendarFrom.clone();
        Calendar currentCalendarTill = (Calendar)calendarFrom.clone();
        if (changeFieldIteration == Calendar.MONTH) {
            currentCalendarTill.set(Calendar.MONTH, currentCalendarFrom.get(Calendar.MONTH));
            currentCalendarTill.set(Calendar.DAY_OF_MONTH, currentCalendarTill.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        int index = 0;
        while (currentCalendarFrom.get(changeFieldIteration) <= calendarTill.get(changeFieldIteration) &&
                currentCalendarFrom.get(Calendar.YEAR) == calendarTill.get(Calendar.YEAR)) {

            fromDate = String.format("%tY-%tm-%td 00:00", currentCalendarFrom, currentCalendarFrom, currentCalendarFrom);
            tillDate = String.format("%tY-%tm-%td 23:59", currentCalendarTill, currentCalendarTill, currentCalendarTill);

            String query = "SELECT SUM (" +
                    AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + ") " +
                    fromJoinSelector + " WHERE " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " >= '" +
                    fromDate + "' AND " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " <= '" +
                    tillDate + "'" + " AND " + AccountancyContract.Category.COLUMN_NAME_IS_OUTGO +
                    " = %d" ;

            Cursor cursor = null;
            try {
                cursor = db.rawQuery(String.format(query, 1), null);
                cursor.moveToFirst();
                outgoes.add(new BarEntry(index, Math.abs(cursor.getInt(0))));


                cursor = db.rawQuery(String.format(query, 0), null);
                cursor.moveToFirst();
                incomes.add(new BarEntry(index, Math.abs(cursor.getInt(0))));

                index++;
                if (changeFieldIteration == Calendar.DAY_OF_YEAR) {
                    currentCalendarFrom.set(Calendar.DAY_OF_YEAR, currentCalendarFrom.get(changeFieldIteration) + 1);
                    currentCalendarTill.set(Calendar.DAY_OF_YEAR, currentCalendarTill.get(changeFieldIteration) + 1);
                }
                else {
                    currentCalendarFrom.set(Calendar.DAY_OF_MONTH, currentCalendarFrom.getActualMinimum(Calendar.DAY_OF_MONTH));
                    currentCalendarFrom.set(Calendar.MONTH, currentCalendarFrom.get(Calendar.MONTH) + 1);

                    currentCalendarTill.set(Calendar.DAY_OF_MONTH, currentCalendarTill.getActualMinimum(Calendar.DAY_OF_MONTH));
                    currentCalendarTill.set(Calendar.MONTH, currentCalendarTill.get(Calendar.MONTH) + 1);
                    currentCalendarTill.set(Calendar.DAY_OF_MONTH, currentCalendarTill.getActualMaximum(Calendar.DAY_OF_MONTH));
                }
            }
            catch (Exception ignored) {}
            finally {
                if (cursor != null)
                    cursor.close();
            }

        }


        BarDataSet barDataSetOutgoes = new BarDataSet(outgoes, "Траты");
        barDataSetOutgoes.setColor(Color.parseColor("#FFCA28"));
        BarDataSet barDataSetIncomes = new BarDataSet(incomes, "Пополнения");
        barDataSetIncomes.setColor(Color.parseColor("#66BB6A"));

        float groupSpace = 0.1f;
        float barSpace = 0.01f;
        float barWidth = 0.44f;

        BarData data = new BarData(barDataSetOutgoes, barDataSetIncomes);
        data.setDrawValues(false);
        data.setBarWidth(barWidth);

        barChart.setData(data);
        barChart.groupBars(0, groupSpace, barSpace);

        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setEnabled(false);

        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(index);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setCenterAxisLabels(true);
        barChart.getXAxis().setLabelRotationAngle(30);

        if (changeFieldIteration == Calendar.DAY_OF_YEAR)
            barChart.getXAxis().setValueFormatter(new DaysValueFormatter(calendarFrom));
        else
            barChart.getXAxis().setValueFormatter(new MonthValueFormatter(calendarFrom));

        barChart.getXAxis().setGranularityEnabled(true);

        barChart.setHighlightPerDragEnabled(false);
        barChart.setHighlightPerTapEnabled(false);

        barChart.getLegend().setXEntrySpace(20);
        barChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        barChart.getViewPortHandler().setMaximumScaleY(1);
        barChart.invalidate();
    }
    private void initializeViews() {
        barChart = (BarChart) findViewById(R.id.chart);
        barChart.getDescription().setEnabled(false);

        humanizeDate = (TextView) findViewById(R.id.humanize_date);
        formatDate = (TextView) findViewById(R.id.format_date);
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
                humanizeDate.setText(String.format("%d %s %d",
                        calendarFrom.get(Calendar.WEEK_OF_YEAR),
                        humanizeMonthPersonable[calendarFrom.get(Calendar.MONTH)],
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

    public class DaysValueFormatter implements IAxisValueFormatter {
        String[] month = new String[] {"Янв.", "Фев.", "Мар.", "Апр.", "Май",
                "Июн.", "Июл.", "Авг.", "Сен.", "Окт.", "Ноя.", "Дек."};

        Calendar startDate;
        DaysValueFormatter(Calendar calendar) {
            startDate = (Calendar)calendar.clone();
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Calendar currentCalendar = (Calendar) startDate.clone();
            currentCalendar.set(Calendar.DAY_OF_YEAR, currentCalendar.get(Calendar.DAY_OF_YEAR) + (int)value);

            return String.format("%d %s %d",
                    currentCalendar.get(Calendar.DAY_OF_MONTH),
                    month[currentCalendar.get(Calendar.MONTH)],
                    currentCalendar.get(Calendar.YEAR));
        }
    }

    public class MonthValueFormatter implements IAxisValueFormatter {
        String[] month = new String[] {"Январь", "Февраль", "Март", "Апрель", "Май",
                "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

        Calendar startDate;
        MonthValueFormatter(Calendar calendar) {
            startDate = (Calendar)calendar.clone();
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Calendar currentCalendar = (Calendar) startDate.clone();
            currentCalendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH) + (int)value);

            return String.format("%s %d",
                    month[currentCalendar.get(Calendar.MONTH)],
                    currentCalendar.get(Calendar.YEAR));
        }
    }
}
