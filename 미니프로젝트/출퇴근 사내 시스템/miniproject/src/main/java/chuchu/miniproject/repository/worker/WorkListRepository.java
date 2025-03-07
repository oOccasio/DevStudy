package chuchu.miniproject.repository.worker;

import chuchu.miniproject.domain.worker.WorkList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WorkListRepository extends JpaRepository<WorkList, Long> {

    Optional<WorkList> findByWorkerIdAndWorkDate(Long workerId, LocalDate workDate);

}
