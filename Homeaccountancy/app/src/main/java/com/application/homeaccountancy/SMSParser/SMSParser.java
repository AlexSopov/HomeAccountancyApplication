package com.application.homeaccountancy.SMSParser;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.application.homeaccountancy.Data.AccountancyContract;
import com.application.homeaccountancy.R;
import com.application.homeaccountancy.Utilities;

import org.xmlpull.v1.XmlPullParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Класс парсинга смс
public class SMSParser {
    // База данных для добавления платежей
    private SQLiteDatabase db;

    // Список объектов, содержащие набор паттернов для анализа смс
    private List<ParsingEntity> parsingEntities;

    // Контекст работы парсера
    private Context context;

    public SMSParser(Context context, SQLiteDatabase db) {
        this.db = db;
        this.context = context;

        parsingEntities = new ArrayList<>();
        initializePatternEntities();
    }

    public boolean handleSMS(String message, long time) {
        // Если смс было ранне обработано - прекратить обработку
        if (smsHandled(time))
            return false;

        // Получение объекта, содержащего паттерны получение счета, категории и суммы,
        // который соответствуте данному смс
        ParsingEntity parsingEntity = getParsingEntity(message);
        if (parsingEntity == null)
            return false;

        // Получение данных платежа
        // Получение счета платежа
        long accountID = getSMSAccountID(message, parsingEntity);

        // Если не найден подходящий счет - прекратить обработку
        if (accountID <= 0)
            return false;

        // Получение категории платежа
        long categoryID = getSMSCategoryID(message, parsingEntity);

        // Получение суммы платежа
        double amount = getSMSAmount(message, parsingEntity);

        // Получение даты платежа
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String date = Utilities.getSQLiteTimeString(calendar);

        // Занести данные в базу данных
        ContentValues contentValuesTransaction = new ContentValues();
        contentValuesTransaction.put(AccountancyContract.Transaction.DATE, date);
        contentValuesTransaction.put(AccountancyContract.Transaction.AMOUNT, amount);
        contentValuesTransaction.put(AccountancyContract.Transaction.ACCOUNT_ID, accountID);
        contentValuesTransaction.put(AccountancyContract.Transaction.CATEGORY_ID, categoryID);
        contentValuesTransaction.put(AccountancyContract.Transaction.NOTE, "");
        db.insert(AccountancyContract.Transaction.TABLE_NAME, null, contentValuesTransaction);

        // Пометить сообщение как проанализированное
        ContentValues contentValuesSMS = new ContentValues();
        contentValuesSMS.put(AccountancyContract.SMS.COLUMN_NAME_SMS_DATE, time);
        db.insert(AccountancyContract.SMS.TABLE_NAME, null, contentValuesSMS);

        return true;
    }

    private void initializePatternEntities() {
        // Чтение паттернов из XML
        Resources resources = context.getResources();
        XmlResourceParser xmlResourceParser = resources.getXml(R.xml.patterns);

        try {
            int eventType = xmlResourceParser.getEventType();

            // Добавление паттернов в список
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xmlResourceParser.getName().equals("pattern")) {
                    String pattern = xmlResourceParser.getAttributeValue(0);
                    String accountPattern = xmlResourceParser.getAttributeValue(1);
                    String amountPattern = xmlResourceParser.getAttributeValue(2);
                    String negativeFormatter = xmlResourceParser.getAttributeValue(3);

                    parsingEntities.add(new ParsingEntity(pattern, accountPattern, amountPattern, negativeFormatter));
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

    private boolean smsHandled(long time) {
        // Проверка, обработано ли ранее данное смс
        Cursor cursorSMS = db.rawQuery("SELECT * FROM " + AccountancyContract.SMS.TABLE_NAME +
                " WHERE " + AccountancyContract.SMS.COLUMN_NAME_SMS_DATE + "=" + time, null);

        boolean result = cursorSMS.getCount() != 0;
        cursorSMS.close();

        return result;
    }
    private ParsingEntity getParsingEntity(String message) {
        // Получение ParsingEntity, паттерн которого соответствует сообщению
        Pattern pattern;
        Matcher matcher;

        for (ParsingEntity parsingEntity : parsingEntities) {
            pattern = Pattern.compile(parsingEntity.getPattern());
            matcher = pattern.matcher(message);

            if (matcher.matches())
                return parsingEntity;
        }

        return null;
    }

    private long getSMSAccountID(String message, ParsingEntity parsingEntity) {
        // Получение id счета из смс
        long id = -1;

        String smsAccount = getSMSAccount(message, parsingEntity);
        if (smsAccount.isEmpty())
            return id;

        Cursor accountCursor = db.rawQuery("SELECT " + AccountancyContract.Account._ID +
                " FROM " + AccountancyContract.Account.TABLE_NAME +
                " WHERE " + AccountancyContract.Account.A_TITLE + " LIKE '%" + smsAccount + "%'",
                null);

        if (accountCursor.moveToFirst())
            id = accountCursor.getLong(accountCursor.getColumnIndex(AccountancyContract.Account._ID));

        accountCursor.close();
        return id;
    }
    private long getSMSCategoryID(String message, ParsingEntity parsingEntity) {
        // Получение id категории из смс
        long id;
        double amount = getSMSAmount(message, parsingEntity);

        Cursor categoryCursor;
        String categoryQuery = "SELECT " + AccountancyContract.Category._ID + " FROM " +
                AccountancyContract.Category.TABLE_NAME + " WHERE " +
                AccountancyContract.Category.C_TITLE + "=%s";

        // Поиск "нулевых" категорий в зависимости от суммы платежа
        if (amount >= 0)
            categoryCursor = db.rawQuery(String.format(categoryQuery, "'Прочие пополнения'"), null);
        else
            categoryCursor = db.rawQuery(String.format(categoryQuery, "'Прочие траты'"), null);

        // Если категория найдена - вернуть её id
        // Иначе - создать новую категори
        if (categoryCursor.moveToFirst())
            id = categoryCursor.getLong(categoryCursor.getColumnIndex(AccountancyContract.Category._ID));
        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AccountancyContract.Category.ICON, R.drawable.others);

            if (amount >= 0) {
                contentValues.put(AccountancyContract.Category.C_TITLE, "'Прочие пополнения'");
                contentValues.put(AccountancyContract.Category.IS_OUTGO, 0);
            }
            else {
                contentValues.put(AccountancyContract.Category.C_TITLE, "'Прочие траты'");
                contentValues.put(AccountancyContract.Category.IS_OUTGO, 1);
            }
            id = db.insert(AccountancyContract.Category.TABLE_NAME, null, contentValues);
        }
        categoryCursor.close();

        return id;
    }

    private String getSMSAccount(String message, ParsingEntity parsingEntity) {
        // Получение счёта из смс
        Pattern pattern = Pattern.compile(parsingEntity.getAccountPattern());
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String account = matcher.group(0);
            if (account.length() >= 4)
                return account.substring(account.length() - 4, account.length());
            else
                return account;
        }
        return "";
    }
    private double getSMSAmount(String message, ParsingEntity parsingEntity) {
        // Получение суммы из смс
        Pattern pattern = Pattern.compile(parsingEntity.getAmountPattern());
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            double amount = Double.parseDouble(matcher.group(0).
                    replaceAll(" ", "").replace(',', '.'));
            amount *= getSMSAmountSign(message, parsingEntity);
            return amount;
        }
        return 0;
    }
    private int getSMSAmountSign(String message, ParsingEntity parsingEntity) {
        Pattern pattern = Pattern.compile(parsingEntity.getNegativeFormatter());
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return -1;
        }
        return 1;
    }
}