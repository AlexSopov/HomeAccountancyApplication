package com.application.homeaccountancy.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.application.homeaccountancy.Data.SQLiteHandler;

// Класс, предоставляющий возможность Activity работать
// с базами данных
public abstract class UsingDataBaseActivity extends AppCompatActivity {
    // Вспомогательный класс для работы с БД
    protected SQLiteHandler handler;

    // База данных
    protected SQLiteDatabase db;

    // Курсор для доступа к данным
    protected Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // инициализация переменных
        handler = new SQLiteHandler(getApplicationContext());
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
