package com.application.homeaccountancy;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.application.homeaccountancy.Data.AccountancyContract;

public class SingleAccountActivity extends AppCompatActivity {
    TextView titleTextView, startBalanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_account_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void saveAccount(View view) {
        String title = titleTextView.getText().toString();
        String startBalance = startBalanceTextView.getText().toString();

        if (title.isEmpty()){
            Toast toast = Toast.makeText(this, "Название категории не может быть пустым",Toast.LENGTH_LONG);
            toast.show();
            return;
        }


        //TODO CONTINUE
        /*try {
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
        }*/
    }
}
