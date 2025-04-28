package hse.diploma.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hse.diploma.dto.TestGenerationDTO;
import hse.diploma.entity.SchemaConfig;
import hse.diploma.entity.Task;
import hse.diploma.model.Schema;
import hse.diploma.repository.SchemaConfigRepository;
import hse.diploma.repository.TaskRepository;
import hse.diploma.repository.TestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TestGenerationConsumerServiceTest {

    @Mock
    private TestRepository testRepository;

    @Mock
    private SchemaConfigRepository schemaConfigRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TestGenerationConsumerService testGenerationConsumerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Task sampleTask;
    private Schema sampleSchema;

    @BeforeEach
    void setup() {
        sampleTask = Task.builder()
                .id(1L)
                .inputData("1 2 3")
                .build();

        sampleSchema = new Schema(List.of());
    }

    @Test
    void listenGenerateTests_successfullyGeneratesAndSavesTests() throws Exception {
        // Given
        String message = objectMapper.writeValueAsString(new TestGenerationDTO(1L));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(schemaConfigRepository.findById("1"))
                .thenReturn(Optional.of(SchemaConfig.builder().taskId("1").schema(sampleSchema).build()));

        try (MockedStatic<hse.diploma.generator.TestGenerator> mocked = mockStatic(hse.diploma.generator.TestGenerator.class)) {
            mocked.when(() -> hse.diploma.generator.TestGenerator.generate(any(), any()))
                    .thenReturn(List.of("input1", "input2"));

            // When
            testGenerationConsumerService.listenGenerateTests(message);

            // Then
            verify(testRepository, times(2)).save(any());
        }
    }

    @Test
    void listenGenerateTests_whenTaskNotFound_shouldLogAndDoNothing() throws Exception {
        // Given
        String message = objectMapper.writeValueAsString(new TestGenerationDTO(42L));
        when(taskRepository.findById(42L)).thenReturn(Optional.empty());

        // When
        testGenerationConsumerService.listenGenerateTests(message);

        // Then
        verifyNoInteractions(schemaConfigRepository);
        verifyNoInteractions(testRepository);
    }

    @Test
    void listenGenerateTests_whenSchemaNotFound_shouldLogAndDoNothing() throws Exception {
        // Given
        String message = objectMapper.writeValueAsString(new TestGenerationDTO(1L));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(schemaConfigRepository.findById("1")).thenReturn(Optional.empty());

        // When
        testGenerationConsumerService.listenGenerateTests(message);

        // Then
        verify(schemaConfigRepository).findById("1");
        verifyNoInteractions(testRepository);
    }

    @Test
    void listenGenerateTests_whenInvalidJson_shouldNotThrowException() {
        // Given
        String invalidMessage = "bad-json-format";

        // When/Then
        assertThatNoException()
                .isThrownBy(() -> testGenerationConsumerService.listenGenerateTests(invalidMessage));

        verifyNoInteractions(taskRepository);
        verifyNoInteractions(schemaConfigRepository);
        verifyNoInteractions(testRepository);
    }

    @Test
    void listenGenerateTests_whenDataIntegrityViolation_shouldSkipDuplicateTest() throws Exception {
        // Given
        String message = objectMapper.writeValueAsString(new TestGenerationDTO(1L));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(schemaConfigRepository.findById("1"))
                .thenReturn(Optional.of(SchemaConfig.builder().taskId("1").schema(sampleSchema).build()));

        try (MockedStatic<hse.diploma.generator.TestGenerator> mocked = mockStatic(hse.diploma.generator.TestGenerator.class)) {
            mocked.when(() -> hse.diploma.generator.TestGenerator.generate(any(), any()))
                    .thenReturn(List.of("input1"));

            when(testRepository.save(any()))
                    .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

            // When
            testGenerationConsumerService.listenGenerateTests(message);

            // Then
            verify(testRepository).save(any());
        }
    }
}
