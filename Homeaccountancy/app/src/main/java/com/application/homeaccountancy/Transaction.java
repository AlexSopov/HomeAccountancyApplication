package com.application.homeaccountancy;


public class Transaction {

    int Sum;
    String Description;
    String Date;
    int FlagResource;

    public Transaction(int sum, String description, String date, int flag){

        this.Sum = sum;
        this.Description = description;
        this.FlagResource = flag;
        this.Date = date;
    }
}