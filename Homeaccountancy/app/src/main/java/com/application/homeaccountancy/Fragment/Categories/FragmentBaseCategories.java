package com.application.homeaccountancy.Fragment.Categories;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.FilterSettings;
import com.application.homeaccountancy.R;
import com.application.homeaccountancy.activity.SingleCategoryActivity;

public class FragmentBaseCategories extends ListFragment {
    SQLiteHandler sqLiteHandler;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sqLiteHandler = new SQLiteHandler(getActivity().getApplicationContext());
        db = sqLiteHandler.getReadableDatabase();

        setEmptyText(Html.fromHtml(getString(R.string.empty_text)));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity().getApplicationContext(), SingleCategoryActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);

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
        SimpleCursorAdapter categoriesAdapter;
        cursor =  db.rawQuery(query, null);

        String[] from = new String[] {
                AccountancyContract.Category.COLUMN_NAME_TITLE,
                AccountancyContract.Category.COLUMN_NAME_ICON
        };

        int[] to = new int[] {
                R.id.category_list_item_title,
                R.id.category_list_item_icon
        };

        categoriesAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
                R.layout.category_list_item, cursor, from, to, 0);

        setListAdapter(categoriesAdapter);
    }
}
