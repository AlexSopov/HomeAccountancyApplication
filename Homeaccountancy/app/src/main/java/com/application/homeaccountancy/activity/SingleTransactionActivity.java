package com.application.homeaccountancy.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.R;
import com.application.homeaccountancy.Utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// Класс, представляющий Платеж
public class SingleTransactionActivity extends SingleEntityActivity {
    // Переменные представления
    private TextView currentDateTextView, currentTimeTextView;
    private Spinner categoriesSpinner, accountsSpinner;
    private EditText transactionAmountEditText, noteEditTextEditText;
    private Button signButton;

    // Слушатели выбора даты и времени
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    // Время платежа
    private Calendar dateTime;

    // Отрицательная сумма
    private boolean isNegativeAmount;

    // ID категории и счета платежа
    private long categoryID, accountID;

    // Категория платежа (трата/пополнение)
    private int categoryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация контента
        setContentView(R.layout.single_transaction_activity);

        // Инициализация тулбара
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Инициализация элементов представлений и слушателей
        initializeViews();
        initializeListeners();

        dateTime = Calendar.getInstance();
        isNegativeAmount = true;

        // Получение типа платежа
        categoryType = getCategoryType();
        if (categoryType == 0)
            signButton.callOnClick();


        // Если данное Activity было вызвано для изменения
        // созданной категории, то заполнить поля данными
        if (isEntityId()) {
            cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Transaction.TABLE_NAME +
                    " WHERE " + AccountancyContract.Transaction._ID + "=?", new String[] {String.valueOf(getEntityId())});
            cursor.moveToFirst();

            String date = cursor.getString(cursor.getColumnIndex(AccountancyContract.Transaction.DATE));
            int amount = cursor.getInt(cursor.getColumnIndex(AccountancyContract.Transaction.AMOUNT));
            categoryID = cursor.getLong(cursor.getColumnIndex(AccountancyContract.Transaction.CATEGORY_ID));
            accountID = cursor.getLong(cursor.getColumnIndex(AccountancyContract.Transaction.ACCOUNT_ID));
            String note = cursor.getString(cursor.getColumnIndex(AccountancyContract.Transaction.NOTE));

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

            Date time = null;
            try {
                time = formatter.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateTime.setTime(time);

            transactionAmountEditText.setText(String.valueOf(Math.abs(amount)));
            noteEditTextEditText.setText(note);
        }
        setInitialDateTime();
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Инициализация выпадающих списков
        initializeSpinners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обработка нажатия на элементе из меню
        int id = item.getItemId();
        if(!item.isChecked())
            item.setChecked(true);

