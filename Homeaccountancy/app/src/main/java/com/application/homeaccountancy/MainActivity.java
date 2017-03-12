package com.application.homeaccountancy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
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
                Toast.makeText(getApplicationContext(), "Был выбран пункт " + selectedTransaction.Sum,
                        Toast.LENGTH_SHORT).show();
            }
        };
        transList.setOnItemClickListener(itemListener);
    }
    private void setInitialData(){

        transactions.add(new Transaction(1000, "1dfsafadsnfkldashflkfdafdsafdsafdsafdsafd safdsafdsafdasfad sfadsfasdfadsfasdfadsfas " +
                "fdsafdasfadsfadsfdsafass",
                "2017-01-01", R.drawable.argentina));
        transactions.add(new Transaction(1100, "gfsdbvxcbtssgfsg sfgsfdgsd", "2017-01-02", R.drawable.uruguai));
        transactions.add(new Transaction(1200, "gfsdgsfdgsfdgfdgfsdg", "2017-01-03", R.drawable.columbia));
        transactions.add(new Transaction(1300, "gfdsgsdfg fgsdgfds fgsdg sfd", "2017-01-04", R.drawable.brazilia));
        transactions.add(new Transaction(1400, "5325gfds gfdsg fdsg", "2017-01-05", R.drawable.chile));
    }
}
