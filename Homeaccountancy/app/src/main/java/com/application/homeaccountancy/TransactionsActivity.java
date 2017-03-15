package com.application.homeaccountancy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.application.homeaccountancy.Data.Adapter.TransactionAdapter;
import com.application.homeaccountancy.Data.Entity.Transaction;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class TransactionsActivity extends IncludeMenuActivity {

    private List<Transaction> transactions = new ArrayList<>();
    ListView transList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.transactions_activity);


        TabHost tabHost = (TabHost) findViewById(R.id.tabs_transactions);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tabs_transactions_tab1");
        tabSpec.setIndicator("Все");
        tabSpec.setContent(R.id.tabs_transactions_tab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tabs_transactions_tab2");
        tabSpec.setIndicator("Пополнения");
        tabSpec.setContent(R.id.tabs_transactions_tab2);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tabs_transactions_tab3");
        tabSpec.setIndicator("Затраты");
        tabSpec.setContent(R.id.tabs_transactions_tab3);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        // начальная инициализация списка
        setInitialData();
        // получаем элемент ListView
        transList = (ListView) findViewById(R.id.all_transactions_list);
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
        Intent intent = new Intent(TransactionsActivity.this, SingleTransactionActivity.class);
        startActivity(intent);
    }
}
