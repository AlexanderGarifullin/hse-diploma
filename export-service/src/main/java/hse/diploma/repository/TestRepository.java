package hse.diploma.repository;

import hse.diploma.entity.Task;
import hse.diploma.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByTask(Task task);
}
