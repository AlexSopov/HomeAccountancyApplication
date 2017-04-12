package com.application.homeaccountancy.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.application.homeaccountancy.R;

// Класс, описывающий сущность (например: счет, категория)
public abstract class SingleEntityActivity extends UsingDataBaseActivity{
    // Id элемента в базе данных
    private long entityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Попытка считать элемент Id
        // если сущность была ранее создана
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("id")) {
            entityId = extras.getLong("id");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Создать меню удаления, если сущность была ранее создана
        if (isEntityId())
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
