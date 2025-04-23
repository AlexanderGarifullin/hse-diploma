package hse.diploma.service;

import hse.diploma.entity.Test;
import hse.diploma.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final TestRepository testRepository;

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
}
