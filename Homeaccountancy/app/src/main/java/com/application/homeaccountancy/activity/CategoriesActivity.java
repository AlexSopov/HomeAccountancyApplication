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

public class CategoriesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        FloatingActionButton addNewCategory = (FloatingActionButton) findViewById(R.id.add_new_category);
        addNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SingleCategoryActivity.class);
                startActivity(intent);
            }
        });

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
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
                    return "Траты";
                case 1:
                    return "Пополнения";
            }
            return null;
        }
    }
}
