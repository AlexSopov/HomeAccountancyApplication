package com.application.homeaccountancy.Data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.application.homeaccountancy.R;

import org.xmlpull.v1.XmlPullParser;

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Accountancy11.db";
    private Context context;

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AccountancyContract.SQLITE_CREATE_CATEGORIES);
        db.execSQL(AccountancyContract.SQLITE_CREATE_ACCOUNTS);
        db.execSQL(AccountancyContract.SQLITE_CREATE_TRANSACTIONS);
        db.execSQL(AccountancyContract.SQLITE_CREATE_IMAGES);

        createCategories(db);
        createAccounts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(AccountancyContract.SQLITE_DELETE_CATEGORIES);
        db.execSQL(AccountancyContract.SQLITE_DELETE_ACCOUNTS);
        db.execSQL(AccountancyContract.SQLITE_DELETE_TRANSACTIONS);
        db.execSQL(AccountancyContract.SQLITE_DELETE_IMAGES);

        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void createCategories(SQLiteDatabase db) {
        ContentValues contentValuesCategories = new ContentValues();
        ContentValues contentValuesImages = new ContentValues();
        Resources resources = context.getResources();

        XmlResourceParser xmlResourceParser = resources.getXml(R.xml.categories_records);
        try {
            int eventType = xmlResourceParser.getEventType();

            int i = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xmlResourceParser.getName().equals("record")) {
                    String title = xmlResourceParser.getAttributeValue(0);
                    int isOutgo = xmlResourceParser.getAttributeIntValue(1, 1);
                    String icon = xmlResourceParser.getAttributeValue(2);

                    contentValuesCategories.put(AccountancyContract.Category.C_TITLE, title);
                    contentValuesCategories.put(AccountancyContract.Category.IS_OUTGO, isOutgo);
                    contentValuesCategories.put(AccountancyContract.Category.ICON,
                            resources.getIdentifier(icon, "drawable", context.getPackageName()));

                    contentValuesImages.put(AccountancyContract.Images.COLUMN_NAME_IMAGE,
                            resources.getIdentifier(icon, "drawable", context.getPackageName()));

                    db.insert(AccountancyContract.Category.TABLE_NAME, null, contentValuesCategories);

                    if (i++ != 0)
                        db.insert(AccountancyContract.Images.TABLE_NAME, null, contentValuesImages);
                }
                eventType = xmlResourceParser.next();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            xmlResourceParser.close();
        }
    }
    private void createAccounts(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(AccountancyContract.Account.A_TITLE, "Наличные");
        contentValues.put(AccountancyContract.Account.START_BALANCE, 0);
        db.insert(AccountancyContract.Account.TABLE_NAME, null, contentValues);

        contentValues.put(AccountancyContract.Account.A_TITLE, "Банковская карточка");
        contentValues.put(AccountancyContract.Account.START_BALANCE, 0);
        db.insert(AccountancyContract.Account.TABLE_NAME, null, contentValues);
    }
}