package hse.dss.controller;

import hse.dss.dto.TaskDto;
import hse.dss.entity.Task;
import hse.dss.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;


    @GetMapping
    public String list(@RequestParam Optional<String> q, Model m) {
        m.addAttribute("tasks", service.findAll(q));
        m.addAttribute("q", q.orElse(""));
        return "tasks";
    }

    @GetMapping("/new")
    public String createForm(Model m) {
        m.addAttribute("taskDto", new TaskDto());
        return "task_form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("taskDto") TaskDto dto,
                         BindingResult br) {
        if (br.hasErrors()) return "task_form";
        service.create(dto);
        return "redirect:/webclient/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model m) {
        TaskDto dto = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));
        m.addAttribute("taskDto", dto);
        return "task_form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("taskDto") TaskDto dto,
                         BindingResult br) {
        if (br.hasErrors()) return "task_form";
        service.update(id, dto);
        return "redirect:/webclient/tasks";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/webclient/tasks";
    }
}
