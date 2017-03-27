package com.application.homeaccountancy.Fragment.Transactions;

public class FragmentAllTransactions extends FragmentBaseTransaction {
    @Override
    public void onResume() {
        super.onResume();

        String query = baseQuery;
        setAdapter(query);
    }
}
