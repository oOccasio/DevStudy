package chuchu.miniproject.dto.worker.response;

import java.time.LocalDate;

public record ResponseListGetWorkList(
        LocalDate date,

        Long workingMinute

) {
}
