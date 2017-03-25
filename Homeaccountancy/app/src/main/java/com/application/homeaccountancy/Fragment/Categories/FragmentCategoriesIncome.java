package com.application.homeaccountancy.Fragment.Categories;

import com.application.homeaccountancy.Data.AccountancyContract;

public class FragmentCategoriesIncome extends FragmentBaseCategories {
    @Override
    public void onResume() {
        super.onResume();

        String query  = "SELECT * FROM " + AccountancyContract.Category.TABLE_NAME +
                " WHERE " + AccountancyContract.Category.COLUMN_NAME_IS_OUTGO + " = 0";

        setAdapter(query);
    }
}