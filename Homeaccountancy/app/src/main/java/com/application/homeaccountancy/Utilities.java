package com.application.homeaccountancy;

import android.content.Context;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class Utilities {
    public static void selectSpinnerItem(SimpleCursorAdapter adapter, Spinner spinner, long targetId) {
        for (int i = 0; i < adapter.getCount(); i++) {
            if (targetId == adapter.getItemId(i)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
    public static void makeToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }
    public static String getSQLiteTimeString(Calendar calendar) {
        return String.format("%tY-%tm-%td %tH:%tM:00",
                calendar, calendar, calendar, calendar, calendar);
    }
}