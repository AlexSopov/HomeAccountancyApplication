package com.application.homeaccountancy.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.FilterSettings;
import com.application.homeaccountancy.R;

import java.util.Calendar;

public class SingleCategoryActivity extends AppCompatActivity {
    TextView titleTextView;
    RadioButton incomeCategoryRadioButton, outgoCategoryRadioButton;
    Spinner logoSpinner;

    SQLiteHandler handler;
    SQLiteDatabase db;
    Cursor cursor;

    long categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Категория");
        setContentView(R.layout.single_category_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InitializeViews();

        handler = new SQLiteHandler(getApplicationContext());
        db = handler.getReadableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            categoryId = extras.getLong("id");
        }

        if (categoryId > 0) {
            cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Category.TABLE_NAME +
                    " WHERE " + AccountancyContract.Category._ID + "=?", new String[] {String.valueOf(categoryId)});

            cursor.moveToFirst();
            titleTextView.setText(cursor.getString(cursor.getColumnIndex(AccountancyContract.Category.COLUMN_NAME_TITLE)));
            if (cursor.getInt(cursor.getColumnIndex(AccountancyContract.Category.COLUMN_NAME_IS_OUTGO)) != 0)
                outgoCategoryRadioButton.setChecked(true);
            else
                incomeCategoryRadioButton.setChecked(true);
        }
        initializeSpinners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (categoryId > 0) {
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(SingleCategoryActivity.this);
            dialog
                    .setTitle("Подтверждение действия")
                    .setMessage("Вы действительно хотите удалить категорию? Все записи данной " +
                            "категории будут стерты.")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.delete(AccountancyContract.Category.TABLE_NAME,
                                    AccountancyContract.Category._ID + "=?",
                                    new String[] {String.valueOf(categoryId)}
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

    private void InitializeViews() {
        titleTextView = (TextView) findViewById(R.id.category_title);
        incomeCategoryRadioButton = (RadioButton) findViewById(R.id.category_income_rb);
        outgoCategoryRadioButton = (RadioButton) findViewById(R.id.category_outgo_rb);
        logoSpinner = (Spinner) findViewById(R.id.logo_spinner);
    }
    private void initializeSpinners() {
        SimpleCursorAdapter logoAdapter;

        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Images.TABLE_NAME, null);
        logoAdapter = new SimpleCursorAdapter(this, R.layout.spinner_logo_item, cursor,
                new String[] {AccountancyContract.Images.COLUMN_NAME_IMAGE}, new int[] {R.id.logo}, 0);
        logoSpinner.setAdapter(logoAdapter);


        cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Category.TABLE_NAME +
                        " WHERE " + AccountancyContract.Category._ID + " =?",
                new String[] {String.valueOf(categoryId)});

        if (!cursor.moveToFirst())
            return;
        int iconValue = cursor.getInt(cursor.getColumnIndex(AccountancyContract.Category.COLUMN_NAME_ICON));


        cursor = db.rawQuery("SELECT " + AccountancyContract.Images._ID + " FROM " +
                AccountancyContract.Images.TABLE_NAME + " WHERE " +
                AccountancyContract.Images.COLUMN_NAME_IMAGE + "=" + String.valueOf(iconValue), null);
        if (!cursor.moveToFirst())
            return;

        int iconId = cursor.getInt(cursor.getColumnIndex(AccountancyContract.Category._ID));

        for (int i = 0; i < logoAdapter.getCount() && categoryId > 0; i++) {
            if (iconId == logoAdapter.getItemId(i)) {
                logoSpinner.setSelection(i);
                break;
            }
        }
    }

    public void saveCategory(View view) {
        String title = titleTextView.getText().toString();

        if (title.isEmpty()){
            Toast toast = Toast.makeText(this, "Название категории не может быть пустым",Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        try {
            cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Images.TABLE_NAME +
                    " WHERE " + AccountancyContract.Images._ID + "=?",
                    new String[] {String.valueOf(logoSpinner.getSelectedItemId())});
            cursor.moveToFirst();


            ContentValues contentValues = new ContentValues();
            contentValues.put(AccountancyContract.Category.COLUMN_NAME_TITLE, title);
            contentValues.put(AccountancyContract.Category.COLUMN_NAME_IS_OUTGO,
                    outgoCategoryRadioButton.isChecked() ? 1 : 0);
            contentValues.put(AccountancyContract.Category.COLUMN_NAME_ICON,
                    cursor.getInt(cursor.getColumnIndex(AccountancyContract.Images.COLUMN_NAME_IMAGE)));

            if (categoryId > 0) {
                db.update(AccountancyContract.Category.TABLE_NAME, contentValues,
                        AccountancyContract.Category._ID + "=?", new String[] {String.valueOf(categoryId)});
            }
            else {
                db.insertOrThrow(AccountancyContract.Category.TABLE_NAME, null, contentValues);
            }
            finish();
        }
        catch (SQLiteConstraintException exceprion) {
            Toast toast = Toast.makeText(this, "Категория с таким именем уже существует", Toast.LENGTH_LONG);
            toast.show();
        }
        catch (Exception ignored) {}
    }
}