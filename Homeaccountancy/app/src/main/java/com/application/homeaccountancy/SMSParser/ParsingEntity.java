package com.application.homeaccountancy.SMSParser;

class ParsingEntity {
    private String pattern;
    private String accountPattern;
    private String amountPattern;
    private String datePattern;

    ParsingEntity(String pattern, String accountPattern, String amountPattern) {
        this.pattern = pattern;
        this.accountPattern = accountPattern;
        this.amountPattern = amountPattern;
        this.datePattern = datePattern;
    }

    String getPattern() {
        return pattern;
    }
    String getAccountPattern() {
        return accountPattern;
    }
    String getAmountPattern() {
        return amountPattern;
    }
}