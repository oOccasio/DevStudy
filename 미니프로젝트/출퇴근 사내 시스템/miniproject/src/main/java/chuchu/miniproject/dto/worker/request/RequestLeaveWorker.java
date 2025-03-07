package chuchu.miniproject.dto.worker.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record RequestLeaveWorker(Long workerId,
                                 LocalDate workDate,
                                 LocalTime endTime) {
}
