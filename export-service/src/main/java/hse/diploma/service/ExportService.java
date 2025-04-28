package hse.diploma.service;

import hse.diploma.entity.Task;
import hse.diploma.entity.Test;
import hse.diploma.repository.TaskRepository;
import hse.diploma.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {

    private final TestRepository testRepository;
    private final TaskRepository taskRepository;

    public void writeTestToOutputStream(Long testId, OutputStream outputStream) {
        log.info("Attempting to write test with id {} to output stream", testId);

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> {
                    log.error("Test not found with id: {}", testId);
                    return new IllegalArgumentException("Test not found: " + testId);
                });
        try {
            outputStream.write(test.getInput().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            log.info("Successfully wrote test input for id {}", testId);
        } catch (Exception e) {
            log.error("Error writing test with id {} to output stream", testId, e);
            throw new RuntimeException("Error writing test: " + testId, e);
        }
    }

    public byte[] exportTests(Long taskId) {
        log.info("Starting export of tests for task id {}", taskId);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> {
            log.error("Task not found with id: {}", taskId);
            return new IllegalArgumentException("Task not found with id = " + taskId);
        });

        List<Test> tests = testRepository.findByTask(task);
        log.info("Found {} tests for task id {}", tests.size(), taskId);

        try (var baos = new ByteArrayOutputStream();
             var zos  = new ZipOutputStream(baos)) {

            for (Test test : tests) {
                String fileName = "test_" + test.getId() + ".txt";
                log.debug("Adding file '{}' to ZIP archive", fileName);
                zos.putNextEntry(new ZipEntry(fileName));
                byte[] data = test.getInput().getBytes(StandardCharsets.UTF_8);
                zos.write(data);
                zos.closeEntry();
            }

            zos.finish();
            log.info("Successfully created ZIP archive for task id {}", taskId);
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Error creating ZIP archive for task id {}", taskId, e);
            throw new RuntimeException("Error creating ZIP archive", e);
        }
    }
}
