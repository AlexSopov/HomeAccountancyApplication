package com.application.homeaccountancy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<Transaction> transactions = new ArrayList<>();

    ListView transList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // начальная инициализация списка
        setInitialData();
        // получаем элемент ListView
        transList = (ListView) findViewById(R.id.transactionsList);
        // создаем адаптер
        TransactionAdapter transactionAdapter = new TransactionAdapter(this, R.layout.transaction_list_item, transactions);
        // устанавливаем адаптер
        transList.setAdapter(transactionAdapter);
        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // получаем выбранный пункт
                Transaction selectedTransaction = (Transaction)parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Был выбран пункт " + selectedTransaction.getSum(),
                        Toast.LENGTH_SHORT).show();
            }
        };
        transList.setOnItemClickListener(itemListener);
    }
    private void setInitialData(){
        for (int i = 0; i < 5; i++) {
            transactions.add(new Transaction(i * 100, new GregorianCalendar(2017, i + 5, i + 7),
                    "Description dfsafsd fdsa afsdaf fdsaf fdas " + i, R.drawable.argentina));
            transactions.add(new Transaction(i * 100, new GregorianCalendar(2017, i + 5, i + 7),
                    "Description dfsafsd afsdaf fdsaf fdas " + i, R.drawable.brazilia));
            transactions.add(new Transaction(i * 100, new GregorianCalendar(2017, i + 5, i + 7),
                    "Description dfsafsd afsdaf fdsaf fdas " + i, R.drawable.columbia));
            transactions.add(new Transaction(i * 100, new GregorianCalendar(2017, i + 5, i + 7),
                    "Descrip fdsa tion dfsafsdafsdaf fdsaf fdas " + i, R.drawable.chile));
            transactions.add(new Transaction(i * 100, new GregorianCalendar(2017, i + 5, i + 7),
                    "Description dfsafsdafsdaf fdsaf fdas " + i, R.drawable.uruguai));
        }
    }

    public void addNewTransaction(View view) {
        Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
        startActivity(intent);
    }
}
