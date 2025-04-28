package hse.dss.controller;

import hse.dss.dto.TaskDto;
import hse.dss.entity.Task;
import hse.dss.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tasks")
@Slf4j
public class TaskController {

    private final TaskService service;


    @GetMapping
    public String list(@RequestParam Optional<String> q, Model m) {
        log.info("Fetching task list with search query: '{}'", q.orElse(""));
        m.addAttribute("tasks", service.findAll(q));
        m.addAttribute("q", q.orElse(""));
        return "tasks";
    }

    @GetMapping("/new")
    public String createForm(Model m) {
        log.info("Opening task creation form");
        m.addAttribute("taskDto", new TaskDto());
        return "task_form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("taskDto") TaskDto dto,
                         BindingResult br) {
        if (br.hasErrors()) {
            log.warn("Task creation form has validation errors");
            return "task_form";
        }
        log.info("Creating new task with name: '{}'", dto.getName());
        service.create(dto);
        return "redirect:/webclient/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model m) {
        log.info("Opening edit form for task with id: {}", id);
        TaskDto dto = service.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task with id {} not found for editing", id);
                    return new IllegalArgumentException("Задача не найдена");
                });
        m.addAttribute("taskDto", dto);
        return "task_form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("taskDto") TaskDto dto,
                         BindingResult br) {
        if (br.hasErrors()) {
            log.warn("Task update form has validation errors for task id: {}", id);
            return "task_form";
        }
        log.info("Updating task with id: {}", id);
        service.update(id, dto);
        return "redirect:/webclient/tasks";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        log.info("Deleting task with id: {}", id);
        service.deleteById(id);
        return "redirect:/webclient/tasks";
    }
}
