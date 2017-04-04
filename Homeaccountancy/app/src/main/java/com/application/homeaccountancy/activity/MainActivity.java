package com.application.homeaccountancy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.application.homeaccountancy.DateSelector;
import com.application.homeaccountancy.Fragment.FragmentTransactions;
import com.application.homeaccountancy.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DateSelector dateSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateSelector = new DateSelector(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);


        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.add_new_transaction);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SingleTransactionActivity.class);
                startActivity(intent);
            }
        });
        floatingActionButton.setVerticalFadingEdgeEnabled(true);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_categories) {
            Intent intent = new Intent(getApplicationContext(), CategoriesActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menu_pie_graphic) {
            Intent intent = new Intent(getApplicationContext(), PieCharActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menu_bar_graphic) {
            Intent intent = new Intent(getApplicationContext(), BarChartActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menu_accounts) {
            Intent intent = new Intent(getApplicationContext(), AccountsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public DateSelector getDateSelector() {
        return dateSelector;
    }

    /* FragmentPagerAdapter */
    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            FragmentTransactions fragmentTransaction = new FragmentTransactions();
            switch (position) {
                case 0:
                    fragmentTransaction.setQuery(FragmentTransactions.getBaseQuery());
                    break;
                case 1:
                    fragmentTransaction.setQuery(FragmentTransactions.getBaseQuery() +
                            " WHERE is_outgo = 1");
                    break;
                case 2:
                    fragmentTransaction.setQuery(FragmentTransactions.getBaseQuery() +
                            " WHERE is_outgo = 0");
                    break;
                default:
                    fragmentTransaction.setQuery(FragmentTransactions.getBaseQuery());
            }
            return fragmentTransaction;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Все";
                case 1:
                    return "Траты";
                case 2:
                    return "Пополнения";
            }
            return null;
        }
    }
}