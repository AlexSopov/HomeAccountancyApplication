package com.application.homeaccountancy.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.Adapter.TransactionCursorAdapter;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.FilterSettings;
import com.application.homeaccountancy.R;
import com.application.homeaccountancy.activity.FilterActivity;
import com.application.homeaccountancy.activity.SingleTransactionActivity;

import java.util.Calendar;
import java.util.List;

public class FragmentTransactions extends ListFragment {
    public static final String fromJoinSelector = " FROM " + AccountancyContract.Transaction.TABLE_NAME +
            " INNER JOIN " + AccountancyContract.Category.TABLE_NAME + " " +
            " ON " + AccountancyContract.Category.TABLE_NAME + "." + AccountancyContract.Category._ID + " = " +
            AccountancyContract.Transaction.TABLE_NAME + "." + AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID + " " +
            " INNER JOIN " + AccountancyContract.Account.TABLE_NAME + " " +
            " ON " + AccountancyContract.Account.TABLE_NAME + "." + AccountancyContract.Account._ID + " = " +
            AccountancyContract.Transaction.TABLE_NAME + "." + AccountancyContract.Transaction.COLUMN_NAME_ACCOUNT_ID;


    public static final String baseQuery = "SELECT " + AccountancyContract.Transaction.TABLE_NAME + "." +
            AccountancyContract.Transaction._ID + AccountancyContract.COMMA +
            AccountancyContract.Category.COLUMN_NAME_ICON + AccountancyContract.COMMA +
            AccountancyContract.Category.COLUMN_NAME_TITLE + AccountancyContract.COMMA +
            AccountancyContract.Account.COLUMN_NAME_TITLE + AccountancyContract.COMMA +
            "strftime('%d.%m.%Y'" + AccountancyContract.COMMA +
            AccountancyContract.Transaction.COLUMN_NAME_DATE  + ") as " +
            "'" + AccountancyContract.Transaction.COLUMN_NAME_DATE + "'" + AccountancyContract.COMMA +
            AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + AccountancyContract.COMMA +
            AccountancyContract.Transaction.COLUMN_NAME_NOTE + fromJoinSelector;

    String[] humanizeMonthSingle = new String[] {"Январь", "Февраль", "Март", "Апрель", "Май",
            "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    String[] humanizeMonthPersonable = new String[] {"Января", "Февраля", "Марта",
            "Апреля", "Июня", "Июля", "Августа", "Сентября", "Октября",
            "Ноября", "Декабря" };

    public String query;

    SimpleCursorAdapter transactionsCursorAdapter;

    SQLiteHandler sqLiteHandler;
    SQLiteDatabase db;
    Cursor cursor;

    private TextView filterIsEnabled;
    TextView humanizeDate, formatDate;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        registerForContextMenu(getListView());

        filterIsEnabled = (TextView) getActivity().findViewById(R.id.filter_is_enabled);

        sqLiteHandler = new SQLiteHandler(getActivity().getApplicationContext());
        db = sqLiteHandler.getReadableDatabase();

        humanizeDate = (TextView) getActivity().findViewById(R.id.humanize_date);
        formatDate = (TextView) getActivity().findViewById(R.id.format_date);

