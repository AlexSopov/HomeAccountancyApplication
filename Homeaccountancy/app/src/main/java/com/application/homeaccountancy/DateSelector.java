package com.application.homeaccountancy;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

public class DateSelector {
    private int changeFieldInterval;
    private Calendar calendarFrom, calendarTill;
    private TextView humanizeDateTextView, formatDateTextView;
    private ImageButton increaseDateImageButton, decreaseDateImageButton;
    private Activity activity;

    public DateSelector(Activity activity) {
        this.activity = activity;

        initializeViews();
        initializeStartDate();

        setDateTimeView();
    }

    public void setChangeFieldInterval(int changeFieldInterval) {
        this.changeFieldInterval = changeFieldInterval;
    }
    public void dateChange(int delta) {
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
    public void resetState() {
        calendarFrom = Calendar.getInstance();
        calendarTill = Calendar.getInstance();
        dateChange(0);
    }
    public String getFromString() {
        return  String.format("%tY-%tm-%td 00:00", calendarFrom, calendarFrom, calendarFrom);
    }
    public String getTillString() {
        return  String.format("%tY-%tm-%td 23:59", calendarTill, calendarTill, calendarTill);
    }
    public Calendar getCalendarFrom() {
        return calendarFrom;
    }
    public Calendar getCalendarTill() {
        return calendarTill;
    }

    public void setOnIncreaseClickListener(View.OnClickListener listener) {
        increaseDateImageButton.setOnClickListener(listener);
    }
    public void setOnDecreaseClickListener(View.OnClickListener listener) {
        decreaseDateImageButton.setOnClickListener(listener);
    }

    private void initializeViews() {
        humanizeDateTextView = (TextView) activity.findViewById(R.id.date_selector_humanize_date);
        formatDateTextView = (TextView) activity.findViewById(R.id.date_selector_format_date);
        increaseDateImageButton = (ImageButton) activity.findViewById(R.id.date_selector_increase_date);
        decreaseDateImageButton = (ImageButton) activity.findViewById(R.id.date_selector_decrease_date);
    }
    private void initializeStartDate() {
        calendarTill = Calendar.getInstance();
        calendarTill.set(Calendar.DAY_OF_MONTH, calendarTill.getActualMaximum(Calendar.DAY_OF_MONTH));

        calendarFrom = Calendar.getInstance();
        calendarFrom.set(Calendar.DAY_OF_MONTH, calendarFrom.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendarFrom.set(Calendar.MONTH, calendarTill.get(Calendar.MONTH));

        changeFieldInterval = Calendar.MONTH;
    }
    private void setDateTimeView() {
        formatDateTextView.setText(String.format("%td.%tm.%tY - %td.%tm.%tY",
                calendarFrom, calendarFrom, calendarFrom, calendarTill, calendarTill, calendarTill));

        switch (changeFieldInterval) {
            case Calendar.DAY_OF_YEAR:
                humanizeDateTextView.setText(String.format("%d %s %d",
                        calendarFrom.get(Calendar.DAY_OF_MONTH),
                        activity.getResources().getStringArray(R.array.humanizeMonthPersonable)[calendarFrom.get(Calendar.MONTH)],
                        calendarFrom.get(Calendar.YEAR)));
                break;
            case Calendar.WEEK_OF_YEAR:
                humanizeDateTextView.setText(String.format("%d неделя %d",
                        calendarFrom.get(Calendar.WEEK_OF_YEAR),
                        calendarFrom.get(Calendar.YEAR)));
                break;
            case Calendar.MONTH:
                humanizeDateTextView.setText(String.format("%s %d",
                        activity.getResources().getStringArray(R.array.humanizeMonthSingle)[calendarFrom.get(Calendar.MONTH)],
                        calendarFrom.get(Calendar.YEAR)));
                break;
            case Calendar.YEAR:
                humanizeDateTextView.setText(String.format("%d год",
                        calendarFrom.get(Calendar.YEAR)));
                break;
        }
    }
}