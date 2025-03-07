package com.group.libraryapp.dto.calculator.response;

import java.time.DayOfWeek;

public class DateResponse {

    private DayOfWeek dayOfTheWeek;

    public DateResponse(DayOfWeek dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public DayOfWeek getDayOfTheWeek() {
        return dayOfTheWeek;
    }
}