        FilterSettings.initialize();
        getActivity().findViewById(R.id.dec_date_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseDate(v);
                transactionsCursorAdapter.changeCursor(cursor);
                getListView().invalidate();
            }
        });
        getActivity().findViewById(R.id.inc_date_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseDate(v);
                transactionsCursorAdapter.changeCursor(cursor);
                getListView().invalidate();
            }
        });

        setEmptyText(Html.fromHtml(getString(R.string.empty_text)));
        setDateTimeView();



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

        setCursor();
        transactionsCursorAdapter = new TransactionCursorAdapter(getActivity().getApplicationContext(),
                R.layout.transaction_list_item, cursor, from, to, 0);

        setListAdapter(transactionsCursorAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        setCursor();
        transactionsCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(!item.isChecked())
            item.setChecked(true);

        boolean isNew = false;
        switch(id){
            case R.id.chart_day :
                FilterSettings.changeFieldInterval = Calendar.DAY_OF_YEAR;
                isNew = true;
                break;
            case R.id.chart_week :
                FilterSettings.changeFieldInterval = Calendar.WEEK_OF_YEAR;
                isNew = true;
                break;
            case R.id.chart_month :
                FilterSettings.changeFieldInterval = Calendar.MONTH;
                isNew = true;
                break;
            case R.id.chart_year :
                FilterSettings.changeFieldInterval = Calendar.YEAR;
                isNew = true;
                break;
            case R.id.filter:
                Intent intent = new Intent(getActivity().getApplicationContext(), FilterActivity.class);
                startActivity(intent);
                break;
        }

        if (isNew) {
            FilterSettings.calendarFrom = Calendar.getInstance();
            FilterSettings.calendarTill = Calendar.getInstance();
            dateChange(0);
            onResume();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (!getUserVisibleHint())
            return false;


        final long id = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).id;
        switch (item.getItemId()) {
            case R.id.change:
                Intent intent = new Intent(getActivity().getApplicationContext(), SingleTransactionActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                return true;
            case R.id.delete:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog
                        .setTitle("Подтверждение действия")
                        .setMessage("Вы действительно хотите удалить запись?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.delete(AccountancyContract.Transaction.TABLE_NAME,
                                        AccountancyContract.Transaction._ID + "=?",
                                        new String[] {String.valueOf(id)}
                                );
                                setCursor();
                                transactionsCursorAdapter.changeCursor(cursor);
                            }
                        })
                        .setNegativeButton("Нет", null)
                        .create();
                dialog.show();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (db != null)
            db.close();

        if (cursor != null)
            cursor.close();
    }

    protected void setCursor() {
        cursor =  db.rawQuery(InitializeConditions(query), null);
    }
    private String InitializeConditions(String query) {
        String orderSequence = " ORDER BY date(" + AccountancyContract.Transaction.COLUMN_NAME_DATE + ") DESC, " +
                "datetime(" + AccountancyContract.Transaction.COLUMN_NAME_DATE + ") DESC";

        String fromDate = String.format("%tY-%tm-%td 00:00", FilterSettings.calendarFrom,
                FilterSettings.calendarFrom, FilterSettings.calendarFrom);
        String tillDate = String.format("%tY-%tm-%td 23:59", FilterSettings.calendarTill,
                FilterSettings.calendarTill, FilterSettings.calendarTill);

        String whereSelector;
        if (query.contains("WHERE"))
            whereSelector= " AND " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " >= '" +
                    fromDate + "' AND " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " <= '" +
                    tillDate + "'";
        else
            whereSelector= " WHERE " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " >= '" +
                fromDate + "' AND " + AccountancyContract.Transaction.COLUMN_NAME_DATE + " <= '" +
                tillDate + "'";
        /*if (!FilterSettings.isFilter) {
            filterIsEnabled.setVisibility(View.GONE);
            setEmptyText(Html.fromHtml(getResources().getString(R.string.empty_text)));
            //return query + orderSequence;
        }
        else {
            filterIsEnabled.setVisibility(View.VISIBLE);
            filterIsEnabled.setText("Фильтр активен");
            setEmptyText(Html.fromHtml(getResources().getString(R.string.empty_text_filter)));
        }*/

        /*boolean isCondition = false;
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
        }*/

        return query + whereSelector + orderSequence;
    }

    private void setDateTimeView() {
        formatDate.setText(String.format("%td.%tm.%tY - %td.%tm.%tY",
                FilterSettings.calendarFrom, FilterSettings.calendarFrom, FilterSettings.calendarFrom,
                FilterSettings.calendarTill, FilterSettings.calendarTill, FilterSettings.calendarTill));

        switch (FilterSettings.changeFieldInterval) {
            case Calendar.DAY_OF_YEAR:
                humanizeDate.setText(String.format("%d %s %d",
                        FilterSettings.calendarFrom.get(Calendar.DAY_OF_MONTH),
                        humanizeMonthPersonable[FilterSettings.calendarFrom.get(Calendar.MONTH)],
                        FilterSettings.calendarFrom.get(Calendar.YEAR)));
                break;
            case Calendar.WEEK_OF_YEAR:
                humanizeDate.setText(String.format("%d неделя %d",
                        FilterSettings.calendarFrom.get(Calendar.WEEK_OF_YEAR),
                        FilterSettings.calendarFrom.get(Calendar.YEAR)));
                break;
            case Calendar.MONTH:
                humanizeDate.setText(String.format("%s %d",
                        humanizeMonthSingle[FilterSettings.calendarFrom.get(Calendar.MONTH)],
                        FilterSettings.calendarFrom.get(Calendar.YEAR)));
                break;
            case Calendar.YEAR:
                humanizeDate.setText(String.format("%d год",
                        FilterSettings.calendarFrom.get(Calendar.YEAR)));
                break;
        }
    }
    private void dateChange(int delta) {
        switch (FilterSettings.changeFieldInterval) {
            case Calendar.DAY_OF_YEAR:
                FilterSettings.calendarTill.set(Calendar.DAY_OF_YEAR, FilterSettings.calendarTill.get(Calendar.DAY_OF_YEAR) + delta);
                FilterSettings.calendarFrom.set(Calendar.DAY_OF_YEAR, FilterSettings.calendarTill.get(Calendar.DAY_OF_YEAR));
                FilterSettings.calendarFrom.set(Calendar.YEAR, FilterSettings.calendarTill.get(Calendar.YEAR));
                break;
            case Calendar.WEEK_OF_YEAR:
                FilterSettings.calendarTill.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                FilterSettings.calendarTill.set(Calendar.WEEK_OF_YEAR, FilterSettings.calendarTill.get(Calendar.WEEK_OF_YEAR) + delta);
                FilterSettings.calendarFrom.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                FilterSettings.calendarFrom.set(Calendar.WEEK_OF_YEAR, FilterSettings.calendarTill.get(Calendar.WEEK_OF_YEAR));
                FilterSettings.calendarFrom.set(Calendar.YEAR, FilterSettings.calendarTill.get(Calendar.YEAR));
                break;
            case Calendar.MONTH:
                FilterSettings.calendarTill.set(Calendar.DAY_OF_MONTH, FilterSettings.calendarTill.getActualMinimum(Calendar.DAY_OF_MONTH));
                FilterSettings.calendarTill.set(Calendar.MONTH, FilterSettings.calendarTill.get(Calendar.MONTH) + delta);
                FilterSettings.calendarTill.set(Calendar.DAY_OF_MONTH, FilterSettings.calendarTill.getActualMaximum(Calendar.DAY_OF_MONTH));
                FilterSettings.calendarFrom.set(Calendar.DAY_OF_MONTH, FilterSettings.calendarFrom.getActualMinimum(Calendar.DAY_OF_MONTH));
                FilterSettings.calendarFrom.set(Calendar.MONTH, FilterSettings.calendarTill.get(Calendar.MONTH));
                FilterSettings.calendarFrom.set(Calendar.YEAR, FilterSettings.calendarTill.get(Calendar.YEAR));
                break;
            case Calendar.YEAR:
                FilterSettings.calendarTill.set(Calendar.DAY_OF_YEAR, FilterSettings.calendarTill.getActualMinimum(Calendar.DAY_OF_YEAR));
                FilterSettings.calendarTill.set(Calendar.YEAR, FilterSettings.calendarTill.get(Calendar.YEAR) + delta);
                FilterSettings.calendarTill.set(Calendar.DAY_OF_YEAR, FilterSettings.calendarTill.getActualMaximum(Calendar.DAY_OF_YEAR));
                FilterSettings.calendarFrom.set(Calendar.DAY_OF_YEAR, FilterSettings.calendarFrom.getActualMinimum(Calendar.DAY_OF_YEAR));
                FilterSettings.calendarFrom.set(Calendar.YEAR, FilterSettings.calendarTill.get(Calendar.YEAR));
                break;
        }
        setDateTimeView();
    }
    public void decreaseDate(View view) {
        dateChange(-1);
        updateCursors();
    }
    public void increaseDate(View view) {
        dateChange(1);
        updateCursors();
    }
    private void updateCursors() {
        List<Fragment> fragmentList = getFragmentManager().getFragments();
        for (int i = 0; i < fragmentList.size(); i++) {
            FragmentTransactions fragmentTransactions = (FragmentTransactions)fragmentList.get(i);
            fragmentTransactions.setCursor();
            fragmentTransactions.transactionsCursorAdapter.changeCursor(
                    fragmentTransactions.cursor
            );

        }
    }

    public void setQuery(String query) {
        this.query = query;
    }
}