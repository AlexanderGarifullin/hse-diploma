package hse.dss.service;

import hse.dss.dto.TaskDto;
import hse.dss.entity.Task;
import hse.dss.entity.User;
import hse.dss.repository.TaskRepository;
import hse.dss.repository.UserRepository;
import hse.dss.utils.SecurityUtil;
import hse.dss.utils.entity.TaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;
    private TaskDto sampleDto;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .name("Sample Task")
                .legend("Legend")
                .inputData("input")
                .outputData("output")
                .timeLimit(1000)
                .memoryLimit(256)
                .build();

        sampleDto = TaskDto.builder()
                .id(1L)
                .name("Sample Task")
                .legend("Legend")
                .inputData("input")
                .outputData("output")
                .timeLimit(1000)
                .memoryLimit(256)
                .build();

        sampleUser = User.builder()
                .id(2L)
                .username("testuser")
                .password("password")
                .build();
    }

    @Test
    void findAll_withoutQuery_returnsAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));
        when(taskMapper.fromEntity(sampleTask)).thenReturn(sampleDto);

        List<TaskDto> tasks = taskService.findAll(Optional.empty());

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getName()).isEqualTo("Sample Task");

        verify(taskRepository).findAll();
        verify(taskMapper).fromEntity(sampleTask);
    }

    @Test
    void findAll_withQuery_returnsFilteredTasks() {
        when(taskRepository.findByNameContainingIgnoreCase("Sample")).thenReturn(List.of(sampleTask));
        when(taskMapper.fromEntity(sampleTask)).thenReturn(sampleDto);

        List<TaskDto> tasks = taskService.findAll(Optional.of("Sample"));

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getName()).isEqualTo("Sample Task");

        verify(taskRepository).findByNameContainingIgnoreCase("Sample");
        verify(taskMapper).fromEntity(sampleTask);
    }

    @Test
    void findById_whenFound_returnsDto() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskMapper.fromEntity(sampleTask)).thenReturn(sampleDto);

        Optional<TaskDto> found = taskService.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Sample Task");

        verify(taskRepository).findById(1L);
        verify(taskMapper).fromEntity(sampleTask);
    }

    @Test
    void findById_whenNotFound_returnsEmpty() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TaskDto> found = taskService.findById(99L);

        assertThat(found).isEmpty();

        verify(taskRepository).findById(99L);
    }

    @Test
    void create_successfullyCreatesTask() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("testuser");

            when(taskMapper.toEntity(sampleDto)).thenReturn(sampleTask);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
            when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
            when(taskMapper.fromEntity(sampleTask)).thenReturn(sampleDto);

            TaskDto created = taskService.create(sampleDto);

            assertThat(created).isNotNull();
            assertThat(created.getName()).isEqualTo("Sample Task");

            verify(taskRepository).save(sampleTask);
            verify(userRepository).findByUsername("testuser");
            verify(taskMapper).fromEntity(sampleTask);
        }
    }

    @Test
    void create_whenUserNotFound_throwsException() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUsername).thenReturn("unknownuser");

            when(taskMapper.toEntity(sampleDto)).thenReturn(sampleTask);
            when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.create(sampleDto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Пользователь не найден: unknownuser");

            verify(userRepository).findByUsername("unknownuser");
            verifyNoInteractions(taskRepository);
        }
    }

    @Test
    void update_whenFound_updatesAndReturnsDto() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
        when(taskMapper.fromEntity(sampleTask)).thenReturn(sampleDto);

        TaskDto updated = taskService.update(1L, sampleDto);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Sample Task");

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(sampleTask);
        verify(taskMapper).fromEntity(sampleTask);
    }

    @Test
    void update_whenNotFound_throwsException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(99L, sampleDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Задача не найдена: 99");

        verify(taskRepository).findById(99L);
    }

    @Test
    void deleteById_invokesRepository() {
        taskService.deleteById(5L);

        verify(taskRepository).deleteById(5L);
    }
}
