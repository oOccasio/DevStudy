package chuchu.miniproject.dto.worker.request;

import chuchu.miniproject.domain.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;


@Getter
@RequiredArgsConstructor
public class RequestSaveWorker {


    private final String name;
    private final String teamName;
    private final Role role;
    private final LocalDate birthday;
    private final LocalDate workStartDate;

}
