package hse.dss.service;

import hse.dss.dto.TestDto;
import hse.dss.entity.Task;

import hse.dss.entity.Test;
import hse.dss.repository.TaskRepository;
import hse.dss.repository.TestRepository;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TestServiceTest {

    @Mock
    private TestRepository testRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private TestService testService;

    private Task sampleTask;
    private Test sampleTest;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(10L)
                .name("Sample")
                .legend("Legend")
                .inputData("in")
                .outputData("out")
                .timeLimit(1000)
                .memoryLimit(128)
                .build();

        sampleTest = Test.builder()
                .id(5L)
                .task(sampleTask)
                .input("hello")
                .build();
    }

    @org.junit.jupiter.api.Test
    void findAllByTaskId_whenTaskExists_returnsDtoList() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(sampleTask));
        when(testRepository.findByTask(sampleTask)).thenReturn(List.of(sampleTest));

        var dtos = testService.findAllByTaskId(10L);

        assertThat(dtos).hasSize(1)
                .first()
                .extracting(TestDto::getId, TestDto::getTaskId, TestDto::getInput)
                .containsExactly(5L, 10L, "hello");

        verify(taskRepository).findById(10L);
        verify(testRepository).findByTask(sampleTask);
    }

    @org.junit.jupiter.api.Test
    void findAllByTaskId_whenTaskNotFound_throws() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> testService.findAllByTaskId(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task not found: 1");

        verify(taskRepository).findById(1L);
        verifyNoMoreInteractions(testRepository);
    }

    @org.junit.jupiter.api.Test
    void findById_whenFound_returnsDto() {
        when(testRepository.findById(5L)).thenReturn(Optional.of(sampleTest));

        Optional<TestDto> dto = testService.findById(5L);

        assertThat(dto).isPresent();
        assertThat(dto.get().getId()).isEqualTo(5L);
        assertThat(dto.get().getTaskId()).isEqualTo(10L);
        assertThat(dto.get().getInput()).isEqualTo("hello");

        verify(testRepository).findById(5L);
    }

    @org.junit.jupiter.api.Test
    void findById_whenNotFound_returnsEmpty() {
        when(testRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TestDto> dto = testService.findById(99L);

        assertThat(dto).isEmpty();
        verify(testRepository).findById(99L);
    }

    @org.junit.jupiter.api.Test
    void create_savesEntity() {
        var dto = TestDto.builder().taskId(10L).input("abc").build();
        when(taskRepository.findById(10L)).thenReturn(Optional.of(sampleTask));
        Test toSave = Test.builder().task(sampleTask).input("abc").build();
        Test saved = Test.builder().id(7L).task(sampleTask).input("abc").build();
        when(testRepository.save(any(Test.class))).thenReturn(saved);

        testService.create(10L, dto);

        ArgumentCaptor<Test> cap = ArgumentCaptor.forClass(Test.class);
        verify(testRepository).save(cap.capture());
        assertThat(cap.getValue().getTask()).isEqualTo(sampleTask);
        assertThat(cap.getValue().getInput()).isEqualTo("abc");
    }

    @org.junit.jupiter.api.Test
    void update_whenFound_updatesAndReturnsDto() {
        var dto = TestDto.builder().id(5L).taskId(10L).input("upd").build();
        when(testRepository.findById(5L)).thenReturn(Optional.of(sampleTest));
        Test updatedEntity = Test.builder()
                .id(5L).task(sampleTask).input("upd").build();
        when(testRepository.save(any(Test.class))).thenReturn(updatedEntity);

        TestDto result = testService.update(5L, dto);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getTaskId()).isEqualTo(10L);
        assertThat(result.getInput()).isEqualTo("upd");
        verify(testRepository).findById(5L);
        verify(testRepository).save(sampleTest);
    }

    @org.junit.jupiter.api.Test
    void update_whenNotFound_throws() {
        when(testRepository.findById(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> testService.update(8L, TestDto.builder().build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Test not found: 8");

        verify(testRepository).findById(8L);
        verifyNoMoreInteractions(testRepository);
    }

    @org.junit.jupiter.api.Test
    void deleteById_invokesRepository() {
        testService.deleteById(3L);
        verify(testRepository).deleteById(3L);
    }

    @org.junit.jupiter.api.Test
    void requestTestGeneration_sendsKafkaMessage() {
        testService.requestTestGeneration(42L);
        String expected = "{\"taskId\": 42}";
        verify(kafkaTemplate).send("generate-tests", expected);
    }

    @org.junit.jupiter.api.Test
    void deleteAllByTaskId_invokesRepository() {
        testService.deleteAllByTaskId(10L);
        verify(testRepository).deleteAllByTask_Id(10L);
    }
}
