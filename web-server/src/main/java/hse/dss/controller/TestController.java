package hse.dss.controller;

import hse.dss.dto.TaskDto;
import hse.dss.dto.TestDto;
import hse.dss.service.TaskService;
import hse.dss.service.TestService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tasks/{taskId}/tests")
public class TestController {

    private final TaskService taskService;
    private final TestService testService;

    @GetMapping
    public String list(@PathVariable Long taskId, Model m) {
        TaskDto task = taskService.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        List<TestDto> tests = testService.findAllByTaskId(taskId);
        m.addAttribute("task", task);
        m.addAttribute("tests", tests);
        return "tests";
    }

    @GetMapping("/new")
    public String createForm(@PathVariable Long taskId, Model m) {
        m.addAttribute("testDto", new TestDto(null, taskId, ""));
        return "test_form";
    }

    @PostMapping
    public String create(@PathVariable Long taskId,
                         @Valid @ModelAttribute("testDto") TestDto testDto,
                         BindingResult br,
                         Model m) {
        if (br.hasErrors()) {
            m.addAttribute("testDto", testDto);
            return "test_form";
        }

        testService.create(taskId, testDto);
        return "redirect:/webclient/tasks/" + taskId + "/tests";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long taskId,
                           @PathVariable Long id,
                           Model m) {
        TestDto dto = testService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + id));
        m.addAttribute("testDto", dto);
        return "test_form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long taskId,
                         @PathVariable Long id,
                         @Valid @ModelAttribute("testDto") TestDto testDto,
                         BindingResult br,
                         Model m) {
        if (br.hasErrors()) {
            m.addAttribute("testDto", testDto);
            return "test_form";
        }

        testService.update(id, testDto);
        return "redirect:/webclient/tasks/" + taskId + "/tests";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long taskId,
                         @PathVariable Long id) {
        testService.deleteById(id);
        return "redirect:/webclient/tasks/" + taskId + "/tests";
    }

    @GetMapping("/{id}/view")
    public String viewInput(@PathVariable Long taskId,
                            @PathVariable Long id,
                            Model m) {
        TestDto dto = testService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + id));
        m.addAttribute("input", dto.getInput());
        return "test_view";
    }


    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id,
                         HttpServletResponse resp) {
        try {
            String filename = "test-" + id + ".txt";
            resp.setContentType("text/plain; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            testService.writeTestToOutputStream(id, resp.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выгрузке теста: " + id, e);
        }
    }

    @GetMapping("/downloadAll")
    public void downloadAll(@PathVariable Long taskId,
                           HttpServletResponse resp) {
        // TODO: собрать все input в архив и вернуть
    }

    @PostMapping("/generate")
    public String generate(@PathVariable Long taskId) {
        // TODO:
        return "redirect:/webclient/tasks/" + taskId + "/tests";
    }

    @PostMapping("/check")
    public String check(@PathVariable Long taskId) {
        // TODO:
        return "redirect:/webclient/tasks/" + taskId + "/tests";
    }
}
