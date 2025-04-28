package hse.diploma.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hse.diploma.dto.TestGenerationDTO;
import hse.diploma.entity.SchemaConfig;
import hse.diploma.entity.Task;
import hse.diploma.model.Schema;
import hse.diploma.parser.SchemaRegexBuilder;
import hse.diploma.repository.SchemaConfigRepository;
import hse.diploma.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaConfigService {

    private final TaskRepository taskRepository;
    private final SchemaConfigRepository schemaConfigRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TOPIC_SCHEMA_READY = "schema-ready";

    @KafkaListener(topics = "generate-tests", groupId = "test-generation-group")
    public void listenGenerateTests(String message) {
        log.info("Received message for schema generation: {}", message);
        try {
            // Преобразуем JSON-сообщение в POJO
            TestGenerationDTO request = objectMapper.readValue(message, TestGenerationDTO.class);
            Long taskId = request.taskId();
            log.info("Parsed taskId from message: {}", taskId);

            // Найдем задачу по taskId
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if (taskOptional.isEmpty()) {
                log.error("Task with id {} not found", taskId);
                return;
            }

            Task task = taskOptional.get();
            Instant taskUpdated = task.getUpdatedAt();

            String key = task.getId().toString();

            boolean isUpToDate = schemaConfigRepository.findById(key)
                    .filter(cfg -> !taskUpdated.isAfter(cfg.getUpdatedAt()))
                    .isPresent();

            if (!isUpToDate) {
                log.info("Generating new schema for task id {}", taskId);
                String inputData = task.getInputData();
                Schema schema = SchemaRegexBuilder.parse(inputData);

                SchemaConfig cfg = SchemaConfig.builder()
                        .taskId(key)
                        .updatedAt(Instant.now())
                        .schema(schema)
                        .build();

                schemaConfigRepository.save(cfg);
                log.info("New schema saved for task id {}", taskId);
            } else {
                log.info("Schema for task id {} is already up-to-date", taskId);
            }

            String schemaReadyMsg = String.format("{\"taskId\": %d}", taskId);
            kafkaTemplate.send(TOPIC_SCHEMA_READY, schemaReadyMsg);
            log.info("Sent schema-ready message for task id {}", taskId);
        } catch (Exception e) {
            log.error("Error while processing schema generation message", e);
        }
    }
}
