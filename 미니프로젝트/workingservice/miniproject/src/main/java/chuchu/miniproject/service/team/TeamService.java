package chuchu.miniproject.service.team;

import chuchu.miniproject.domain.Team;
import chuchu.miniproject.dto.team.request.RequestSaveTeam;
import chuchu.miniproject.dto.team.response.ResponseGetTeam;
import chuchu.miniproject.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;


    @Transactional
    public void saveTeam (RequestSaveTeam requestSaveTeam) {
        String name = requestSaveTeam.getName();
        String manager = requestSaveTeam.getManager();

        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format("유효하지 않은 이름입니다: %s", name));
        }
        if(name.length() > 25){
            throw new IllegalArgumentException("팀 이름은 25자를 초과할 수 없습니다.");
        }

        teamRepository.findByName(name)
                .ifPresent(team -> {
                    throw new IllegalArgumentException("이미 존재하는 팀이름입니다.");
                });

        if(manager != null && manager.length() > 25) {
            throw new IllegalArgumentException("매니저 이름은 25자를 초과할 수 없습니다.");
        }


        Team team = Team.builder()
                .name(name)
                .manager(manager)
                .memberCount(new ArrayList<>())
                .build();

        teamRepository.save(team);

    }

    @Transactional(readOnly = true)
    public List<ResponseGetTeam> getTeams() {


        return teamRepository.findAll().stream()
            .map(team -> ResponseGetTeam.builder()
                    .name(team.getName())
                    .manager(team.getManager())
                    .memberCount(team.getMemberCount())
                    .build())
                .collect(Collectors.toList());
    }

}
