package com.group.libraryapp.dto.calculator.request;

import java.util.ArrayList;

public class CalculatorSumRequest {
    private ArrayList<Integer> numbers;

    // ✅ 기본 생성자 추가

    public ArrayList<Integer> getNumbers() {
        return numbers;
    }
}
