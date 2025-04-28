package hse.diploma.controller;

import hse.diploma.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/export")
@Slf4j
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id,
                         HttpServletResponse response) {
        log.info("Received request to download test with id: {}", id);
        try {
            String filename = "test-" + id + ".txt";
            response.setContentType("text/plain; charset=UTF-8");
            response.setHeader(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + "\"");

            exportService.writeTestToOutputStream(id, response.getOutputStream());
            log.info("Successfully wrote test file for id: {}", id);
        } catch (Exception e) {
            log.error("Error occurred while generating report for test id: {}", id, e);
            throw new RuntimeException("Error generating report for test id: " + id, e);
        }
    }

    @GetMapping("/{taskId}/downloadAll")
    public ResponseEntity<ByteArrayResource> downloadAll(@PathVariable Long taskId) {
        log.info("Received request to download all tests for task id: {}", taskId);

        byte[] zipBytes = exportService.exportTests(taskId);

        var resource = new ByteArrayResource(zipBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("tests_" + taskId + ".zip")
                        .build()
        );
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        log.info("Successfully created zip file for task id: {} (size: {} bytes)", taskId, zipBytes.length);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(zipBytes.length)
                .body(resource);
    }
}
