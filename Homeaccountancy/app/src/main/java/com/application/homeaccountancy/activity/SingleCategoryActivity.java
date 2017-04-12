package com.application.homeaccountancy.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.R;
import com.application.homeaccountancy.Utilities;

// Класс, представляющий Ктегорию
public class SingleCategoryActivity extends SingleEntityActivity {
    // Переменные представления
    private TextView titleTextView;
    private RadioButton incomeCategoryRadioButton, outgoCategoryRadioButton;
    private Spinner logoSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализация контента
        setContentView(R.layout.single_category_activity);

        // Инициализация тулбара
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();

        // Если данное Activity было вызвано для изменения
        // созданной категории, то заполнить поля данными
        if (isEntityId()) {
            cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Category.TABLE_NAME +
                    " WHERE " + AccountancyContract.Category._ID + "=?", new String[] {String.valueOf(getEntityId())});

            cursor.moveToFirst();
            titleTextView.setText(cursor.getString(cursor.getColumnIndex(AccountancyContract.Category.C_TITLE)));

            if (cursor.getInt(cursor.getColumnIndex(AccountancyContract.Category.IS_OUTGO)) != 0)
                outgoCategoryRadioButton.setChecked(true);
            else
                incomeCategoryRadioButton.setChecked(true);
        }
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


        if (!isEntityId())
            return;

        cursor = db.rawQuery("SELECT " + AccountancyContract.Images.TABLE_NAME + "."+ AccountancyContract.Images._ID + ", " +
                AccountancyContract.Category.ICON + " as 'icon_value'" + " FROM " +
                AccountancyContract.Images.TABLE_NAME + " LEFT JOIN " +
                AccountancyContract.Category.TABLE_NAME  + " ON " +
                "icon_value=" +
                AccountancyContract.Images.COLUMN_NAME_IMAGE +
                " WHERE " + AccountancyContract.Category.TABLE_NAME + "." +
                AccountancyContract.Category._ID + "=" +
                String.valueOf(getEntityId()), null);

        cursor.moveToFirst();
        long iconId = cursor.getLong(
                cursor.getColumnIndex(AccountancyContract.Images.TABLE_NAME + "."+ AccountancyContract.Images._ID));

        Utilities.selectSpinnerItem(logoAdapter, logoSpinner, iconId);
    }

    public void saveCategory(View view) {
        String title = titleTextView.getText().toString();

        // Валидация данных
        if (title.isEmpty()){
            Toast toast = Toast.makeText(this, "Название категории не может быть пустым",Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        // Если элемент изменялся - обновить его в базе данных
        // Иначе - создать новый
        try {
            cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Images.TABLE_NAME +
                    " WHERE " + AccountancyContract.Images._ID + "=?",
                    new String[] {String.valueOf(logoSpinner.getSelectedItemId())});
            cursor.moveToFirst();

            ContentValues contentValues = new ContentValues();
            contentValues.put(AccountancyContract.Category.C_TITLE, title);
            contentValues.put(AccountancyContract.Category.IS_OUTGO,
                    outgoCategoryRadioButton.isChecked() ? 1 : 0);
            contentValues.put(AccountancyContract.Category.ICON,
                    cursor.getInt(cursor.getColumnIndex(AccountancyContract.Images.COLUMN_NAME_IMAGE)));

            if (isEntityId()) {
                db.update(AccountancyContract.Category.TABLE_NAME, contentValues,
                        AccountancyContract.Category._ID + "=?", new String[] {String.valueOf(getEntityId())});
            }
            else {
                db.insertOrThrow(AccountancyContract.Category.TABLE_NAME, null, contentValues);
            }
            finish();
        }
        catch (SQLiteConstraintException excerion) {
            Toast toast = Toast.makeText(this, "Категория с таким именем уже существует", Toast.LENGTH_LONG);
            toast.show();
        }
        catch (Exception ignored) {}
    }
}