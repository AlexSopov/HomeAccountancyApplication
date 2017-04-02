package com.application.homeaccountancy;

import android.app.DatePickerDialog;

import java.util.Calendar;

public class FilterSettings {
    public static boolean fromDateCheckBoxChecked, tillDateCheckBoxChecked,
            categoryCheckBoxChecked, accountCheckBoxChecked;
    public static long categoryID, accountID;
    public static Calendar dateFrom, dateTill;
    public static boolean isFilter;

    public static Calendar calendarFrom, calendarTill;
    public static int changeFieldInterval = 2;

    static boolean isInitialized = false;
    public static void initialize() {
        if (!isInitialized) {
            calendarTill = Calendar.getInstance();
            calendarTill.set(Calendar.DAY_OF_MONTH, calendarTill.getActualMaximum(Calendar.DAY_OF_MONTH));

            calendarFrom = Calendar.getInstance();
            calendarFrom.set(Calendar.DAY_OF_MONTH, 1);
            calendarFrom.set(Calendar.MONTH, calendarTill.get(Calendar.MONTH));

            isInitialized = true;
        }
    }
}
