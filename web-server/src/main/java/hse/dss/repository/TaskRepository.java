package hse.dss.repository;

import hse.dss.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByNameContainingIgnoreCase(String namePart);
}
