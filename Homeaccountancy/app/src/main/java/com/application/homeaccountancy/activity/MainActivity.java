package com.application.homeaccountancy.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.application.homeaccountancy.DateSelector;
import com.application.homeaccountancy.Fragment.FragmentTransactions;
import com.application.homeaccountancy.R;
import com.application.homeaccountancy.SMSParser.SMSParser;
import com.application.homeaccountancy.Utilities;

public class MainActivity extends UsingDataBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Объект для генерации временных периодов
    DateSelector dateSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация контента
        setContentView(R.layout.main_activity);

        // Инициализация тулраба
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Журнал");

        // Инициализация объекта для генерации временных периодов
        dateSelector = new DateSelector(this);

        // Чтение данных из смc
        initializeDataFromSMS();

        // Инициализация бокового меню
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Инициализация адаптера постраничного просмотра
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Элемент постраничного просмотра
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        // Кнопка для добавления нового платежа
        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.add_new_transaction);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            // В зависимости от страницы просмотра журнала
            // При добавлении новой запии определить категорию платежа
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SingleTransactionActivity.class);
                final int[] category = new int[1];
                switch (mViewPager.getCurrentItem()) {
                    case 1:
                        intent.putExtra("category", 1);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.putExtra("category", 0);
                        startActivity(intent);
                        break;
                    case 0:
                    default:
                        getCategoryTypeFromDialog(intent).show();
                }
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onBackPressed() {
        // Обработка нажатия на кнопку "назад"
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Обработка нажатия на элемент бокового меню
        // Вызвать соответствующее Activity
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

    private void initializeDataFromSMS() {
        // Чтение всех смс
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"),
                null, null, null, null);

        SMSParser smsParser = new SMSParser(getApplicationContext(), db);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Получение текста, даты и id смс
                String smsMessage = cursor.getString(cursor.getColumnIndex("body"));
                long time = cursor.getLong(cursor.getColumnIndex("date"));

                // Обработка смс
                if (smsParser.handleSMS(smsMessage.trim(), time))
                    count++;
            }
            while (cursor.moveToNext());
            cursor.close();

            if (count > 0)
                Utilities.makeToast(getApplicationContext(), String.format("Было добавлено %d платежей из смс", count));
        }
    }
    public DateSelector getDateSelector() {
        return dateSelector;
    }

    // Адаптер для получения фрагментов страниц
    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            FragmentTransactions fragmentTransaction;

            // Генерация фрагметов, в котором определены данные
            // отделно для всех платежей, трат и пополнений
            switch (position) {
                case 0:
                    fragmentTransaction = FragmentTransactions.FragmentTransactionsFactory(
                            FragmentTransactions.getBaseQuery()
                    );
                    break;
                case 1:
                    fragmentTransaction = FragmentTransactions.FragmentTransactionsFactory(
                            FragmentTransactions.getBaseQuery() + " WHERE is_outgo = 1"
                    );
                    break;
                case 2:
                    fragmentTransaction = FragmentTransactions.FragmentTransactionsFactory(
                            FragmentTransactions.getBaseQuery() + " WHERE is_outgo = 0"
                    );
                    break;
                default:
                    fragmentTransaction = FragmentTransactions.FragmentTransactionsFactory(
                            FragmentTransactions.getBaseQuery()
                    );
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

    private AlertDialog getCategoryTypeFromDialog(final Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите тип платежа");
        builder.setItems(new String[]{"Пополнение", "Трата"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intent.putExtra("category", which);
                startActivity(intent);
            }
        });
        return builder.create();
    }
}