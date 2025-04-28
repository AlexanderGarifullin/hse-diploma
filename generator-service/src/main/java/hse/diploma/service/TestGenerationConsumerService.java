package hse.diploma.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hse.diploma.dto.TestGenerationDTO;
import hse.diploma.entity.Task;
import hse.diploma.entity.Test;
import hse.diploma.generator.TestGenerator;
import hse.diploma.repository.SchemaConfigRepository;
import hse.diploma.repository.TaskRepository;
import hse.diploma.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestGenerationConsumerService {

    private final TestRepository testRepository;
    private final SchemaConfigRepository schemaConfigRepository;
    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "schema-ready", groupId = "schema-ready-group")
    public void listenGenerateTests(String message) {
        try {
            log.info("Received message for test generation: {}", message);

            // Преобразуем JSON-сообщение в POJO
            TestGenerationDTO request = objectMapper.readValue(message, TestGenerationDTO.class);
            Long taskId = request.taskId();
            log.info("Parsed taskId from message: {}", taskId);

            // Найдем задачу по taskId
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if (taskOptional.isEmpty()) {
                log.error("Task with id {} not found in the database", taskId);
                return;
            }
            Task task = taskOptional.get();
            log.info("Task with id {} found successfully", taskId);
            // 1. получить схему
            var schemaOpt = schemaConfigRepository.findById(taskId.toString());
            if (schemaOpt.isEmpty()) {
                log.error("Schema for task id {} not found in MongoDB", taskId);
                return;
            }

            var schema = schemaOpt.get().getSchema();
            log.info("Schema for task id {} loaded successfully: {}", taskId, schema);

            // 2. сгенировать по схеме тесты и сорханить их
            List<String> tests = TestGenerator.generate(schema, null);
            log.info("Generated {} tests for task id {}", tests.size(), taskId);
            for (String testInput : tests) {
                Test test = new Test();
                test.setTask(task);
                test.setInput(testInput);
                try {
                    testRepository.save(test);
                    log.info("Successfully save test for task id {}", task.getId());
                } catch (DataIntegrityViolationException e) {
                    log.warn("Duplicate test detected for task id {}. Skipping input: {}", task.getId(), testInput.trim());
                }
            }
            log.info("All generated tests saved successfully for task id {}", taskId);
        } catch (Exception e) {
            log.error("Error while processing test generation message", e);
        }
    }
}