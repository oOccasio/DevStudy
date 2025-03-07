package com.group.libraryapp.dto.assignment.response;

public class FruitSellMoney {
    private long salesAmount;
    private long notSalesAmount;

    public FruitSellMoney(long salesAmount, long notSalesAmount) {
        this.salesAmount = salesAmount;
        this.notSalesAmount = notSalesAmount;
    }

    public long getSalesAmount() {
        return salesAmount;
    }

    public long getNotSalesAmount() {
        return notSalesAmount;
    }
}
