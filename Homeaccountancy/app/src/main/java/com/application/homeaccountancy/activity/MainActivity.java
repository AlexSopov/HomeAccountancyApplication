package com.application.homeaccountancy.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.DateSelector;
import com.application.homeaccountancy.Fragment.FragmentTransactions;
import com.application.homeaccountancy.R;
import com.application.homeaccountancy.Utilities;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        // Кнопка для добавления нового платежа
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

        if (cursor != null && cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                // Получение текста и id смс
                String smsMessage = cursor.getString(cursor.getColumnIndex("body"));
                long smsID = cursor.getLong(cursor.getColumnIndex("_id"));

                // Проверка на ссответствие текста сообщения паттерну
                Pattern pattern = Pattern.compile("Pattern");
                Matcher matcher = pattern.matcher(smsMessage);

                // Если тест совпадает - занести данные про платеж
                //if (!matcher.matches()) continue;

                // Проверка, обработано ли ранее данное смс
                Cursor cursorSMS = db.rawQuery("SELECT * FROM " +
                        AccountancyContract.SMS.TABLE_NAME +
                        " WHERE " + AccountancyContract.SMS._ID + "=" + smsID, null);

                if (cursorSMS.getCount() == 0) {
                    // Получение данных платежа
                    long accountID = getSMSAccountID(smsMessage);
                    if (accountID <= 0) {
                        cursorSMS.close();
                        continue;
                    }
                    long categoryID = getSMSCategoryID(smsMessage);
                    double amount = getSMSAmount(smsMessage);

                    // Занести данные в базу данных
                    ContentValues contentValuesTransaction = new ContentValues();
                    contentValuesTransaction.put(AccountancyContract.Transaction.DATE, Utilities.getSQLiteTimeString(Calendar.getInstance()));
                    contentValuesTransaction.put(AccountancyContract.Transaction.AMOUNT, amount);
                    contentValuesTransaction.put(AccountancyContract.Transaction.ACCOUNT_ID, accountID);
                    contentValuesTransaction.put(AccountancyContract.Transaction.CATEGORY_ID, categoryID);
                    db.insert(AccountancyContract.Transaction.TABLE_NAME, null, contentValuesTransaction);

                    // Пометить сообщение как проанализированное
                    ContentValues contentValuesSMS = new ContentValues();
                    contentValuesSMS.put(AccountancyContract.SMS.COLUMN_NAME_SMS_ID, smsID);
                    db.insert(AccountancyContract.SMS.TABLE_NAME, null, contentValuesSMS);
                }
                cursorSMS.close();

            } while (cursor.moveToNext());

            cursor.close();
        }
    }
    private long getSMSAccountID(String message) {
        // Получение id счета из смс
        long id = -1;
        String smsAccount = getSMSAccount(message);
        if (smsAccount.isEmpty())
            return id;


        Cursor accountCursor = db.rawQuery("SELECT " +
                AccountancyContract.Account._ID + " FROM " +
                AccountancyContract.Account.TABLE_NAME +
                " WHERE " + AccountancyContract.Account.A_TITLE + "=" +
                getSMSAccount(message), null);

        if (accountCursor.moveToFirst())
            id = accountCursor.getLong(cursor.getColumnIndex(AccountancyContract.Account._ID));

        accountCursor.close();
        return id;
    }
    private long getSMSCategoryID(String message) {
        long id = -1;
        double amount = getSMSAmount(message);

        Cursor categoryCursor;
        String categoryQuery = "SELECT " + AccountancyContract.Category._ID + " FROM " +
                AccountancyContract.Category.TABLE_NAME + " WHERE " +
                AccountancyContract.Category.C_TITLE + "=%s";

        // Поиск "нулевых" категорий в зависимости от суммы платежа
        if (amount >= 0)
            categoryCursor = db.rawQuery(String.format(categoryQuery, "Прочие доходы"), null);
        else
            categoryCursor = db.rawQuery(String.format(categoryQuery, "Прочие затраты"), null);

        // Если категория найдена - вернуть её id
        // Иначе - создать новую категори
        if (categoryCursor.moveToFirst())
            id = cursor.getLong(cursor.getColumnIndex(AccountancyContract.Category._ID));
        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AccountancyContract.Category.ICON, R.drawable.others);

            if (amount >= 0) {
                contentValues.put(AccountancyContract.Category.C_TITLE, "Прочие затраты");
                contentValues.put(AccountancyContract.Category.IS_OUTGO, 0);
            }
            else {
                contentValues.put(AccountancyContract.Category.C_TITLE, "Прочие доходы");
                contentValues.put(AccountancyContract.Category.IS_OUTGO, 1);
            }
            id = db.insert(AccountancyContract.Category.TABLE_NAME, null, contentValues);
        }
        categoryCursor.close();

        return id;
    }
    private String getSMSAccount(String message) {
        // Получение счёте из смс
        Pattern pattern = Pattern.compile("pattern_account");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }
    private double getSMSAmount(String message) {
        // Получение суммы из смс
        Pattern pattern = Pattern.compile("pattern_amount");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(0));
        }
        return 0;
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
}