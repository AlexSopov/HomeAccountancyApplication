package com.application.homeaccountancy.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.DateSelector;
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

public class PieCharActivity extends UsingDataBaseActivity {
    // region final int[] colors
    static final int[] colors = new int[] {Color.parseColor("#e53935"), Color.parseColor("#26C6DA"),  Color.parseColor("#FFCA28"),
            Color.parseColor("#EC407A"),  Color.parseColor("#26A69A"),  Color.parseColor("#FFA726"),
            Color.parseColor("#AB47BC"),  Color.parseColor("#66BB6A"),  Color.parseColor("#FF7043"),
            Color.parseColor("#7E57C2"),  Color.parseColor("#9CCC65"),  Color.parseColor("#8D6E63"),
            Color.parseColor("#5C6BC0"),  Color.parseColor("#D4E157"),  Color.parseColor("#BDBDBD"),
            Color.parseColor("#42A5F5"),  Color.parseColor("#FFEE58"),  Color.parseColor("#78909C"),
            Color.parseColor("#29B6F6")};
    // endregion

    private String fromJoinSelector, whereSelector;

    private TextView categoryTextView, totalTextView, percentTextView;
    private Spinner categoriesSpinner;
    private DateSelector dateSelector;

    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_char_activity);

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

        initializeViews();
        initializeListeners();
        initializeGraphics();

        pieChart.setNoDataText("Не найдено данных для отображения.");
        pieChart.setNoDataTextColor(Color.BLACK);
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
                break;
            case R.id.range_week:
                dateSelector.setChangeFieldInterval(Calendar.WEEK_OF_YEAR);
                break;
            case R.id.range_month:
                dateSelector.setChangeFieldInterval(Calendar.MONTH);
                break;
            case R.id.range_year:
                dateSelector.setChangeFieldInterval(Calendar.YEAR);
                break;
        }
        dateSelector.resetState();

        initializeGraphics();
        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        pieChart = (PieChart) findViewById(R.id.chart);
        pieChart.getLegend().setEnabled(false);;
        pieChart.setCenterTextSize(20);
        pieChart.setHoleRadius(30);
        pieChart.getContentDescription();
        pieChart.getDescription().setEnabled(false);
        pieChart.setTransparentCircleRadius(35);

        categoryTextView = (TextView) findViewById(R.id.category);
        totalTextView = (TextView) findViewById(R.id.total);
        percentTextView = (TextView) findViewById(R.id.percent);

        categoriesSpinner = (Spinner) findViewById(R.id.spinner_category_type);
    }
    private void initializeGraphics() {
        int totalSum;
        final int totalSumEx;

        initializeStrings();
        List<PieEntry> entries = new ArrayList<>();
        String query = "SELECT SUM(" + AccountancyContract.Transaction.AMOUNT + ") " +
                fromJoinSelector + whereSelector;

        cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
            totalSum = cursor.getInt(0);
        else return;

        query = "SELECT " + AccountancyContract.Category.C_TITLE +
                AccountancyContract.COMMA + " SUM (" +
                AccountancyContract.Transaction.AMOUNT + ") " +
                fromJoinSelector + whereSelector +
                " GROUP BY " + AccountancyContract.Transaction.CATEGORY_ID;

        cursor = db.rawQuery(query, null);

        int currentSum;
        if (cursor.moveToFirst()) {
            do {
                currentSum = cursor.getInt(1);
                entries.add(new PieEntry(Math.abs(currentSum), cursor.getString(0)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();


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
                totalTextView.setText("Сумма: " + String.valueOf(e.getY()) + " руб.");
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
    private void initializeStrings() {
        int isOutgo = categoriesSpinner.getSelectedItemPosition() == 0 ? 1 : 0;

        String fromDate = dateSelector.getFromString();
        String tillDate = dateSelector.getTillString();

        fromJoinSelector = " FROM " + AccountancyContract.Transaction.TABLE_NAME + " INNER JOIN " +
                AccountancyContract.Category.TABLE_NAME + " ON " + AccountancyContract.Category.TABLE_NAME +
                "." + AccountancyContract.Category._ID + " = " + AccountancyContract.Transaction.TABLE_NAME + "." +
                AccountancyContract.Transaction.CATEGORY_ID;

        whereSelector = " WHERE " + AccountancyContract.Transaction.DATE + " >= '" +
                fromDate + "' AND " + AccountancyContract.Transaction.DATE + " <= '" +
                tillDate + "'" + " AND " + AccountancyContract.Category.IS_OUTGO + " = " + isOutgo ;
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

    public void decreaseDate(View view) {
        dateSelector.dateChange(-1);
        initializeGraphics();
    }
    public void increaseDate(View view) {
        dateSelector.dateChange(1);
        initializeGraphics();
    }

    private class NegativeValueFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.format("-%.1f", value);
        }
    }
}