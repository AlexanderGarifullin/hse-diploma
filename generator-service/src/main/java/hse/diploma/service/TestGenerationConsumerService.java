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
            // Преобразуем JSON-сообщение в POJO
            TestGenerationDTO request = objectMapper.readValue(message, TestGenerationDTO.class);
            Long taskId = request.taskId();

            // Найдем задачу по taskId
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if (taskOptional.isEmpty()) {
                log.error("Task with id {} not found", taskId);
                return;
            }
            Task task = taskOptional.get();

            // 1. получить схему
            var schemaOpt = schemaConfigRepository.findById(taskId.toString());
            if (schemaOpt.isEmpty()) {
                log.error("Schema for task {} not found in MongoDB", taskId);
                return;
            }

            var schema = schemaOpt.get().getSchema();
            log.info("Schema для задачи {} успешно загружена: {}", taskId, schema);

            // 2. сгенировать по схеме тесты и сорханить их
            List<String> tests = TestGenerator.generate(schema, null);
            for (String testInput : tests) {
                Test test = new Test();
                test.setTask(task);
                test.setInput(testInput);
                testRepository.save(test);
            }
        } catch (Exception e) {
            log.error("Error processing test generation message", e);
        }
    }
}