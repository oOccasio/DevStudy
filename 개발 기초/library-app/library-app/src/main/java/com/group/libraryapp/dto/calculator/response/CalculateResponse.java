package com.group.libraryapp.dto.calculator.response;

public class CalculateResponse {
    private Integer add;
    private Integer minus;
    private Integer multiply;

    public CalculateResponse(Integer add, Integer minus, Integer multiply) {
        this.add = add;
        this.minus = minus;
        this.multiply = multiply;
    }

    public Integer getAdd() {
        return add;
    }

    public Integer getMinus() {
        return minus;
    }

    public Integer getMultiply() {
        return multiply;
    }
}
