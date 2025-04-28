package hse.diploma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hse.diploma.dto.TestGenerationDTO;
import hse.diploma.entity.SchemaConfig;
import hse.diploma.entity.Task;
import hse.diploma.model.Schema;
import hse.diploma.parser.SchemaRegexBuilder;
import hse.diploma.repository.SchemaConfigRepository;
import hse.diploma.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchemaConfigServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SchemaConfigRepository schemaConfigRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private SchemaConfigService schemaConfigService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Task sampleTask;
    private Schema sampleSchema;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .inputData("1 2 3")
                .updatedAt(Instant.now())
                .build();

        sampleSchema = new Schema(List.of());
    }

    @Test
    void listenGenerateTests_whenTaskExistsAndSchemaOutdated_generatesNewSchema() throws JsonProcessingException {
        // Given
        String message = objectMapper.writeValueAsString(new TestGenerationDTO(1L));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(schemaConfigRepository.findById("1")).thenReturn(Optional.empty());

        mockStatic(SchemaRegexBuilder.class)
                .when(() -> SchemaRegexBuilder.parse(anyString()))
                .thenReturn(sampleSchema);

        // When
        schemaConfigService.listenGenerateTests(message);

        // Then
        verify(taskRepository).findById(1L);
        verify(schemaConfigRepository).save(any(SchemaConfig.class));
        verify(kafkaTemplate).send(eq("schema-ready"), eq("{\"taskId\": 1}"));
    }

    @Test
    void listenGenerateTests_whenTaskDoesNotExist_logsErrorAndDoesNothingElse() throws JsonProcessingException {
        // Given
        String message = objectMapper.writeValueAsString(new TestGenerationDTO(99L));
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        schemaConfigService.listenGenerateTests(message);

        // Then
        verify(taskRepository).findById(99L);
        verifyNoInteractions(schemaConfigRepository);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void listenGenerateTests_whenSchemaIsUpToDate_doesNotRegenerate() throws JsonProcessingException {
        // Given
        String message = objectMapper.writeValueAsString(new TestGenerationDTO(1L));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        SchemaConfig upToDateConfig = SchemaConfig.builder()
                .taskId("1")
                .updatedAt(Instant.now())
                .schema(sampleSchema)
                .build();
        when(schemaConfigRepository.findById("1")).thenReturn(Optional.of(upToDateConfig));

        // When
        schemaConfigService.listenGenerateTests(message);

        // Then
        verify(schemaConfigRepository, never()).save(any());
        verify(kafkaTemplate).send(eq("schema-ready"), eq("{\"taskId\": 1}"));
    }

    @Test
    void listenGenerateTests_whenInvalidJson_logsError() {
        // Given
        String invalidMessage = "invalid json";

        // When/Then
        assertThatNoException()
                .isThrownBy(() -> schemaConfigService.listenGenerateTests(invalidMessage));

        verifyNoInteractions(taskRepository);
        verifyNoInteractions(schemaConfigRepository);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}
