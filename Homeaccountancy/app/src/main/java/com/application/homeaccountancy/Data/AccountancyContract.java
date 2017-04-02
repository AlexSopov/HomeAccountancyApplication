package com.application.homeaccountancy.Data;

import android.provider.BaseColumns;

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
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_ACCOUNT_ID = "account_id";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
        public static final String COLUMN_NAME_NOTE = "note";
    }

    public static class Category implements BaseColumns {
        public static final String TABLE_NAME = "Categories";
        public static final String COLUMN_NAME_TITLE = "c_title";
        public static final String COLUMN_NAME_IS_OUTGO = "is_outgo";
        public static final String COLUMN_NAME_ICON = "icon";
    }

    public static class Account implements BaseColumns {
        public static final String TABLE_NAME = "Accounts";
        public static final String COLUMN_NAME_START_BALANCE = "start_balance";
        public static final String COLUMN_NAME_TITLE = "a_title";
    }

    public static class Images implements BaseColumns {
        public static final String TABLE_NAME = "Images";
        public static final String COLUMN_NAME_IMAGE = "image";
    }

    public static final String SQLITE_CREATE_TRANSACTIONS = CREATE_TABLE +
            Transaction.TABLE_NAME + " (" +
            Transaction._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA +
            Transaction.COLUMN_NAME_DATE + INTEGER_TYPE + NOT_NULL + COMMA +
            Transaction.COLUMN_NAME_AMOUNT + REAL_TYPE + NOT_NULL + COMMA +
            Transaction.COLUMN_NAME_ACCOUNT_ID + INTEGER_TYPE + COMMA +
            Transaction.COLUMN_NAME_CATEGORY_ID + INTEGER_TYPE + COMMA +
            Transaction.COLUMN_NAME_NOTE + TEXT_TYPE + COMMA +
            FOREIGN_KEY + "(" + Transaction.COLUMN_NAME_ACCOUNT_ID + ")" +
            REFERENCES + Account.TABLE_NAME + "(_id)" + ON_DELETE + COMMA +
            FOREIGN_KEY + "(" + Transaction.COLUMN_NAME_CATEGORY_ID + ")" +
            REFERENCES + Category.TABLE_NAME + "(_id)" + ON_DELETE + ")";

    public static final String SQLITE_CREATE_CATEGORIES = CREATE_TABLE +
            Category.TABLE_NAME + " (" +
            Category._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA +
            Category.COLUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA +
            Category.COLUMN_NAME_IS_OUTGO + INTEGER_TYPE + NOT_NULL + COMMA +
            Category.COLUMN_NAME_ICON + INTEGER_TYPE + ")";

    public static final String SQLITE_CREATE_ACCOUNTS = CREATE_TABLE +
            Account.TABLE_NAME + " (" +
            Account._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA +
            Account.COLUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA +
            Account.COLUMN_NAME_START_BALANCE + REAL_TYPE + NOT_NULL + ")";

    public static final String SQLITE_CREATE_IMAGES = CREATE_TABLE +
            Images.TABLE_NAME + " (" +
            Images._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA +
            Images.COLUMN_NAME_IMAGE + INTEGER_TYPE + NOT_NULL + ")";


    public static final String SQLITE_DELETE_TRANSACTIONS = DROP_TABLE + Transaction.TABLE_NAME;
    public static final String SQLITE_DELETE_CATEGORIES = DROP_TABLE + Category.TABLE_NAME;
    public static final String SQLITE_DELETE_ACCOUNTS = DROP_TABLE + Account.TABLE_NAME;
    public static final String SQLITE_DELETE_IMAGES = DROP_TABLE + Images.TABLE_NAME;
}