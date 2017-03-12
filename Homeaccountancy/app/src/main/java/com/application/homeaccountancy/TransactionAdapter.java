package com.application.homeaccountancy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private LayoutInflater inflater;
    private int layout;
    private List<Transaction> transactions;

    public TransactionAdapter(Context context, int resource, List<Transaction> transactions) {
        super(context, resource, transactions);
        this.transactions = transactions;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(this.layout, parent, false);

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView sum = (TextView) view.findViewById(R.id.sum);
        TextView date = (TextView) view.findViewById(R.id.date);

        Transaction transaction = transactions.get(position);

        icon.setImageResource(transaction.FlagResource);
        description.setText(transaction.Description);
        sum.setText(String.valueOf(transaction.Sum));
        date.setText(transaction.Date);

        return view;
    }
}