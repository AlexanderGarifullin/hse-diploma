package hse.diploma.service;

import hse.diploma.entity.Task;
import hse.diploma.entity.Test;
import hse.diploma.repository.TaskRepository;
import hse.diploma.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final TestRepository testRepository;
    private final TaskRepository taskRepository;

    public void writeTestToOutputStream(Long testId, OutputStream outputStream) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + testId));
        try {
            outputStream.write(test.getInput().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при записи теста: " + testId, e);
        }
    }

    public byte[] exportTests(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new IllegalArgumentException("Not found task with id = " + taskId)
        );

        List<Test> tests = testRepository.findByTask(task);

        try (var baos = new ByteArrayOutputStream();
             var zos  = new ZipOutputStream(baos)) {

            for (Test test : tests) {
                String fileName = "test_" + test.getId() + ".txt";
                zos.putNextEntry(new ZipEntry(fileName));
                byte[] data = test.getInput().getBytes(StandardCharsets.UTF_8);
                zos.write(data);
                zos.closeEntry();
            }

            zos.finish();
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error creating ZIP archive", e);
        }
    }
}
