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

// Класс Activity, содержащего столбчатую диаграмму
public class BarChartActivity extends UsingDataBaseActivity {
    private String fromJoinSelector;

    // Поле календаря, по которому производятся итерации
    private int changeFieldIteration;

    // Объект для генерации временных периодов
    private DateSelector dateSelector;

    // Элемент стобчатой диаграммы
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализация контента
        setContentView(R.layout.bar_chart_activity);

        // Инициализация тулбара
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Инициализация объекта для генерации временных периодов
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

        // Инициализация представления
        initializeViews();

        // Инициализация графика
        initializeGraphics();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Меню для выбора шага временного периода
        getMenuInflater().inflate(R.menu.date_selector_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обработка нажатия по элементу в меню

        int id = item.getItemId();
        if(!item.isChecked())
            item.setChecked(true);

        boolean isChangeState = true;
        // В зависимости от нажатого элемента - изменить переменную
        // для итерации
        // Изменить временной диапазон
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
            default:
                isChangeState = false;
        }
        if (isChangeState) {
            dateSelector.resetState();
            initializeGraphics();
        }

        return super.onOptionsItemSelected(item);
}

    private void initializeViews() {
        // Инициализация представления

        // Инициализация элемента столбчатой диаграммы
        barChart = (BarChart) findViewById(R.id.chart);

        // Убрать описание
        barChart.getDescription().setEnabled(false);

        // Установка параметров отображения пустых данных
        barChart.setNoDataText("No data found to be displayed.");
        barChart.setNoDataTextColor(Color.BLACK);

        // Установить минимум для оси у
        barChart.getAxisLeft().setAxisMinimum(0);

        // Убрать правую ось
        barChart.getAxisRight().setEnabled(false);

        // Настройка вида графика
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setCenterAxisLabels(true);
        barChart.getXAxis().setLabelRotationAngle(30);
        barChart.getXAxis().setGranularityEnabled(true);

        // Убрать подсвечивание при нажатии на элементе графика
        barChart.setHighlightPerDragEnabled(false);
        barChart.setHighlightPerTapEnabled(false);

        // Настройка легенды
        barChart.getLegend().setXEntrySpace(20);
        barChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        // Убрать масштабирование по у
        barChart.getViewPortHandler().setMaximumScaleY(1);
    }
    private void initializeStrings() {
        // Инициализация строк запроса

        // Часть запроса, содержащую блок выбора таблицы
        // данных и соединения
        fromJoinSelector = " FROM " + AccountancyContract.Transaction.TABLE_NAME + " INNER JOIN " +
                AccountancyContract.Category.TABLE_NAME + " ON " + AccountancyContract.Category.TABLE_NAME +
                "." + AccountancyContract.Category._ID + " = " + AccountancyContract.Transaction.TABLE_NAME + "." +
                AccountancyContract.Transaction.CATEGORY_ID;
    }
    private void initializeGraphics() {
        // Инициализация строк запроса
        initializeStrings();

        // Данные трат и пополнения соответственно
        List<BarEntry> outgoes = new ArrayList<>();
        List<BarEntry> incomes = new ArrayList<>();

        // Инициализация начальной и конечной даты итераций
        Calendar currentCalendarFrom = (Calendar)dateSelector.getCalendarFrom().clone();
        Calendar currentCalendarTill = (Calendar)dateSelector.getCalendarFrom().clone();
        if (changeFieldIteration == Calendar.MONTH) {
            currentCalendarTill.set(Calendar.MONTH, currentCalendarFrom.get(Calendar.MONTH));
            currentCalendarTill.set(Calendar.DAY_OF_MONTH, currentCalendarTill.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        int index = 0;
        while (currentCalendarTill.compareTo(dateSelector.getCalendarTill()) <= 0) {
            // Инициализация строк временного диапазона
            // для которого будут вычисляться данные на текущей итерации
            String fromDate = String.format("%tY-%tm-%td 00:00", currentCalendarFrom, currentCalendarFrom, currentCalendarFrom);
            String tillDate = String.format("%tY-%tm-%td 23:59", currentCalendarTill, currentCalendarTill, currentCalendarTill);

            // Запрос на получение суммы платежей за определенный период времени
            // Для трат или пополнений
            String query = "SELECT SUM (" +
                    AccountancyContract.Transaction.AMOUNT + ") " +
                    fromJoinSelector + " WHERE " + AccountancyContract.Transaction.DATE + " >= '" +
                    fromDate + "' AND " + AccountancyContract.Transaction.DATE + " <= '" +
                    tillDate + "'" + " AND " + AccountancyContract.Category.IS_OUTGO +
                    " = %d" ;

            // Нахождение суммы трат
            cursor = db.rawQuery(String.format(query, 1), null);
            cursor.moveToFirst();
            outgoes.add(new BarEntry(index, Math.abs(cursor.getInt(0))));

            // Нахождение суммы пополнений
            cursor = db.rawQuery(String.format(query, 0), null);
            cursor.moveToFirst();
            incomes.add(new BarEntry(index, Math.abs(cursor.getInt(0))));


            // Переход к слеующему временному отрезку
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

        // Установка данных графика
        BarDataSet barDataSetOutgoes = new BarDataSet(outgoes, "Outgo");
        barDataSetOutgoes.setColor(Color.parseColor("#FFCA28"));
        BarDataSet barDataSetIncomes = new BarDataSet(incomes, "Income");
        barDataSetIncomes.setColor(Color.parseColor("#66BB6A"));

        float groupSpace = 0.1f;
        float barSpace = 0.01f;
        float barWidth = 0.44f;

        BarData data = new BarData(barDataSetOutgoes, barDataSetIncomes);
        data.setDrawValues(false);
        data.setBarWidth(barWidth);

        barChart.setData(data);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.getXAxis().setAxisMaximum(index);

        // Формат подписи осей графика
        if (changeFieldIteration == Calendar.DAY_OF_YEAR)
            barChart.getXAxis().setValueFormatter(new DaysValueFormatter(dateSelector.getCalendarFrom()));
        else
            barChart.getXAxis().setValueFormatter(new MonthValueFormatter(dateSelector.getCalendarFrom()));

        barChart.invalidate();
    }

    private void decreaseDate(View view) {
        // Уменьшение временного периода
        dateSelector.dateChange(-1);
        initializeGraphics();
    }
    private void increaseDate(View view) {
        // Увеличение временного периода
        dateSelector.dateChange(1);
        initializeGraphics();
    }

    // Форматирование дней месяца
    private class DaysValueFormatter implements IAxisValueFormatter {
        String[] month = new String[] {"Jan.", "Feb.", "Mar.", "Apr.", "May",
                "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."};

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

    // Форматирование месяцев года
    private class MonthValueFormatter implements IAxisValueFormatter {
        String[] month = new String[] {"January", "February", "March", "April", "May",
                "June", "July", "August", "September", "October", "November", "December"};

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
