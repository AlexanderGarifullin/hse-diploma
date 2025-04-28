package hse.dss.service;

import hse.dss.dto.TestDto;
import hse.dss.entity.Task;
import hse.dss.entity.Test;
import hse.dss.repository.TaskRepository;
import hse.dss.repository.TestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final TaskRepository taskRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC_GENERATE_TESTS = "generate-tests";

    public List<TestDto> findAllByTaskId(Long taskId) {
        log.info("Fetching all tests for task id: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
            log.warn("Task not found while fetching tests for task id: {}", taskId);
            return new IllegalArgumentException("Task not found: " + taskId);
        });
        List<TestDto> tests = testRepository.findByTask(task).stream()
                .map(t -> TestDto.builder()
                        .id(t.getId())
                        .taskId(taskId)
                        .input(t.getInput())
                        .build())
                .collect(Collectors.toList());
        log.info("Found {} tests for task id: {}", tests.size(), taskId);
        return tests;
    }


    public Optional<TestDto> findById(Long id) {
        log.info("Finding test by id: {}", id);
        return testRepository.findById(id)
                .map(t -> {
                    log.info("Test found with id: {}", id);
                    return TestDto.builder()
                            .id(t.getId())
                            .taskId(t.getTask().getId())
                            .input(t.getInput())
                            .build();
                });
    }


    public void create(Long taskId, TestDto dto) {
        log.info("Creating test for task id: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("Task not found while creating test for task id: {}", taskId);
                    return new IllegalArgumentException("Task not found: " + taskId);
                });
        Test entity = Test.builder()
                .task(task)
                .input(dto.getInput())
                .build();
        Test saved = testRepository.save(entity);
        log.info("Test created successfully with id: {}", saved.getId());
    }

    public TestDto update(Long id, TestDto dto) {
        log.info("Updating test with id: {}", id);
        return testRepository.findById(id).map(existing -> {
            existing.setInput(dto.getInput());
            Test updated = testRepository.save(existing);
            log.info("Test updated successfully with id: {}", updated.getId());
            return TestDto.builder()
                    .id(updated.getId())
                    .taskId(updated.getTask().getId())
                    .input(updated.getInput())
                    .build();
        }).orElseThrow(() -> {
            log.warn("Test not found for updating, id: {}", id);
            return new IllegalArgumentException("Test not found: " + id);
        });
    }

    public void deleteById(Long id) {
        log.info("Deleting test with id: {}", id);
        testRepository.deleteById(id);
        log.info("Test with id: {} deleted successfully", id);
    }


    public void requestTestGeneration(Long taskId) {
        log.info("Requesting test generation for task id: {}", taskId);
        String message = String.format("{\"taskId\": %d}", taskId);
        kafkaTemplate.send(TOPIC_GENERATE_TESTS, message);
        log.info("Test generation request sent to Kafka for task id: {}", taskId);
    }

    @Transactional
    public void deleteAllByTaskId(Long taskId) {
        log.info("Deleting all tests for task id: {}", taskId);
        testRepository.deleteAllByTask_Id(taskId);
        log.info("All tests deleted for task id: {}", taskId);
    }
}
