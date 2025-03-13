package chuchu.miniproject.dto.worker.response;

public record ResponseOverTimeWork(
        Long id,

        String name,

        Integer overtimeMinutes
) {
}
