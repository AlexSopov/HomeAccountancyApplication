package com.application.homeaccountancy.Data;

import android.provider.BaseColumns;

public final class AccountancyContract {

    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String NOT_NULL = " NOT NULL";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String INTEGER_PRIMARY_KEY_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String COMMA_SEPARATOR = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public AccountancyContract() {}

    public static class Transaction implements BaseColumns {
        public static final String TABLE_NAME = "Transactions";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_ACCOUNT_ID = "account_id";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
        public static final String COLUMN_NAME_NOTE = "note";
    }

    public static class Category implements BaseColumns {
        public static final String TABLE_NAME = "Categories";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }

    public static class Account implements BaseColumns {
        public static final String TABLE_NAME = "Accounts";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
        public static final String COLUMN_NAME_ICON_ID = "icon";
        //public static final String COLUMN_NAME_TYPE = "type";
    }

    private static final String SQLITE_CREATE_TRANSACTIONS = CREATE_TABLE +
            Transaction.TABLE_NAME + " (" +
            Transaction._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA_SEPARATOR +
            Transaction.COLUMN_NAME_DATE + TEXT_TYPE + NOT_NULL + COMMA_SEPARATOR +
            Transaction.COLUMN_NAME_AMOUNT + REAL_TYPE + NOT_NULL + COMMA_SEPARATOR +
            Transaction.COLUMN_NAME_ACCOUNT_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEPARATOR +
            Transaction.COLUMN_NAME_CATEGORY_ID + INTEGER_TYPE + COMMA_SEPARATOR +
            Transaction.COLUMN_NAME_NOTE + TEXT_TYPE + COMMA_SEPARATOR +
            "FOREIGN KEY(" + Transaction.COLUMN_NAME_ACCOUNT_ID + ") " +
            "REFERENCES " + Account.TABLE_NAME + "(_id)" +
            "FOREIGN KEY(" + Transaction.COLUMN_NAME_CATEGORY_ID + ") " +
            "REFERENCES " + Category.TABLE_NAME + "(_id)" + ")";

    private static final String SQLITE_CREATE_CATEGORIES = CREATE_TABLE +
            Category.TABLE_NAME + " (" +
            Category._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA_SEPARATOR +
            Category.COLUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL +COMMA_SEPARATOR +
            Category.COLUMN_NAME_SUBTITLE + TEXT_TYPE + COMMA_SEPARATOR + ")";

    private static final String SQLITE_CREATE_ACCOUNTS = CREATE_TABLE +
            Account.TABLE_NAME + " (" +
            Account._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA_SEPARATOR +
            Account.COLUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEPARATOR +
            Account.COLUMN_NAME_SUBTITLE + TEXT_TYPE + COMMA_SEPARATOR + ")";


    private static final String SQLITE_DELETE_TRANSACTIONS = DROP_TABLE + Transaction.TABLE_NAME;
    private static final String SQLITE_DELETE_CATEGORIES = DROP_TABLE + Category.TABLE_NAME;
    private static final String SQLITE_DELETE_ACCOUNTS = DROP_TABLE + Account.TABLE_NAME;
}