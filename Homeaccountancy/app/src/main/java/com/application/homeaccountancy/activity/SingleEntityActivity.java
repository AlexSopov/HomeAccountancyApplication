package com.application.homeaccountancy.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.application.homeaccountancy.R;


public abstract class SingleEntityActivity extends UsingDataBaseActivity{
    private long entityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("id")) {
            entityId = extras.getLong("id");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (entityId > 0)
            getMenuInflater().inflate(R.menu.dalete_menu, menu);

        return true;
    }

    @Override
    public abstract boolean onOptionsItemSelected(MenuItem item);

    public long getEntityId() {
        return entityId;
    }

    public boolean isEntityId() {
        return entityId > 0;
    }
}
