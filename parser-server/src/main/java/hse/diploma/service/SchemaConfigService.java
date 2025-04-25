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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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
        try {
            // Преобразуем JSON-сообщение в POJO
            TestGenerationDTO request = objectMapper.readValue(message, TestGenerationDTO.class);
            Long taskId = request.taskId();

            // Найдем задачу по taskId
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if (taskOptional.isEmpty()) {
                return;
            }

            Task task = taskOptional.get();
            Instant taskUpdated = task.getUpdatedAt();

            String key = task.getId().toString();

            boolean isUpToDate = schemaConfigRepository.findById(key)
                    .filter(cfg -> !taskUpdated.isAfter(cfg.getUpdatedAt()))
                    .isPresent();

            if (!isUpToDate) {
                String inputData = task.getInputData();
                Schema schema = SchemaRegexBuilder.parse(inputData);

                SchemaConfig cfg = SchemaConfig.builder()
                        .taskId(key)
                        .updatedAt(Instant.now())
                        .schema(schema)
                        .build();

                schemaConfigRepository.save(cfg);
            }

            String schemaReadyMsg = String.format("{\"taskId\": %d}", taskId);
            kafkaTemplate.send(TOPIC_SCHEMA_READY, schemaReadyMsg);
        } catch (Exception e) {

        }
    }
}
