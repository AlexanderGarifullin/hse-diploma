package hse.dss.service;

import hse.dss.dto.TaskDto;
import hse.dss.entity.Task;
import hse.dss.entity.User;
import hse.dss.repository.TaskRepository;
import hse.dss.repository.UserRepository;
import hse.dss.utils.SecurityUtil;
import hse.dss.utils.entity.TaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public List<TaskDto> findAll(Optional<String> q) {
        if (q.isPresent()) {
            log.info("Finding tasks with query: '{}'", q.get());
        } else {
            log.info("Finding all tasks (no query provided)");
        }
        List<Task> tasks = q
                .map(taskRepository::findByNameContainingIgnoreCase)
                .orElseGet(taskRepository::findAll);
        return tasks.stream()
                .map(taskMapper::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<TaskDto> findById(Long id) {
        log.info("Finding task by id: {}", id);
        return taskRepository.findById(id)
                .map(task -> {
                    log.info("Task found for id: {}", id);
                    return taskMapper.fromEntity(task);
                });
    }

    public TaskDto create(TaskDto dto) {
        log.info("Creating new task with name: '{}'", dto.getName());
        Task task = taskMapper.toEntity(dto);
        String username = SecurityUtil.getCurrentUsername();
        log.info("Current user: '{}'", username);
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new IllegalStateException("Пользователь не найден: " + username);
                });
        task.setOwner(owner);

        Task saved = taskRepository.save(task);
        log.info("Task successfully created with id: {}", saved.getId());
        return taskMapper.fromEntity(saved);
    }

    public TaskDto update(Long id, TaskDto dto) {
        log.info("Updating task id: {}", id);
        return taskRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setTimeLimit(dto.getTimeLimit());
            existing.setMemoryLimit(dto.getMemoryLimit());
            existing.setLegend(dto.getLegend());
            existing.setInputData(dto.getInputData());
            existing.setOutputData(dto.getOutputData());
            Task updated = taskRepository.save(existing);
            log.info("Task id: {} successfully updated", id);
            return taskMapper.fromEntity(updated);
        }).orElseThrow(() -> {
            log.warn("Task not found for updating id: {}", id);
            return new IllegalArgumentException("Задача не найдена: " + id);
        });
    }

    public void deleteById(Long id) {
        log.info("Deleting task by id: {}", id);
        taskRepository.deleteById(id);
        log.info("Task id: {} successfully deleted", id);
    }


}
