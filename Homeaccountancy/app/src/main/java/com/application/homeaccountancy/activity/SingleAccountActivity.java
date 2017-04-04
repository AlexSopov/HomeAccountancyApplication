package com.application.homeaccountancy.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.R;
import com.application.homeaccountancy.Utilities;

public class SingleAccountActivity extends SingleEntityActivity {
    TextView titleTextView, startBalanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_account_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();

        if (isEntityId()) {
            cursor = db.rawQuery("SELECT * FROM " + AccountancyContract.Account.TABLE_NAME +
                    " WHERE " + AccountancyContract.Account._ID + "=?", new String[] {String.valueOf(getEntityId())});

            cursor.moveToFirst();
            titleTextView.setText(cursor.getString(cursor.getColumnIndex(AccountancyContract.Account.A_TITLE)));
            startBalanceTextView.setText(cursor.getString(cursor.getColumnIndex(AccountancyContract.Account.START_BALANCE)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(!item.isChecked())
            item.setChecked(true);

        if (id == R.id.delete) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SingleAccountActivity.this);
            dialog
                    .setTitle("Подтверждение действия")
                    .setMessage("Вы действительно хотите удалить счёт? Все записи данного " +
                            "счёта будут стерты.")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.delete(AccountancyContract.Account.TABLE_NAME,
                                    AccountancyContract.Account._ID + "=?",
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
        titleTextView = (TextView) findViewById(R.id.account_title);
        startBalanceTextView = (TextView) findViewById(R.id.start_balance);
    }

    public void saveAccount(View view) {
        String title = titleTextView.getText().toString();
        String startBalanceText = startBalanceTextView.getText().toString();


        if (title.isEmpty()){
            Utilities.makeToast(this, "Название счёта не может быть пустым");
            return;
        }

        if (startBalanceText.isEmpty()){
            Utilities.makeToast(this, "Необхоимо ввести начальный баланс счёта");
            return;
        }


        double startBalance = Double.parseDouble(startBalanceText);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AccountancyContract.Account.A_TITLE, title);
            contentValues.put(AccountancyContract.Account.START_BALANCE, startBalance);

            if (isEntityId()) {
                db.update(AccountancyContract.Account.TABLE_NAME, contentValues,
                        AccountancyContract.Account._ID + "=?", new String[] {String.valueOf(getEntityId())});
                finish();
            }
            else {
                db.insertOrThrow(AccountancyContract.Account.TABLE_NAME, null, contentValues);
                finish();
            }
        }
        catch (SQLiteConstraintException exception) {
            Utilities.makeToast(this, "Счёт с таким именем уже существует");
        }
    }
}