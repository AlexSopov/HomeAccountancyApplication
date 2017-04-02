package com.application.homeaccountancy.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.FilterSettings;
import com.application.homeaccountancy.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SingleTransactionActivity extends AppCompatActivity {
    TextView currentDate, currentTime;
    Spinner categoriesSpinner, accountsSpinner;
    EditText transactionSum, noteEditText;
    Button signButton;

    SQLiteHandler handler;
    SQLiteDatabase db;
    Cursor cursor;

    DatePickerDialog.OnDateSetListener onDateSetListener;
    TimePickerDialog.OnTimeSetListener onTimeSetListener;

    Calendar dateTime;
    boolean isNegativeSum = true;

    long transactionId;
    long categoryID, accountID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_transaction_activity);
        setTitle("Транзакция");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViews();
        initializeListeners();

        dateTime = Calendar.getInstance();
        handler = new SQLiteHandler(getApplicationContext());
        db = handler.getReadableDatabase();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            transactionId = extras.getLong("id");
        }

        if (transactionId > 0) {
            cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Transaction.TABLE_NAME +
                    " WHERE " + AccountancyContract.Transaction._ID + "=?", new String[] {String.valueOf(transactionId)});
            cursor.moveToFirst();

            String date = cursor.getString(cursor.getColumnIndex(AccountancyContract.Transaction.COLUMN_NAME_DATE));
            int amount = cursor.getInt(cursor.getColumnIndex(AccountancyContract.Transaction.COLUMN_NAME_AMOUNT));
            categoryID = cursor.getLong(cursor.getColumnIndex(AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID));
            accountID = cursor.getLong(cursor.getColumnIndex(AccountancyContract.Transaction.COLUMN_NAME_ACCOUNT_ID));
            String note = cursor.getString(cursor.getColumnIndex(AccountancyContract.Transaction.COLUMN_NAME_NOTE));

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

            Date time = null;
            try {
                time = formatter.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateTime.setTime(time);


            if (amount >= 0)
                signButton.performClick();

            transactionSum.setText(String.valueOf(Math.abs(amount)));
            noteEditText.setText(note);
        }
        initializeSpinners();
        setInitialDateTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (transactionId > 0) {
            getMenuInflater().inflate(R.menu.dalete_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(!item.isChecked())
            item.setChecked(true);

        if (id == R.id.delete) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SingleTransactionActivity.this);
            dialog
                    .setTitle("Подтверждение действия")
                    .setMessage("Вы действительно хотите удалить данную запись?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.delete(AccountancyContract.Transaction.TABLE_NAME,
                                    AccountancyContract.Transaction._ID + "=?",
                                    new String[] {String.valueOf(transactionId)}
                            );
                            finish();
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (db != null)
            db.close();

        if (cursor != null)
            cursor.close();
    }

    private void initializeViews() {
        categoriesSpinner = (Spinner)findViewById(R.id.transaction_category);
        accountsSpinner = (Spinner)findViewById(R.id.transaction_account);
        currentDate = (TextView)findViewById(R.id.transaction_date);
        currentTime = (TextView)findViewById(R.id.transaction_time);
        transactionSum = (EditText)findViewById(R.id.transaction_sum);
        noteEditText = (EditText)findViewById(R.id.transaction_note);
        signButton = (Button) findViewById(R.id.button_sign);
    }
    private void initializeSpinners() {
        SimpleCursorAdapter categoriesAdapter, accountsAdapter;

        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Category.TABLE_NAME +
                " ORDER BY " + AccountancyContract.Category.COLUMN_NAME_IS_OUTGO + " DESC", null);
        categoriesAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor,
                new String[] {AccountancyContract.Category.COLUMN_NAME_TITLE}, new int[] {android.R.id.text1}, 0);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(categoriesAdapter);

        for (int i = 0; i < categoriesAdapter.getCount() && categoryID > 0; i++) {
            if (categoriesAdapter.getItemId(i) == categoryID) {
                categoriesSpinner.setSelection(i);
                break;
            }
        }


        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Account.TABLE_NAME, null);
        accountsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor,
                new String[] {AccountancyContract.Account.COLUMN_NAME_TITLE}, new int[] {android.R.id.text1}, 0);
        accountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountsSpinner.setAdapter(accountsAdapter);

        for (int i = 0; i < accountsAdapter.getCount() && accountID > 0; i++) {
            if (accountsAdapter.getItemId(i) == accountID) {
                accountsSpinner.setSelection(i);
                break;
            }
        }
    }
    private void initializeListeners() {
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
                isNegativeSum = !isNegativeSum;

                if (isNegativeSum)
                    signButton.setText("-");
                else
                    signButton.setText("+");
            }
        });
    }
    private void setInitialDateTime() {
        currentDate.setText(String.format("%td.%tm.%tY", dateTime, dateTime, dateTime));
        currentTime.setText(String.format("%tH:%tM", dateTime, dateTime));
    }

    public void setDate(View view) {
        new DatePickerDialog(this, onDateSetListener,
                dateTime.get(Calendar.YEAR),
                dateTime.get(Calendar.MONTH),
                dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }
    public void setTime(View view) {
        new TimePickerDialog(this, onTimeSetListener,
                dateTime.get(Calendar.HOUR_OF_DAY),
                dateTime.get(Calendar.MINUTE), true).show();
    }

    public void saveTransaction(View view) {
        if (saveTransactionCloseActivity(view)) {
            Intent intent = new Intent(getApplicationContext(), SingleTransactionActivity.class);
            startActivity(intent);
        }
    }
    public boolean saveTransactionCloseActivity(View view) {
        if (executeSaving()) {
            finish();
            return true;
        }
        return false;
    }

    public boolean executeSaving() {
        if (!ValidateData())
            return false;

        double amount = Double.parseDouble(transactionSum.getText().toString());
        amount *= isNegativeSum ? -1 : 1;

        ContentValues contentValues = new ContentValues();
        contentValues.put(AccountancyContract.Transaction.COLUMN_NAME_DATE, getTimeString());
        contentValues.put(AccountancyContract.Transaction.COLUMN_NAME_AMOUNT, amount);

        contentValues.put(AccountancyContract.Transaction.COLUMN_NAME_ACCOUNT_ID, accountsSpinner.getSelectedItemId());
        contentValues.put(AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID, categoriesSpinner.getSelectedItemId());
        contentValues.put(AccountancyContract.Transaction.COLUMN_NAME_NOTE, noteEditText.getText().toString());


        if (transactionId > 0) {
            db.update(AccountancyContract.Transaction.TABLE_NAME, contentValues,
                    AccountancyContract.Transaction._ID + "=?",
                    new String[] {String.valueOf(transactionId)});
        }
        else {
            db.insert(AccountancyContract.Transaction.TABLE_NAME, null, contentValues);
        }

        return true;
    }
    private boolean ValidateData() {
        if (transactionSum.getText().toString().isEmpty()) {
            makeToast("Поле сумма обязательно для заполнения");
            return false;
        }

        if (accountsSpinner.getSelectedItemPosition() < 0) {
            makeToast("Необходимо выбрать для какого счёта производится опреация");
            return false;
        }

        if (categoriesSpinner.getSelectedItemPosition() < 0) {
            makeToast("Необходимо выбрать категорию");
            return false;
        }


        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Category.TABLE_NAME +
        " WHERE " + AccountancyContract.Category._ID + "=?",
                new String[] {String.valueOf(categoriesSpinner.getSelectedItemId())});

        if (cursor.moveToFirst()) {
            int isOutgo = cursor.getInt(cursor.getColumnIndex(AccountancyContract.Category.COLUMN_NAME_IS_OUTGO));
            double sum = Double.parseDouble(transactionSum.getText().toString());

            if (sum == 0) {
                makeToast("Сумма не может равняться нулю");
                return false;
            }
            else if (isOutgo > 0 && !isNegativeSum) {
                makeToast("Недопустима положительная сумма для категории \"Траты\"");
                return false;
            }
            else if (isOutgo == 0 && isNegativeSum) {
                makeToast("Недопустима отрицательная сумма для категории \"Пополнения\"");
                return false;
            }
        }

        return true;
    }
    private void makeToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    private String getTimeString() {
        return String.format("%tY-%tm-%td %tH:%tM:00",
                dateTime, dateTime, dateTime, dateTime, dateTime);
    }
}