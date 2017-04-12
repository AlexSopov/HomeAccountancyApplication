package com.application.homeaccountancy;

import android.content.Context;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

// Вспомогательные методы для работы с представлениями
public class Utilities {
    // Выделить элемент в spinner, с id targetId
    public static void selectSpinnerItem(SimpleCursorAdapter adapter, Spinner spinner, long targetId) {
        for (int i = 0; i < adapter.getCount(); i++) {
            if (targetId == adapter.getItemId(i)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
    // Вывести Toast с сообщением message
    public static void makeToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

    // Получить строковое представлнеие даты calendar в формате SQLite
    public static String getSQLiteTimeString(Calendar calendar) {
        return String.format("%tY-%tm-%td %tH:%tM:00",
                calendar, calendar, calendar, calendar, calendar);
    }
}