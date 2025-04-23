package hse.dss.service;

import hse.dss.dto.TestDto;
import hse.dss.entity.Task;
import hse.dss.entity.Test;
import hse.dss.repository.TaskRepository;
import hse.dss.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final TaskRepository taskRepository;

    public List<TestDto> findAllByTaskId(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        return testRepository.findByTask(task).stream()
                .map(t -> TestDto.builder()
                        .id(t.getId())
                        .taskId(taskId)
                        .input(t.getInput())
                        .build())
                .collect(Collectors.toList());
    }


    public Optional<TestDto> findById(Long id) {
        return testRepository.findById(id)
                .map(t -> TestDto.builder()
                        .id(t.getId())
                        .taskId(t.getTask().getId())
                        .input(t.getInput())
                        .build());
    }


    public void create(Long taskId, TestDto dto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        Test entity = Test.builder()
                .task(task)
                .input(dto.getInput())
                .build();
        Test saved = testRepository.save(entity);
    }

    public TestDto update(Long id, TestDto dto) {
        return testRepository.findById(id).map(existing -> {
            existing.setInput(dto.getInput());
            Test updated = testRepository.save(existing);
            return TestDto.builder()
                    .id(updated.getId())
                    .taskId(updated.getTask().getId())
                    .input(updated.getInput())
                    .build();
        }).orElseThrow(() -> new IllegalArgumentException("Test not found: " + id));
    }

    public void deleteById(Long id) {
        testRepository.deleteById(id);
    }


}
