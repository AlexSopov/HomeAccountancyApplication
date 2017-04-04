package com.application.homeaccountancy.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.DateSelector;
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

public class BarChartActivity extends UsingDataBaseActivity {
    private String fromDate, tillDate, fromJoinSelector;
    private int changeFieldIteration;
    private DateSelector dateSelector;

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_chart_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateSelector = new DateSelector(this);
        dateSelector.setOnDecreaseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseDate(v);
            }
        });
        dateSelector.setOnIncreaseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseDate(v);
            }
        });

        changeFieldIteration = Calendar.DAY_OF_YEAR;
        initializeViews();
        initializeGraphics();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.date_selector_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(!item.isChecked())
            item.setChecked(true);

        switch(id){
            case R.id.range_day:
                dateSelector.setChangeFieldInterval(Calendar.DAY_OF_YEAR);
                changeFieldIteration = Calendar.DAY_OF_YEAR;
                break;
            case R.id.range_week:
                dateSelector.setChangeFieldInterval(Calendar.WEEK_OF_YEAR);
                changeFieldIteration = Calendar.DAY_OF_YEAR;
                break;
            case R.id.range_month:
                dateSelector.setChangeFieldInterval(Calendar.MONTH);
                changeFieldIteration = Calendar.DAY_OF_YEAR;
                break;
            case R.id.range_year:
                dateSelector.setChangeFieldInterval(Calendar.YEAR);
                changeFieldIteration = Calendar.MONTH;
                break;
        }
        dateSelector.resetState();
        initializeGraphics();

        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        barChart = (BarChart) findViewById(R.id.chart);
        barChart.getDescription().setEnabled(false);
        barChart.setNoDataText("Не найдено данных для отображения.");
        barChart.setNoDataTextColor(Color.BLACK);
    }
    private void initializeStrings() {
        fromDate = dateSelector.getFromString();
        tillDate = dateSelector.getTillString();

        fromJoinSelector = " FROM " + AccountancyContract.Transaction.TABLE_NAME + " INNER JOIN " +
                AccountancyContract.Category.TABLE_NAME + " ON " + AccountancyContract.Category.TABLE_NAME +
                "." + AccountancyContract.Category._ID + " = " + AccountancyContract.Transaction.TABLE_NAME + "." +
                AccountancyContract.Transaction.CATEGORY_ID;
    }
    private void initializeGraphics() {
        initializeStrings();

        List<BarEntry> outgoes = new ArrayList<>();
        List<BarEntry> incomes = new ArrayList<>();

        Calendar currentCalendarFrom = (Calendar)dateSelector.getCalendarFrom().clone();
        Calendar currentCalendarTill = (Calendar)dateSelector.getCalendarFrom().clone();
        if (changeFieldIteration == Calendar.MONTH) {
            currentCalendarTill.set(Calendar.MONTH, currentCalendarFrom.get(Calendar.MONTH));
            currentCalendarTill.set(Calendar.DAY_OF_MONTH, currentCalendarTill.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        int index = 0;
        while (currentCalendarTill.compareTo(dateSelector.getCalendarTill()) <= 0) {
            fromDate = String.format("%tY-%tm-%td 00:00", currentCalendarFrom, currentCalendarFrom, currentCalendarFrom);
            tillDate = String.format("%tY-%tm-%td 23:59", currentCalendarTill, currentCalendarTill, currentCalendarTill);

            String query = "SELECT SUM (" +
                    AccountancyContract.Transaction.AMOUNT + ") " +
                    fromJoinSelector + " WHERE " + AccountancyContract.Transaction.DATE + " >= '" +
                    fromDate + "' AND " + AccountancyContract.Transaction.DATE + " <= '" +
                    tillDate + "'" + " AND " + AccountancyContract.Category.IS_OUTGO +
                    " = %d" ;

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

            cursor.close();
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
            barChart.getXAxis().setValueFormatter(new DaysValueFormatter(dateSelector.getCalendarFrom()));
        else
            barChart.getXAxis().setValueFormatter(new MonthValueFormatter(dateSelector.getCalendarFrom()));

        barChart.getXAxis().setGranularityEnabled(true);

        barChart.setHighlightPerDragEnabled(false);
        barChart.setHighlightPerTapEnabled(false);

        barChart.getLegend().setXEntrySpace(20);
        barChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        barChart.getViewPortHandler().setMaximumScaleY(1);
        barChart.invalidate();
    }

    private void decreaseDate(View view) {
        dateSelector.dateChange(-1);
        initializeGraphics();
    }
    private void increaseDate(View view) {
        dateSelector.dateChange(1);
        initializeGraphics();
    }

    private class DaysValueFormatter implements IAxisValueFormatter {
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
    private class MonthValueFormatter implements IAxisValueFormatter {
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
