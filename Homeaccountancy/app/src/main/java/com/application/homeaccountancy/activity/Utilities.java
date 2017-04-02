package com.application.homeaccountancy.activity;

import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

class Utilities {
    static void selectSpinnerItem(SimpleCursorAdapter adapter, Spinner spinner, long targetId) {
        for (int i = 0; i < adapter.getCount(); i++) {
            if (targetId == adapter.getItemId(i)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}
