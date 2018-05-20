package com.application.homeaccountancy.Data.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.R;

// Адаптер для вывода счета
public class AccountCursorAdapter extends SimpleCursorAdapter {
    public AccountCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return super.newView(context, cursor, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        double balance = cursor.getDouble(cursor.getColumnIndex(AccountancyContract.Transaction.AMOUNT));
        double start = cursor.getDouble(cursor.getColumnIndex(AccountancyContract.Account.START_BALANCE));
        TextView balanceTextView = (TextView)view.findViewById(R.id.account_list_item_balance);
        TextView startTextView = (TextView)view.findViewById(R.id.account_list_item_start);

        double currentBalance = balance + start;
        if (currentBalance >= 0)
            balanceTextView.setTextColor(Color.parseColor("#4CAF50"));
        else
            balanceTextView.setTextColor(Color.parseColor("#F44336"));

        balanceTextView.setText(String.format("%.2f %s", currentBalance, "$"));
        startTextView.setText(String.format("Start balance: %.2f %s", start, "$"));
    }
}
