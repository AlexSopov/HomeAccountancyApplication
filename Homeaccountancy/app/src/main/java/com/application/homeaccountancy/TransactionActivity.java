package com.application.homeaccountancy;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TransactionActivity extends AppCompatActivity {
    Calendar date = Calendar.getInstance();
    TextView currentDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        currentDateTime=(TextView)findViewById(R.id.transaction_date);
        setInitialDateTime();
    }

    public void setDate(View view) {
        new DatePickerDialog(this, d,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    private void setInitialDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        currentDateTime.setText(dateFormat.format(date.getTime()));
    }
}
