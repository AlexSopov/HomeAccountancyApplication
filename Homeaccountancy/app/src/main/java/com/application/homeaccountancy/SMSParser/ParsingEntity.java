package com.application.homeaccountancy.SMSParser;

class ParsingEntity {
    private String pattern;
    private String accountPattern;
    private String amountPattern;
    private String negativeFormatter;

    ParsingEntity(String pattern, String accountPattern, String amountPattern, String negativeFormatter) {
        this.pattern = pattern;
        this.accountPattern = accountPattern;
        this.amountPattern = amountPattern;
        this.negativeFormatter = negativeFormatter;
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
    String getNegativeFormatter() {
        return negativeFormatter;
    }
}