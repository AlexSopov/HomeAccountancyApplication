package com.application.homeaccountancy.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.R;
import com.application.homeaccountancy.activity.SingleCategoryActivity;

public class FragmentCategories extends ListFragment {
    SQLiteHandler sqLiteHandler;
    SQLiteDatabase db;
    Cursor cursor;

    String query;
    SimpleCursorAdapter categoriesAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sqLiteHandler = new SQLiteHandler(getActivity().getApplicationContext());
        db = sqLiteHandler.getReadableDatabase();

        setEmptyText(Html.fromHtml(getResources().getString(R.string.empty_text)));

        String[] from = new String[] {
                AccountancyContract.Category.COLUMN_NAME_TITLE,
                AccountancyContract.Category.COLUMN_NAME_ICON
        };

        int[] to = new int[] {
                R.id.category_list_item_title,
                R.id.category_list_item_icon
        };

        setCursor();
        categoriesAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
                R.layout.category_list_item, cursor, from, to, 0);
        setListAdapter(categoriesAdapter);

        registerForContextMenu(getListView());
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

        final long id = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).id;
        switch (item.getItemId()) {
            case R.id.change:
                Intent intent = new Intent(getActivity().getApplicationContext(), SingleCategoryActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                return true;
            case R.id.delete:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog
                        .setTitle("Подтверждение действия")
                        .setMessage("Вы действительно хотите удалить категорию? Все записи данной " +
                                "категории будут стерты.")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.delete(AccountancyContract.Category.TABLE_NAME,
                                        AccountancyContract.Category._ID + "=?",
                                        new String[]{String.valueOf(id)}
                                );
                                setCursor();
                                categoriesAdapter.changeCursor(cursor);
                                categoriesAdapter.notifyDataSetChanged();
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
    public void onResume() {
        super.onResume();

        setCursor();
        categoriesAdapter.changeCursor(cursor);
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
        cursor =  db.rawQuery(query, null);
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
