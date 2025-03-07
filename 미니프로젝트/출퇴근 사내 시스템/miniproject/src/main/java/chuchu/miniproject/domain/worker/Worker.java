package chuchu.miniproject.domain.worker;

import chuchu.miniproject.domain.Role;
import chuchu.miniproject.domain.Team;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 25)
    private String name;


    @Column(length = 25)
    private String teamName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(nullable = false)
    private LocalDate workStartDate;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Column
    private Integer dayOff;

    @Builder
    public Worker(String name, String teamName, Role role, LocalDate birthday,
                  LocalDate workStartDate, Team team, Integer dayOff) {
        this.name = name;
        this.teamName = teamName;
        this.role = role;
        this.birthday = birthday;
        this.workStartDate = workStartDate;
        this.team = team;
        this.dayOff = dayOff;
    }


    public void useDayOff (){
        this.dayOff--;
    }




}
