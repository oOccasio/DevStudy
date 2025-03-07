package chuchu.miniproject.dto.worker.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record RequestGoWorker(Long workerId,
                              LocalDate workDate,
                              LocalTime goTime) {

}
