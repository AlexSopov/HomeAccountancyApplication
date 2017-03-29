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

import org.w3c.dom.Text;


public class TransactionCursorAdapter extends SimpleCursorAdapter {
    public TransactionCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return super.newView(context, cursor, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        double amount = cursor.getDouble(cursor.getColumnIndex(AccountancyContract.Transaction.COLUMN_NAME_AMOUNT));
        String note = cursor.getString(cursor.getColumnIndex(AccountancyContract.Transaction.COLUMN_NAME_NOTE));
        TextView amountTextView = (TextView)view.findViewById(R.id.transaction_list_item_amount);
        TextView noteTextView = (TextView)view.findViewById(R.id.transaction_list_item_note);

        if (amount > 0)
            amountTextView.setTextColor(Color.parseColor("#4CAF50"));
        else
            amountTextView.setTextColor(Color.parseColor("#F44336"));
        amountTextView.setText(String.format("%.2f %s", amount, "руб."));

        if (note.isEmpty())
            noteTextView.setVisibility(View.GONE);
        else
            noteTextView.setVisibility(View.VISIBLE);
    }
}
