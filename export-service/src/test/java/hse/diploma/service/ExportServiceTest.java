package hse.diploma.service;

import hse.diploma.entity.Task;
import hse.diploma.entity.Test;
import hse.diploma.repository.TaskRepository;
import hse.diploma.repository.TestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock
    private TestRepository testRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ExportService exportService;

    private Test testEntity;
    private Task taskEntity;

    @BeforeEach
    void setup() {
        taskEntity = Task.builder()
                .id(1L)
                .build();

        testEntity = Test.builder()
                .id(100L)
                .task(taskEntity)
                .input("test input data")
                .build();
    }

    @org.junit.jupiter.api.Test
    void writeTestToOutputStream_success() throws Exception {
        // Given
        when(testRepository.findById(100L)).thenReturn(Optional.of(testEntity));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // When
        exportService.writeTestToOutputStream(100L, outputStream);

        // Then
        String output = outputStream.toString(StandardCharsets.UTF_8);
        assertThat(output).isEqualTo("test input data");
        verify(testRepository).findById(100L);
    }

    @org.junit.jupiter.api.Test
    void writeTestToOutputStream_testNotFound() {
        // Given
        when(testRepository.findById(100L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> exportService.writeTestToOutputStream(100L, new ByteArrayOutputStream()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Test not found");
    }

    @org.junit.jupiter.api.Test
    void exportTests_success() throws Exception {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(testRepository.findByTask(taskEntity)).thenReturn(List.of(testEntity));

        // When
        byte[] zipBytes = exportService.exportTests(1L);

        // Then
        assertThat(zipBytes).isNotEmpty();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry = zis.getNextEntry();
            assertThat(entry).isNotNull();
            assertThat(entry.getName()).isEqualTo("test_100.txt");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            zis.transferTo(baos);

            String content = baos.toString(StandardCharsets.UTF_8);
            assertThat(content).isEqualTo("test input data");
        }
    }

    @org.junit.jupiter.api.Test
    void exportTests_taskNotFound() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> exportService.exportTests(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task not found");
    }

    @org.junit.jupiter.api.Test
    void exportTests_zipCreationError_shouldThrowRuntimeException() {
        ExportService serviceWithBrokenStream = new ExportService(testRepository, taskRepository) {
            @Override
            public byte[] exportTests(Long taskId) {
                try (var baos = new ByteArrayOutputStream();
                     var zos = new ZipOutputStream(new OutputStream() {
                         @Override
                         public void write(int b) throws IOException {
                             throw new IOException("Simulated IOException");
                         }
                     })) {
                    zos.putNextEntry(new ZipEntry("broken.txt"));
                    zos.write("broken".getBytes(StandardCharsets.UTF_8));
                    zos.closeEntry();
                    return baos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("Error creating ZIP archive", e);
                }
            }
        };

        assertThatThrownBy(() -> serviceWithBrokenStream.exportTests(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error creating ZIP archive");
    }

}
