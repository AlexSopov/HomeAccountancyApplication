package com.application.homeaccountancy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Fragment.FragmentCategories;
import com.application.homeaccountancy.R;

// Класс описывающий Activity просмотра ктегорий
public class CategoriesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация контента
        setContentView(R.layout.categories_activity);

        // Инициализация тулбара
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Инициализация адаптера постраничного просмотра
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Кнопка добавления новой категории
        FloatingActionButton addNewCategory = (FloatingActionButton) findViewById(R.id.add_new_category);
        addNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SingleCategoryActivity.class);
                startActivity(intent);
            }
        });

        // Элемент постраничного просмотра
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    // Адаптер для получения фрагментов страниц
    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // Генерация фрагметов, в котором определены данные
            // отделно для категорий трат и пополнений
            FragmentCategories fragmentCategories = null;

            switch (position) {
                case 0:
                    fragmentCategories = FragmentCategories.FragmentCategoriesFactory(
                            FragmentCategories.getBaseQuery() +
                                    " WHERE " + AccountancyContract.Category.IS_OUTGO + " = 1"
                    );
                    break;
                case 1:
                    fragmentCategories = FragmentCategories.FragmentCategoriesFactory(
                            FragmentCategories.getBaseQuery() +
                                    " WHERE " + AccountancyContract.Category.IS_OUTGO + " = 0"
                    );
                    break;
                default:
                    fragmentCategories = FragmentCategories.FragmentCategoriesFactory(
                            FragmentCategories.getBaseQuery()
                    );
            }
            return fragmentCategories;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Outgo";
                case 1:
                    return "Income";
            }
            return null;
        }
    }
}
