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
    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "Accountancy8.db";
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

        CreateCategories(db);
        CreateAccounts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(AccountancyContract.SQLITE_DELETE_CATEGORIES);
        db.execSQL(AccountancyContract.SQLITE_DELETE_ACCOUNTS);
        db.execSQL(AccountancyContract.SQLITE_DELETE_TRANSACTIONS);
        db.execSQL(AccountancyContract.SQLITE_DELETE_IMAGES);

        onCreate(db);
    }

    private void CreateCategories(SQLiteDatabase db) {
        //TODO SQLiteConstraintException
        ContentValues contentValuesCategories = new ContentValues();
        ContentValues contentValuesImages = new ContentValues();
        Resources resources = context.getResources();

        XmlResourceParser xmlResourceParser = resources.getXml(R.xml.categories_records);
        try {
            int eventType = xmlResourceParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xmlResourceParser.getName().equals("record")) {
                    String title = xmlResourceParser.getAttributeValue(0);
                    int isOutgo = xmlResourceParser.getAttributeIntValue(1, 1);
                    String icon = xmlResourceParser.getAttributeValue(2);

                    contentValuesCategories.put(AccountancyContract.Category.COLUMN_NAME_TITLE, title);
                    contentValuesCategories.put(AccountancyContract.Category.COLUMN_NAME_IS_OUTGO, isOutgo);
                    contentValuesCategories.put(AccountancyContract.Category.COLUMN_NAME_ICON,
                            resources.getIdentifier(icon, "drawable", context.getPackageName()));

                    contentValuesImages.put(AccountancyContract.Images.COLUMN_NAME_IMAGE,
                            resources.getIdentifier(icon, "drawable", context.getPackageName()));

                    db.insert(AccountancyContract.Category.TABLE_NAME, null, contentValuesCategories);
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

    private void CreateAccounts(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(AccountancyContract.Account.COLUMN_NAME_TITLE, "Наличные");
        contentValues.put(AccountancyContract.Account.COLUMN_NAME_START_BALANCE, 500);
        db.insert(AccountancyContract.Account.TABLE_NAME, null, contentValues);

        contentValues.put(AccountancyContract.Account.COLUMN_NAME_TITLE, "Карточка xxxx-xxxx-xxxx-xxxx");
        contentValues.put(AccountancyContract.Account.COLUMN_NAME_START_BALANCE, 1000);
        db.insert(AccountancyContract.Account.TABLE_NAME, null, contentValues);
    }
}
