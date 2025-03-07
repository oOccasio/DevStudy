package com.group.libraryapp.dto.assignment.request;

public class FruitSaveRequest {
    private String name;
    private long price;
    private boolean sell;

    public FruitSaveRequest(String name, long price, boolean sell) {
        this.name = name;
        this.price = price;
        this.sell = sell;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public boolean isSell() {
        return sell;
    }
}
