package chuchu.miniproject.dto.worker.request;

import java.time.LocalDate;

public record RequestUsingDayOff(

        Long workerId,

        LocalDate dayOffDay
) {
}
