package com.application.homeaccountancy.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.Adapter.TransactionCursorAdapter;
import com.application.homeaccountancy.DateSelector;
import com.application.homeaccountancy.R;
import com.application.homeaccountancy.Activity.MainActivity;
import com.application.homeaccountancy.Activity.SingleTransactionActivity;

import java.util.Calendar;
import java.util.List;

public class FragmentTransactions extends UsingDataBaseListFragment {
    private String query;
    private SimpleCursorAdapter transactionsCursorAdapter;
    private DateSelector dateSelector;

    // Фабричный метод для создания фрагмента со списком платежей
    // Удовлетворяющих запросу query
    public static FragmentTransactions FragmentTransactionsFactory(String query) {
        FragmentTransactions fragmentTransactions = new FragmentTransactions();
        fragmentTransactions.setQuery(query);
        return fragmentTransactions;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        registerForContextMenu(getListView());

        // Инициализация генератора времменых периодов
        dateSelector = ((MainActivity)getActivity()).getDateSelector();
        dateSelector.setOnDecreaseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseDate(v);
            }
        });
        dateSelector.setOnIncreaseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseDate(v);
            }
        });

        setEmptyText(Html.fromHtml(getString(R.string.empty_text)));
        initializeAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();

        requeryCursor();
        transactionsCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.date_selector_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Обработка нажатия по элементу в меню

        if(!item.isChecked())
            item.setChecked(true);

        boolean isChangeState = true;
        // В зависимости от нажатого элемента - изменить переменную
        // для итерации
        // Изменить временной диапазон
        switch(id){
            case R.id.range_day:
                dateSelector.setChangeFieldInterval(Calendar.DAY_OF_YEAR);
                break;
            case R.id.range_week:
                dateSelector.setChangeFieldInterval(Calendar.WEEK_OF_YEAR);
                break;
            case R.id.range_month:
                dateSelector.setChangeFieldInterval(Calendar.MONTH);
                break;
            case R.id.range_year:
                dateSelector.setChangeFieldInterval(Calendar.YEAR);
                break;
            default:
                isChangeState = false;
        }
        if (isChangeState) {
            dateSelector.resetState();
            onResume();
            updateCursors();
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

        // Обработка нажатия на элементе из контекстного меню

        // Id нажатого элемента
        final long id = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).id;
        switch (item.getItemId()) {
            case R.id.change:
                // Если нажата кнопка "Изменить"
                // Вызвать Activity изменения категории и передать Id нажатого элемента
                Intent intent = new Intent(getActivity().getApplicationContext(), SingleTransactionActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                return true;
            case R.id.delete:
                // Если нажата кнопка "Удалить"

                // Создать диалоговое окно для подтверждения действия
                // и удалить элемент в случае необходимости
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
                                requeryCursor();
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

    public void setQuery(String query) {
        this.query = query;
    }
    public static String getBaseQuery() {
        String fromJoinSelector = " FROM " + AccountancyContract.Transaction.TABLE_NAME +
                " INNER JOIN " + AccountancyContract.Category.TABLE_NAME + " " +
                " ON " + AccountancyContract.Category.TABLE_NAME + "." + AccountancyContract.Category._ID + " = " +
                AccountancyContract.Transaction.TABLE_NAME + "." + AccountancyContract.Transaction.CATEGORY_ID + " " +
                " INNER JOIN " + AccountancyContract.Account.TABLE_NAME + " " +
                " ON " + AccountancyContract.Account.TABLE_NAME + "." + AccountancyContract.Account._ID + " = " +
                AccountancyContract.Transaction.TABLE_NAME + "." + AccountancyContract.Transaction.ACCOUNT_ID;


        return "SELECT " + AccountancyContract.Transaction.TABLE_NAME + "." +
                AccountancyContract.Transaction._ID + AccountancyContract.COMMA +
                AccountancyContract.Category.ICON + AccountancyContract.COMMA +
                AccountancyContract.Category.C_TITLE + AccountancyContract.COMMA +
                AccountancyContract.Account.A_TITLE + AccountancyContract.COMMA +
                "strftime('%d.%m.%Y'" + AccountancyContract.COMMA +
                AccountancyContract.Transaction.DATE + ") as " +
                "'" + AccountancyContract.Transaction.DATE + "'" + AccountancyContract.COMMA +
                AccountancyContract.Transaction.AMOUNT + AccountancyContract.COMMA +
                AccountancyContract.Transaction.NOTE + fromJoinSelector;
    }

    private void initializeAdapter() {
        String[] from = new String[] {
                AccountancyContract.Category.ICON,
                AccountancyContract.Category.C_TITLE,
                AccountancyContract.Account.A_TITLE,
                AccountancyContract.Transaction.DATE,
                AccountancyContract.Transaction.AMOUNT,
                AccountancyContract.Transaction.NOTE,
        };

        int[] to = new int[] {
                R.id.transaction_list_item_icon,
                R.id.transaction_list_item_category,
                R.id.transaction_list_item_account,
                R.id.transaction_list_item_date,
                R.id.transaction_list_item_amount,
                R.id.transaction_list_item_note
        };

        requeryCursor();
        transactionsCursorAdapter = new TransactionCursorAdapter(getActivity().getApplicationContext(),
                R.layout.transaction_list_item, cursor, from, to, 0);

        setListAdapter(transactionsCursorAdapter);
    }

    private void requeryCursor() {
        // Обновление данных в курсоре
        cursor =  db.rawQuery(initializeConditions(query), null);
    }
    private String initializeConditions(String query) {
        String orderSequence = " ORDER BY date(" + AccountancyContract.Transaction.DATE + ") DESC, " +
                "datetime(" + AccountancyContract.Transaction.DATE + ") DESC";

        String fromDate = dateSelector.getFromString();
        String tillDate = dateSelector.getTillString();

        String whereSelector = "";
        if (!query.contains("WHERE"))
            whereSelector += " WHERE ";
        else
            whereSelector += " AND ";

        whereSelector += AccountancyContract.Transaction.DATE + " >= '" +
                fromDate + "' AND " + AccountancyContract.Transaction.DATE + " <= '" +
                tillDate + "'";

        return query + whereSelector + orderSequence;
    }

    private void decreaseDate(View view) {
        // Уменьшение временного периода
        dateSelector.dateChange(-1);
        updateCursors();

        transactionsCursorAdapter.changeCursor(cursor);
    }
    private void increaseDate(View view) {
        // Увеличение временного периода
        dateSelector.dateChange(1);
        updateCursors();

        transactionsCursorAdapter.changeCursor(cursor);
    }
    private void updateCursors() {
        // Обновить данные во всех фрагментах, смежных с текущим
        List<Fragment> fragmentList = getFragmentManager().getFragments();

        for (int i = 0; i < fragmentList.size(); i++) {
            FragmentTransactions fragmentTransactions = (FragmentTransactions)fragmentList.get(i);
            fragmentTransactions.requeryCursor();
            fragmentTransactions.transactionsCursorAdapter.changeCursor(
                    fragmentTransactions.cursor
            );
        }
    }
}