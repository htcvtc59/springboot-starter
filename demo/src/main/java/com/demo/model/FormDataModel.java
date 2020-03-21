package com.demo.model;

public class FormDataModel {
    private String amount;
    private String account_form;
    private String account_to;
    private String date;

    public FormDataModel(String amount, String account_form, String account_to, String date) {
        this.amount = amount;
        this.account_form = account_form;
        this.account_to = account_to;
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAccount_form() {
        return account_form;
    }

    public void setAccount_form(String account_form) {
        this.account_form = account_form;
    }

    public String getAccount_to() {
        return account_to;
    }

    public void setAccount_to(String account_to) {
        this.account_to = account_to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
