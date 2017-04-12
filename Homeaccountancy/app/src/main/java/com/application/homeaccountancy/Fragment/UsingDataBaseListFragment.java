package com.application.homeaccountancy.Fragment;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.application.homeaccountancy.Data.SQLiteHandler;

// Класс, предоставляющий возможность ListFragment работать
// с базами данных
public class UsingDataBaseListFragment extends ListFragment {
    // Вспомогательный класс для работы с БД
    protected SQLiteHandler handler;

    // База данных
    protected SQLiteDatabase db;

    // Курсор для доступа к данным
    protected Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // инициализация переменных
        handler = new SQLiteHandler(getActivity().getApplicationContext());
        db = handler.getReadableDatabase();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        // При уничтожении Activity - освободить используемые ресурсы
        if (db != null)
            db.close();

        if (cursor != null)
            cursor.close();
    }
}
