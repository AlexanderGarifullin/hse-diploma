package hse.dss.controller;

import hse.dss.dto.TaskDto;
import hse.dss.dto.TestDto;
import hse.dss.service.TaskService;
import hse.dss.service.TestService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.WebUtils;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tasks/{taskId}/tests")
@Slf4j
public class TestController {

    private final TaskService taskService;
    private final TestService testService;
    private final RestTemplate restTemplate;

    @GetMapping
    public String list(@PathVariable Long taskId, Model m) {
        log.info("Fetching test list for task id: {}", taskId);
        TaskDto task = taskService.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("Task not found while fetching tests: {}", taskId);
                    return new IllegalArgumentException("Task not found: " + taskId);
                });
        List<TestDto> tests = testService.findAllByTaskId(taskId);
        m.addAttribute("task", task);
        m.addAttribute("tests", tests);
        return "tests";
    }

    @GetMapping("/new")
    public String createForm(@PathVariable Long taskId, Model m) {
        log.info("Opening test creation form for task id: {}", taskId);
        m.addAttribute("testDto", new TestDto(null, taskId, ""));
        m.addAttribute("readOnly", false);
        return "test_form";
    }

    @PostMapping
    public String create(@PathVariable Long taskId,
                         @Valid @ModelAttribute("testDto") TestDto testDto,
                         BindingResult br,
                         Model m) {
        if (br.hasErrors()) {
            log.warn("Validation errors while creating test for task id: {}", taskId);
            m.addAttribute("testDto", testDto);
            m.addAttribute("readOnly", false);
            return "test_form";
        }
        log.info("Creating test for task id: {}", taskId);
        testService.create(taskId, testDto);
        return "redirect:/webclient/tasks/" + taskId + "/tests";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long taskId,
                           @PathVariable Long id,
                           Model m) {
        log.info("Opening edit form for test id: {} (task id: {})", id, taskId);
        TestDto dto = testService.findById(id)
                .orElseThrow(() -> {
                    log.warn("Test not found for editing: {}", id);
                    return new IllegalArgumentException("Test not found: " + id);
                });
        m.addAttribute("testDto", dto);
        m.addAttribute("readOnly", false);
        return "test_form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long taskId,
                         @PathVariable Long id,
                         @Valid @ModelAttribute("testDto") TestDto testDto,
                         BindingResult br,
                         Model m) {
        if (br.hasErrors()) {
            log.warn("Validation errors while updating test id: {}", id);
            m.addAttribute("testDto", testDto);
            m.addAttribute("readOnly", false);
            return "test_form";
        }
        log.info("Updating test id: {} (task id: {})", id, taskId);
        testService.update(id, testDto);
        return "redirect:/webclient/tasks/" + taskId + "/tests";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long taskId,
                         @PathVariable Long id) {
        log.info("Deleting test id: {} for task id: {}", id, taskId);
        testService.deleteById(id);
        return "redirect:/webclient/tasks/" + taskId + "/tests";
    }

    @PostMapping("/deleteAll")
    public String deleteAll(@PathVariable Long taskId) {
        log.info("Deleting all tests for task id: {}", taskId);
        testService.deleteAllByTaskId(taskId);
        return "redirect:/webclient/tasks/" + taskId + "/tests";
    }

    @GetMapping("/{id}/view")
    public String viewInput(@PathVariable Long taskId,
                            @PathVariable Long id,
                            Model m) {
        log.info("Viewing input for test id: {} (task id: {})", id, taskId);
        TestDto dto = testService.findById(id)
                .orElseThrow(() -> {
                    log.warn("Test not found for viewing input: {}", id);
                    return new IllegalArgumentException("Test not found: " + id);
                });
        m.addAttribute("input", dto.getInput());
        return "test_view";
    }


    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        log.info("Downloading single test with id: {}", id);
        try {
            String authHeader = "";
            Cookie jwtCookie = WebUtils.getCookie(request, "JWT");
            if (jwtCookie != null) {
                authHeader = "Bearer " + jwtCookie.getValue();
            }
            String finalAuthHeader = authHeader;
            RequestCallback callback = clientReq -> {
                clientReq.getHeaders().set(HttpHeaders.AUTHORIZATION, finalAuthHeader);
                clientReq.getHeaders().set("X-GATEWAY", "true");
            };

            // http://export-service/export/{id}/download
            restTemplate.execute(
                    "http://export-service/export/{id}/download",
                    HttpMethod.GET,
                    callback,
                    clientHttpResponse -> {
                        response.setContentType(
                                clientHttpResponse.getHeaders()
                                        .getContentType().toString());
                        response.setHeader(
                                HttpHeaders.CONTENT_DISPOSITION,
                                clientHttpResponse.getHeaders()
                                        .getFirst(HttpHeaders.CONTENT_DISPOSITION));

                        StreamUtils.copy(
                                clientHttpResponse.getBody(),
                                response.getOutputStream());
                        return null;
                    },
                    id
            );
        } catch (Exception e) {
            log.error("Error when downloading the test id: {}", id, e);
            throw new RuntimeException("Ошибка при выгрузке теста: " + id, e);
        }
    }

    @GetMapping("/downloadAll")
    public void downloadAll(@PathVariable Long taskId,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        log.info("Downloading all tests for task id: {}", taskId);
        try {
            String authHeader = "";
            Cookie jwtCookie = WebUtils.getCookie(request, "JWT");
            if (jwtCookie != null) {
                authHeader = "Bearer " + jwtCookie.getValue();
            }
            String finalAuthHeader = authHeader;

            RequestCallback callback = clientReq -> {
                if (!finalAuthHeader.isBlank()) {
                    clientReq.getHeaders().set(HttpHeaders.AUTHORIZATION, finalAuthHeader);
                }
                clientReq.getHeaders().set("X-GATEWAY", "true");
            };


            ResponseExtractor<Void> extractor = clientResp -> {
                MediaType ct = clientResp.getHeaders().getContentType();
                if (ct != null) {
                    response.setContentType(ct.toString());
                }
                clientResp.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)
                        .lines().forEach(disp -> response.setHeader(
                                HttpHeaders.CONTENT_DISPOSITION, disp
                        ));

                StreamUtils.copy(clientResp.getBody(), response.getOutputStream());
                return null;
            };

            restTemplate.execute(
                    "http://export-service/export/{taskId}/downloadAll",
                    HttpMethod.GET,
                    callback,
                    extractor,
                    taskId
            );

        } catch (Exception e) {
            log.error("Error when downloading all tests of the task id: {}", taskId, e);
            throw new RuntimeException("Ошибка при выгрузке всех тестов задачи " + taskId, e);
        }
    }

    @GetMapping("/viewTask")
    public String viewTask(@PathVariable Long taskId, Model m) {
        log.info("Viewing task details for task id: {}", taskId);
        TaskDto task = taskService.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("Task not found while viewing: {}", taskId);
                    return new IllegalArgumentException("Задача не найдена: " + taskId);
                });
        m.addAttribute("taskDto", task);
        m.addAttribute("readOnly", true);
        return "task_form";
    }

    @PostMapping("/generate")
    public String generate(@PathVariable Long taskId) {
        log.info("Requesting test generation for task id: {}", taskId);
        testService.requestTestGeneration(taskId);
        return "redirect:/webclient/tasks/" + taskId + "/tests";
    }

    @PostMapping("/check")
    public String check(@PathVariable Long taskId) {
        // TODO: implement check logic
        log.info("Requesting test check for task id: {}", taskId);
        return "redirect:/webclient/tasks/" + taskId + "/tests";
    }
}