        if (id == R.id.delete) {
            // Если нажат элемент "Удалить"

            // Создать диалоговое окно для подтверждения действия
            // и удалить элемент в случае необходимости
            AlertDialog.Builder dialog = new AlertDialog.Builder(SingleTransactionActivity.this);
            dialog
                    .setTitle("Подтверждение действия")
                    .setMessage("Вы действительно хотите удалить данную запись?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.delete(AccountancyContract.Transaction.TABLE_NAME,
                                    AccountancyContract.Transaction._ID + "=?",
                                    new String[] {String.valueOf(getEntityId())}
                            );
                            finish();
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .create();
            dialog.show();
        }
        return true;
    }

    private void initializeViews() {
        // Инициализировать переменные представления
        categoriesSpinner = (Spinner)findViewById(R.id.transaction_category);
        accountsSpinner = (Spinner)findViewById(R.id.transaction_account);
        currentDateTextView = (TextView)findViewById(R.id.transaction_date);
        currentTimeTextView = (TextView)findViewById(R.id.transaction_time);
        transactionAmountEditText = (EditText)findViewById(R.id.transaction_sum);
        noteEditTextEditText = (EditText)findViewById(R.id.transaction_note);
        signButton = (Button) findViewById(R.id.button_sign);
    }
    private void initializeSpinners() {
        // Инициализировать выпадающие списки соответствующими данными
        SimpleCursorAdapter categoriesAdapter, accountsAdapter;

        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Category.TABLE_NAME +
                " WHERE " + AccountancyContract.Category.IS_OUTGO + "=" + categoryType +
                " ORDER BY " + AccountancyContract.Category.IS_OUTGO + " DESC" + AccountancyContract.COMMA +
                AccountancyContract.Category.C_TITLE + " ASC", null);
        categoriesAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor,
                new String[] {AccountancyContract.Category.C_TITLE}, new int[] {android.R.id.text1}, 0);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(categoriesAdapter);

        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Account.TABLE_NAME, null);
        accountsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor,
                new String[] {AccountancyContract.Account.A_TITLE}, new int[] {android.R.id.text1}, 0);
        accountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountsSpinner.setAdapter(accountsAdapter);

        // Выбрать элементы, соответствующие платежу
        // Если Activity было вызвано для изменения платежа
        if (isEntityId()) {
            Utilities.selectSpinnerItem(categoriesAdapter, categoriesSpinner, categoryID);
            Utilities.selectSpinnerItem(accountsAdapter, accountsSpinner, accountID);
        }
    }
    private void initializeListeners() {
        // Инициализация слушателей
        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateTime.set(Calendar.MINUTE, minute);
                setInitialDateTime();
            }
        };

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTime.set(Calendar.YEAR, year);
                dateTime.set(Calendar.MONTH, monthOfYear);
                dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime();
            }
        };

        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNegativeAmount = !isNegativeAmount;

                if (isNegativeAmount)
                    signButton.setText("-");
                else
                    signButton.setText("+");
            }
        });
    }

    private void setInitialDateTime() {
        currentDateTextView.setText(String.format("%td.%tm.%tY", dateTime, dateTime, dateTime));
        currentTimeTextView.setText(String.format("%tH:%tM", dateTime, dateTime));
    }

    public void setDate(View view) {
        // Выбор даты
        new DatePickerDialog(this, onDateSetListener,
                dateTime.get(Calendar.YEAR),
                dateTime.get(Calendar.MONTH),
                dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }
    public void setTime(View view) {
        // Выбор времени
        new TimePickerDialog(this, onTimeSetListener,
                dateTime.get(Calendar.HOUR_OF_DAY),
                dateTime.get(Calendar.MINUTE), true).show();
    }

    public void saveTransactionContinue(View view) {
        // Сохранить платеж и создать новый
        if (executeSaving()) {
            Intent intent = new Intent(getApplicationContext(), SingleTransactionActivity.class);
            startActivity(intent);
        }
    }
    public void saveTransactionClose(View view) {
        // Сохранить платеж и закрыть Activity
        executeSaving();
    }

    private boolean executeSaving() {
        if (executeDataBaseSaving()) {
            finish();
            return true;
        }
        return false;
    }
    public boolean executeDataBaseSaving() {
        // Сохранение платежа

        // Валидация данных
        if (!validateData())
            return false;

        double amount = Double.parseDouble(transactionAmountEditText.getText().toString());
        amount *= isNegativeAmount ? -1 : 1;

        ContentValues contentValues = new ContentValues();
        contentValues.put(AccountancyContract.Transaction.DATE, Utilities.getSQLiteTimeString(dateTime));
        contentValues.put(AccountancyContract.Transaction.AMOUNT, amount);

        contentValues.put(AccountancyContract.Transaction.ACCOUNT_ID, accountsSpinner.getSelectedItemId());
        contentValues.put(AccountancyContract.Transaction.CATEGORY_ID, categoriesSpinner.getSelectedItemId());
        contentValues.put(AccountancyContract.Transaction.NOTE, noteEditTextEditText.getText().toString());

        // Если элемент изменялся - обновить его в базе данных
        // Иначе - создать новый
        if (isEntityId()) {
            db.update(AccountancyContract.Transaction.TABLE_NAME, contentValues,
                    AccountancyContract.Transaction._ID + "=?",
                    new String[] {String.valueOf(getEntityId())});
        }
        else {
            db.insert(AccountancyContract.Transaction.TABLE_NAME, null, contentValues);
        }

        return true;
    }
    private boolean validateData() {
        // Валидация данных
        if (transactionAmountEditText.getText().toString().isEmpty()) {
            Utilities.makeToast(this, "Поле сумма обязательно для заполнения");
            return false;
        }

        if (accountsSpinner.getSelectedItemPosition() < 0) {
            Utilities.makeToast(this, "Необходимо выбрать для какого счёта производится опреация");
            return false;
        }

        if (categoriesSpinner.getSelectedItemPosition() < 0) {
            Utilities.makeToast(this, "Необходимо выбрать категорию");
            return false;
        }


        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Category.TABLE_NAME +
        " WHERE " + AccountancyContract.Category._ID + "=?",
                new String[] {String.valueOf(categoriesSpinner.getSelectedItemId())});

        if (cursor.moveToFirst()) {
            int isOutgo = cursor.getInt(cursor.getColumnIndex(AccountancyContract.Category.IS_OUTGO));
            double sum = Double.parseDouble(transactionAmountEditText.getText().toString());

            if (sum == 0) {
                Utilities.makeToast(this, "Сумма не может равняться нулю");
                return false;
            }
            else if (isOutgo > 0 && !isNegativeAmount) {
                Utilities.makeToast(this, "Недопустима положительная сумма для категории \"Траты\"");
                return false;
            }
            else if (isOutgo == 0 && isNegativeAmount) {
                Utilities.makeToast(this, "Недопустима отрицательная сумма для категории \"Пополнения\"");
                return false;
            }
        }

        return true;
    }

    public void addNewCategory(View view) {
        // Добавить новую категорию
        Intent intent = new Intent(getApplicationContext(), SingleCategoryActivity.class);
        startActivity(intent);
    }
    public void addNewAccount(View view) {
        // Добавить новый счет
        Intent intent = new Intent(getApplicationContext(), SingleAccountActivity.class);
        startActivity(intent);
    }

    private int getCategoryType() {
        if (isEntityId()) {
            cursor = db.rawQuery("SELECT " + AccountancyContract.Category.IS_OUTGO + " FROM " + AccountancyContract.Transaction.TABLE_NAME +
                    " INNER JOIN " + AccountancyContract.Category.TABLE_NAME + " ON " +
                    AccountancyContract.Transaction.CATEGORY_ID + " = " + AccountancyContract.Category.TABLE_NAME + "." + AccountancyContract.Category._ID +
                    " WHERE " + AccountancyContract.Transaction.TABLE_NAME + "." + AccountancyContract.Transaction._ID + "=?",
                    new String[] {String.valueOf(getEntityId())});
            cursor.moveToFirst();

            return cursor.getInt(cursor.getColumnIndex(AccountancyContract.Category.IS_OUTGO));
        }
        else {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey("category")) {
                return extras.getInt("category");
            }
        }
        return -1;
    }
}