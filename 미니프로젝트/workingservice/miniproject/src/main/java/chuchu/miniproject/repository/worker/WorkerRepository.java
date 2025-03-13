package chuchu.miniproject.repository.worker;

import chuchu.miniproject.domain.worker.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    Optional <Worker> findById(Long id);

}
