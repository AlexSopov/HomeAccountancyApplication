package com.application.homeaccountancy.Fragment.Transactions;

import com.application.homeaccountancy.Data.AccountancyContract;

public class FragmentOutgoTransactions extends FragmentBaseTransaction{
    @Override
    public void onResume() {
        super.onResume();

        String query = baseQuery + " WHERE is_outgo = 1";
        setAdapter(query);
    }
}
