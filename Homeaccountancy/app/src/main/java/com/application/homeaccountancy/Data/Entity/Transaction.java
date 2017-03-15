package com.application.homeaccountancy.Data.Entity;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class Transaction {
    private int sum;
    private long date;
    private String description;
    private int flagResource;


    public Transaction(int sum, long date, String description, int flagResource){
        this.sum = sum;
        this.date = date;
        this.description = description;
        this.flagResource = flagResource;
    }

    public int getSum() {
        return sum;
    }
    public void setSum(int sum) {
        this.sum = sum;
    }

    public long getDate() {
        return date;
    }
    public void setDate(int date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getFlagResource() {
        return flagResource;
    }
    public void setFlagResource(int flagResource) {
        this.flagResource = flagResource;
    }

    public String getDescriptionShort() {
        if (description.length() > 50)
            return description.substring(0, 50) + "...";

        return description;
    }
}