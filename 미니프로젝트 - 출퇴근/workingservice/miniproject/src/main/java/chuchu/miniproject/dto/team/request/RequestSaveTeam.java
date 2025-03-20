package chuchu.miniproject.dto.team.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestSaveTeam {

    private final String name;
    private final String manager;

}
