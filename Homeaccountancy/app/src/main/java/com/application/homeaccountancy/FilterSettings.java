package com.application.homeaccountancy;

import android.app.DatePickerDialog;

import java.util.Calendar;

public class FilterSettings {
    public static boolean fromDateCheckBoxChecked, tillDateCheckBoxChecked,
            categoryCheckBoxChecked, accountCheckBoxChecked;
    public static long categoryID, accountID;
    public static Calendar dateFrom, dateTill;
    public static boolean isFilter;
}
