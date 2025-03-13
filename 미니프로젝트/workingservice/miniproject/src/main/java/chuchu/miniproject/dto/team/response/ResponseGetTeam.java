package chuchu.miniproject.dto.team.response;

import chuchu.miniproject.domain.Team;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
public class ResponseGetTeam {
    private String name;
    private String manager;
    private int memberCount;

}
