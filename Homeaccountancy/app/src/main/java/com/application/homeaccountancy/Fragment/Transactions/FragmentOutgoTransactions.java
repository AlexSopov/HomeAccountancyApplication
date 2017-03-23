package com.application.homeaccountancy.Fragment.Transactions;

import com.application.homeaccountancy.Data.AccountancyContract;

public class FragmentOutgoTransactions extends FragmentBaseTransaction{
    @Override
    public void onResume() {
        super.onResume();

        String query = "SELECT " + AccountancyContract.Transaction.TABLE_NAME + "." +
                AccountancyContract.Transaction._ID + AccountancyContract.COMMA_SEPARATOR +
                AccountancyContract.Category.COLUMN_NAME_ICON + AccountancyContract.COMMA_SEPARATOR +
                AccountancyContract.Category.COLUMN_NAME_TITLE + AccountancyContract.COMMA_SEPARATOR +
                "strftime('%d.%m.%Y %H:%M'" + AccountancyContract.COMMA_SEPARATOR +
                AccountancyContract.Transaction.COLUMN_NAME_DATE  + ") as " +
                "'" + AccountancyContract.Transaction.COLUMN_NAME_DATE + "'" + AccountancyContract.COMMA_SEPARATOR +
                AccountancyContract.Transaction.COLUMN_NAME_AMOUNT + AccountancyContract.COMMA_SEPARATOR +
                AccountancyContract.Transaction.COLUMN_NAME_NOTE +
                " FROM " + AccountancyContract.Transaction.TABLE_NAME +
                " INNER JOIN " + AccountancyContract.Category.TABLE_NAME + " " +
                " ON " + AccountancyContract.Category.TABLE_NAME + "." + AccountancyContract.Category._ID + " = " +
                AccountancyContract.Transaction.TABLE_NAME + "." +
                AccountancyContract.Transaction.COLUMN_NAME_CATEGORY_ID + " WHERE is_outgo = 1";

        setAdapter(query);
    }
}
