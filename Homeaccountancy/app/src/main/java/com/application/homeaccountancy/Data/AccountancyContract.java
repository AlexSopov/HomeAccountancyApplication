package com.application.homeaccountancy.Data;

import android.provider.BaseColumns;

// Класс-контракт базы данных
// Описаны основные поля таблиц и запросы на их создание и удаление
public final class AccountancyContract {

    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String NOT_NULL = " NOT NULL";
    private static final String UNIQUE = " UNIQUE";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String FOREIGN_KEY= "FOREIGN KEY";
    private static final String REFERENCES= " REFERENCES ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String INTEGER_PRIMARY_KEY_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String ON_DELETE = " ON DELETE CASCADE ON UPDATE NO ACTION ";
    public static final String COMMA = ",";

    public AccountancyContract() {}

    public static class Transaction implements BaseColumns {
        public static final String TABLE_NAME = "Transactions";
        public static final String DATE = "date";
        public static final String AMOUNT = "amount";
        public static final String ACCOUNT_ID = "account_id";
        public static final String CATEGORY_ID = "category_id";
        public static final String NOTE = "note";
    }

    public static class Category implements BaseColumns {
        public static final String TABLE_NAME = "Categories";
        public static final String C_TITLE = "c_title";
        public static final String IS_OUTGO = "is_outgo";
        public static final String ICON = "icon";
    }

    public static class Account implements BaseColumns {
        public static final String TABLE_NAME = "Accounts";
        public static final String START_BALANCE = "start_balance";
        public static final String A_TITLE = "a_title";
    }

    public static class Images implements BaseColumns {
        public static final String TABLE_NAME = "Images";
        public static final String COLUMN_NAME_IMAGE = "image";
    }

    public static class SMS implements BaseColumns {
        public static final String TABLE_NAME = "SMS";
        public static final String COLUMN_NAME_SMS_DATE = "sms_date";
    }

    static final String SQLITE_CREATE_TRANSACTIONS = CREATE_TABLE +
            Transaction.TABLE_NAME + " (" +
            Transaction._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA +
            Transaction.DATE + INTEGER_TYPE + NOT_NULL + COMMA +
            Transaction.AMOUNT + REAL_TYPE + NOT_NULL + COMMA +
            Transaction.ACCOUNT_ID + INTEGER_TYPE + COMMA +
            Transaction.CATEGORY_ID + INTEGER_TYPE + COMMA +
            Transaction.NOTE + TEXT_TYPE + COMMA +
            FOREIGN_KEY + "(" + Transaction.ACCOUNT_ID + ")" +
            REFERENCES + Account.TABLE_NAME + "(_id)" + ON_DELETE + COMMA +
            FOREIGN_KEY + "(" + Transaction.CATEGORY_ID + ")" +
            REFERENCES + Category.TABLE_NAME + "(_id)" + ON_DELETE + ")";

    static final String SQLITE_CREATE_CATEGORIES = CREATE_TABLE +
            Category.TABLE_NAME + " (" +
            Category._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA +
            Category.C_TITLE + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA +
            Category.IS_OUTGO + INTEGER_TYPE + NOT_NULL + COMMA +
            Category.ICON + INTEGER_TYPE + ")";

    static final String SQLITE_CREATE_ACCOUNTS = CREATE_TABLE +
            Account.TABLE_NAME + " (" +
            Account._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA +
            Account.A_TITLE + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA +
            Account.START_BALANCE + REAL_TYPE + NOT_NULL + ")";

    static final String SQLITE_CREATE_IMAGES = CREATE_TABLE +
            Images.TABLE_NAME + " (" +
            Images._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA +
            Images.COLUMN_NAME_IMAGE + INTEGER_TYPE + NOT_NULL + ")";

    static final String SQLITE_CREATE_SMS = CREATE_TABLE +
            SMS.TABLE_NAME + " (" +
            SMS._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA +
            SMS.COLUMN_NAME_SMS_DATE + INTEGER_TYPE + NOT_NULL + ")";


    static final String SQLITE_DELETE_TRANSACTIONS = DROP_TABLE + Transaction.TABLE_NAME;
    static final String SQLITE_DELETE_CATEGORIES = DROP_TABLE + Category.TABLE_NAME;
    static final String SQLITE_DELETE_ACCOUNTS = DROP_TABLE + Account.TABLE_NAME;
    static final String SQLITE_DELETE_IMAGES = DROP_TABLE + Images.TABLE_NAME;
    static final String SQLITE_DELETE_SMS = DROP_TABLE + SMS.TABLE_NAME;
}