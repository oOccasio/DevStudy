package chuchu.miniproject.domain.worker;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Entity
@Getter
public class WorkList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private LocalDate workDate;

    @Column
    private LocalTime goTime;

    @Column
    private LocalTime endTime;

    @Column(nullable = true)
    private Long workingMinutes;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;


    @Builder
    public WorkList(LocalDate workDate, Long workingMinutes, Worker worker, LocalTime goTime, LocalTime endTime) {
        this.workDate = workDate;
        this.workingMinutes = workingMinutes;
        this.worker = worker;
        this.goTime = goTime;
        this.endTime = endTime;
    }




    public void leaveWork(LocalTime endTime){
        this.endTime = endTime;
    }

    public void worKingMinutes(WorkList workList){
        this.workingMinutes = Duration.between(workList.getGoTime(), workList.getEndTime()).toMinutes();
    }

}
