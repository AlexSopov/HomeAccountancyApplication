package com.application.homeaccountancy.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.Data.SQLiteHandler;
import com.application.homeaccountancy.R;

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
        //logoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        logoSpinner.setAdapter(logoAdapter);
    }

    public void saveCategory(View view) {
        String title = titleTextView.getText().toString();

        if (title.isEmpty()){
            Toast toast = Toast.makeText(this, "Название категории не может быть пустым",Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AccountancyContract.Category.COLUMN_NAME_TITLE, title);
            contentValues.put(AccountancyContract.Category.COLUMN_NAME_IS_OUTGO,
                    outgoCategoryRadioButton.isChecked() ? 1 : 0);

            if (categoryId > 0) {
                db.update(AccountancyContract.Category.TABLE_NAME, contentValues,
                        AccountancyContract.Category._ID + "=?", new String[] {String.valueOf(categoryId)});
            }
            else {
                contentValues.put(AccountancyContract.Category.COLUMN_NAME_ICON, R.drawable.others);
                db.insertOrThrow(AccountancyContract.Category.TABLE_NAME, null, contentValues);
            }
            finish();
        }
        catch (SQLiteConstraintException exceprion) {
            Toast toast = Toast.makeText(this, "Категория с таким именем уже существует", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}