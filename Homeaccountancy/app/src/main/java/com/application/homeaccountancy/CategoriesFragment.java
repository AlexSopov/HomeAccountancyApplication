package com.application.homeaccountancy;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;

public class CategoriesFragment extends Fragment {
    ListView categoriesOutgo, categoriesIncome;

    SQLiteHandler sqLiteHandler;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categories_content, container, false);

        categoriesOutgo = (ListView)view.findViewById(R.id.categories_outgo);
        categoriesIncome = (ListView)view.findViewById(R.id.categories_income);

        sqLiteHandler = new SQLiteHandler(getActivity().getApplicationContext());
        db = sqLiteHandler.getReadableDatabase();

        TabHost tabHost = (TabHost) view.findViewById(R.id.tabs_categories);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tabs_categories_tab1");
        tabSpec.setIndicator("Затраты");
        tabSpec.setContent(R.id.tabs_categories_tab_1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tabs_categories_tab2");
        tabSpec.setIndicator("Доходы");
        tabSpec.setContent(R.id.tabs_categories_tab_2);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SimpleCursorAdapter categoriesOutgoAdapter, categoriesIncomesAdapter;


        String query = "SELECT * FROM " + AccountancyContract.Category.TABLE_NAME +
                " WHERE " + AccountancyContract.Category.COLUMN_NAME_IS_OUTGO + "=1";
        cursor = db.rawQuery(query, null);
        categoriesOutgoAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
                R.layout.category_list_item, cursor,
                new String[]{AccountancyContract.Category.COLUMN_NAME_TITLE,
                        AccountancyContract.Category.COLUMN_NAME_ICON},
                new int[]{R.id.category_list_item_title, R.id.category_list_item_icon}, 0);
        categoriesOutgo.setAdapter(categoriesOutgoAdapter);


        query = "SELECT * FROM " + AccountancyContract.Category.TABLE_NAME +
                " WHERE " + AccountancyContract.Category.COLUMN_NAME_IS_OUTGO + "=0";
        cursor = db.rawQuery(query, null);
        categoriesIncomesAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
                R.layout.category_list_item, cursor,
                new String[]{AccountancyContract.Category.COLUMN_NAME_TITLE,
                        AccountancyContract.Category.COLUMN_NAME_ICON},
                new int[]{R.id.category_list_item_title, R.id.category_list_item_icon}, 0);
        categoriesIncome.setAdapter(categoriesIncomesAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        db.close();
        cursor.close();
    }
}
