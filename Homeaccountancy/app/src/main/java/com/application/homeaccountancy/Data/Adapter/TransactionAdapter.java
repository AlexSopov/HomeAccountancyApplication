package com.application.homeaccountancy.Data.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.homeaccountancy.Data.Entity.Transaction;
import com.application.homeaccountancy.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private LayoutInflater inflater;
    private List<Transaction> transactions;
    private int layout;

    TransactionAdapter(Context context, int resource, List<Transaction> transactions) {
        super(context, resource, transactions);
        this.transactions = transactions;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Transaction transaction = transactions.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        viewHolder.iconView.setImageResource(transaction.getFlagResource());
        viewHolder.descriptionView.setText(transaction.getDescriptionShort());
        viewHolder.sumView.setText(String.valueOf(transaction.getSum()));
        viewHolder.dateView.setText(dateFormat.format(transaction.getDate().getTime()));

        return convertView;
    }

    private class ViewHolder {
        final ImageView iconView;
        final TextView descriptionView, sumView, dateView;

        ViewHolder(View view){
            iconView = (ImageView)view.findViewById(R.id.icon);
            descriptionView = (TextView) view.findViewById(R.id.description);
            sumView = (TextView) view.findViewById(R.id.sum);
            dateView = (TextView) view.findViewById(R.id.date);
        }
    }
}