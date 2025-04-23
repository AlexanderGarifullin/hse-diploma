package hse.dss.service;

import hse.dss.dto.TaskDto;
import hse.dss.entity.Task;
import hse.dss.entity.User;
import hse.dss.repository.TaskRepository;
import hse.dss.repository.UserRepository;
import hse.dss.utils.SecurityUtil;
import hse.dss.utils.entity.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public List<TaskDto> findAll(Optional<String> q) {
        List<Task> tasks = q
                .map(taskRepository::findByNameContainingIgnoreCase)
                .orElseGet(taskRepository::findAll);
        return tasks.stream()
                .map(taskMapper::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<TaskDto> findById(Long id) {
        return taskRepository.findById(id).map(taskMapper::fromEntity);
    }

    public TaskDto create(TaskDto dto) {
        Task task = taskMapper.toEntity(dto);
        String username = SecurityUtil.getCurrentUsername();

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException("Пользователь не найден: " + username));
        task.setOwner(owner);

        Task saved = taskRepository.save(task);
        return taskMapper.fromEntity(saved);
    }

    public TaskDto update(Long id, TaskDto dto) {
        return taskRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setTimeLimit(dto.getTimeLimit());
            existing.setMemoryLimit(dto.getMemoryLimit());
            existing.setLegend(dto.getLegend());
            existing.setInputData(dto.getInputData());
            existing.setOutputData(dto.getOutputData());
            Task updated = taskRepository.save(existing);
            return taskMapper.fromEntity(updated);
        }).orElseThrow(() -> new IllegalArgumentException("Задача не найдена: " + id));
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }


}
