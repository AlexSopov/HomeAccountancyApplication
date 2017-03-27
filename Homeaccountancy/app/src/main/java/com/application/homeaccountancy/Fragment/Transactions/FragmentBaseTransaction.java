package com.application.homeaccountancy.Fragment.Transactions;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.Html;

import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.Adapter.TransactionCursorAdapter;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.FilterSettings;
import com.application.homeaccountancy.R;

public class FragmentBaseTransaction extends ListFragment {
    protected final String fromJoinSelector = " FROM " + AccountancyContract.Transaction.TABLE_NAME +
            " INNER JOIN " + AccountancyContract.Category.TABLE_NAME + " " +
            " ON " + AccountancyContract.Category.TABLE_NAME + "." + AccountancyContract.Category._ID + " = " +
            AccountancyContract.Transaction.TABLE_NAME + "." + AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID + " " +
            " INNER JOIN " + AccountancyContract.Account.TABLE_NAME + " " +
            " ON " + AccountancyContract.Account.TABLE_NAME + "." + AccountancyContract.Account._ID + " = " +
            AccountancyContract.Transaction.TABLE_NAME + "." + AccountancyContract.Transaction.COLUMN_NAME_ACCOUNT_ID;


    protected final String baseQuery = "SELECT " + AccountancyContract.Transaction.TABLE_NAME + "." +
            AccountancyContract.Transaction._ID + AccountancyContract.COMMA_SEPARATOR +
            AccountancyContract.Category.COLUMN_NAME_ICON + AccountancyContract.COMMA_SEPARATOR +
            AccountancyContract.Category.COLUMN_NAME_TITLE + AccountancyContract.COMMA_SEPARATOR +
            AccountancyContract.Account.COLUMN_NAME_TITLE + AccountancyContract.COMMA_SEPARATOR +
            "strftime('%d.%m.%Y %H:%M'" + AccountancyContract.COMMA_SEPARATOR +
            AccountancyContract.Transaction.COLUMN_NAME_DATE  + ") as " +
            "'" + AccountancyContract.Transaction.COLUMN_NAME_DATE + "'" + AccountancyContract.COMMA_SEPARATOR +
            AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + AccountancyContract.COMMA_SEPARATOR +
            AccountancyContract.Transaction.COLUMN_NAME_NOTE + fromJoinSelector;


    SQLiteHandler sqLiteHandler;
    SQLiteDatabase db;
    Cursor cursor;

    private TextView filterIsEnabled;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        filterIsEnabled = (TextView) getActivity().findViewById(R.id.filter_is_enabled);

        sqLiteHandler = new SQLiteHandler(getActivity().getApplicationContext());
        db = sqLiteHandler.getReadableDatabase();

        setEmptyText(Html.fromHtml(getString(R.string.empty_text)));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (db != null)
            db.close();

        if (cursor != null)
            cursor.close();
    }

    protected void setAdapter(String query) {
        SimpleCursorAdapter transactionsCursorAdapter;
        cursor =  db.rawQuery(InitializeConditions(query), null);

        String[] from = new String[] {
                AccountancyContract.Category.COLUMN_NAME_ICON,
                AccountancyContract.Category.COLUMN_NAME_TITLE,
                AccountancyContract.Account.COLUMN_NAME_TITLE,
                AccountancyContract.Transaction.COLUMN_NAME_DATE,
                AccountancyContract.Transaction.COLUMN_NAME_AMOUNT,
                AccountancyContract.Transaction.COLUMN_NAME_NOTE,
        };

        int[] to = new int[] {
                R.id.transaction_list_item_icon,
                R.id.transaction_list_item_category,
                R.id.transaction_list_item_account,
                R.id.transaction_list_item_date,
                R.id.transaction_list_item_amount,
                R.id.transaction_list_item_note
        };

        transactionsCursorAdapter = new TransactionCursorAdapter(getActivity().getApplicationContext(),
                R.layout.transaction_list_item, cursor, from, to, 0);
        setListAdapter(transactionsCursorAdapter);
    }

    private String InitializeConditions(String query) {
        String orderSequence = " ORDER BY date(" + AccountancyContract.Transaction.COLUMN_NAME_DATE + ") DESC, " +
                "datetime(" + AccountancyContract.Transaction.COLUMN_NAME_DATE + ") DESC";
        if (!FilterSettings.isFilter) {
            filterIsEnabled.setVisibility(View.GONE);
            setEmptyText(Html.fromHtml(getString(R.string.empty_text)));
            return query + orderSequence;
        }
        else {
            filterIsEnabled.setVisibility(View.VISIBLE);
            filterIsEnabled.setText("Фильтр активен");
            setEmptyText(Html.fromHtml(getString(R.string.empty_text_filter)));
        }

        boolean isCondition = false;
        if (!query.contains("WHERE"))
            query += " WHERE ";
        else
            isCondition = true;

        if (FilterSettings.fromDateCheckBoxChecked) {
            if (isCondition) query += " AND ";
            query += AccountancyContract.Transaction.COLUMN_NAME_DATE + " >= '" +
                    String.format("%tY-%tm-%td 00:00", FilterSettings.dateFrom,
                            FilterSettings.dateFrom, FilterSettings.dateFrom) + "'";
            isCondition = true;
        }

        if (FilterSettings.tillDateCheckBoxChecked) {
            if (isCondition) query += " AND ";
            query += AccountancyContract.Transaction.COLUMN_NAME_DATE + " <= '" +
                    String.format("%tY-%tm-%td 23:59", FilterSettings.dateTill,
                            FilterSettings.dateTill, FilterSettings.dateTill) + "'";
            isCondition = true;
        }

        if (FilterSettings.accountCheckBoxChecked) {
            if (isCondition) query += " AND ";
            query += AccountancyContract.Transaction.COLUMN_NAME_ACCOUNT_ID + "=" +
                    String.valueOf(FilterSettings.accountID);
            isCondition = true;
        }

        if (FilterSettings.categoryCheckBoxChecked) {
            if (isCondition) query += " AND ";
            query += AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID + "=" +
                    String.valueOf(FilterSettings.categoryID);
        }

        return query + orderSequence;
    }
}
