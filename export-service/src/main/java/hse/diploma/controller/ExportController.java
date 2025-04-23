package hse.diploma.controller;

import hse.diploma.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/export")
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id,
                         HttpServletResponse response) {
        try {
            String filename = "test-" + id + ".txt";
            response.setContentType("text/plain; charset=UTF-8");
            response.setHeader(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + "\"");

            exportService.writeTestToOutputStream(id, response.getOutputStream());

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при генерации отчёта: " + id, e);
        }
    }
}
