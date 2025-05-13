package hse.diploma.controller;

import hse.diploma.service.ExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ExportControllerTest {

    @Mock
    private ExportService exportService;

    @InjectMocks
    private ExportController exportController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(exportController).build();
    }

    @Test
    void downloadAll_shouldReturnZipFile() throws Exception {
        Long taskId = 42L;
        byte[] zipData = "fake zip content".getBytes();

        when(exportService.exportTests(taskId)).thenReturn(zipData);

        mockMvc.perform(get("/export/{taskId}/downloadAll", taskId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"tests_42.zip\""))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(zipData));

        verify(exportService).exportTests(taskId);
    }

    @Test
    void download_shouldWriteToOutputStream() throws Exception {
        Long id = 123L;

        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);

        when(response.getOutputStream()).thenReturn(outputStream);

        exportController.download(id, response);

        verify(response).setContentType("text/plain; charset=UTF-8");
        verify(response).setHeader(eq("Content-Disposition"), contains("test-123.txt"));
        verify(exportService).writeTestToOutputStream(eq(id), eq(outputStream));
    }

    @Test
    void download_shouldThrowRuntimeExceptionOnFailure() throws Exception {
        Long id = 555L;

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenThrow(new RuntimeException("Stream failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            exportController.download(id, response);
        });

        assertThat(exception.getMessage()).contains("Error generating report for test id: " + id);
    }
}
