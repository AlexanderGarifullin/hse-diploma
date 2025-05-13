package hse.dss.controller;

import hse.dss.dto.TaskDto;
import hse.dss.dto.TestDto;
import hse.dss.service.TaskService;
import hse.dss.service.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {

    @Mock private TaskService taskService;
    @Mock private TestService testService;
    @Mock private RestTemplate restTemplate;
    @InjectMocks private TestController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        ViewResolver vr = (viewName, locale) -> {
            if (viewName.startsWith("redirect:")) {
                String url = viewName.substring("redirect:".length());
                RedirectView rv = new RedirectView(url, false);
                rv.setContextRelative(true);
                return rv;
            }
            return (model, req, res) -> {  };
        };

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setValidator(validator)
                .setViewResolvers(vr)
                .build();
    }

    @Test @DisplayName("GET /tasks/{taskId}/tests → tests view")
    void list_shouldReturnTestsView() throws Exception {
        long taskId = 7;
        TaskDto dto = new TaskDto(); dto.setName("T");
        when(taskService.findById(taskId)).thenReturn(Optional.of(dto));
        TestDto t1 = new TestDto(1L, taskId, "in1");
        when(testService.findAllByTaskId(taskId)).thenReturn(List.of(t1));

        mockMvc.perform(get("/tasks/{taskId}/tests", taskId))
                .andExpect(status().isOk())
                .andExpect(view().name("tests"))
                .andExpect(model().attribute("task", dto))
                .andExpect(model().attribute("tests", List.of(t1)));

        verify(taskService).findById(taskId);
        verify(testService).findAllByTaskId(taskId);
    }

    @Test @DisplayName("GET /tasks/{taskId}/tests/new → test_form")
    void createForm_shouldShowEmptyForm() throws Exception {
        mockMvc.perform(get("/tasks/5/tests/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("test_form"))
                .andExpect(model().attributeExists("testDto", "readOnly"));
    }

    @Test @DisplayName("POST /tasks/{taskId}/tests valid → redirect")
    void create_shouldCallServiceAndRedirect() throws Exception {
        mockMvc.perform(post("/tasks/3/tests")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("taskId", "3")
                        .param("input", "foo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webclient/tasks/3/tests"));

        ArgumentCaptor<TestDto> cap = ArgumentCaptor.forClass(TestDto.class);
        verify(testService).create(eq(3L), cap.capture());
        assertThat(cap.getValue().getInput()).isEqualTo("foo");
    }

    @Test @DisplayName("POST /tests create validation error → form")
    void create_withErrors_shouldShowForm() throws Exception {
        mockMvc.perform(post("/tasks/3/tests")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("taskId", "3")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("test_form"))
                .andExpect(model().attribute("readOnly", false));

        verify(testService, never()).create(anyLong(), any());
    }

    @Test @DisplayName("GET /tests/{id}/edit exists → form")
    void editForm_existing_shouldShowForm() throws Exception {
        long id = 11, taskId = 4;
        TestDto dto = new TestDto(id, taskId, "in");
        when(testService.findById(id)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/tasks/{taskId}/tests/{id}/edit", taskId, id))
                .andExpect(status().isOk())
                .andExpect(view().name("test_form"))
                .andExpect(model().attribute("testDto", dto))
                .andExpect(model().attribute("readOnly", false));

        verify(testService).findById(id);
    }


    @Test @DisplayName("POST /tests/{id} valid → update & redirect")
    void update_shouldCallServiceAndRedirect() throws Exception {
        long id = 5, taskId = 2;
        mockMvc.perform(post("/tasks/{taskId}/tests/{id}", taskId, id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", String.valueOf(id))
                        .param("taskId", String.valueOf(taskId))
                        .param("input", "xyz"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webclient/tasks/2/tests"));

        ArgumentCaptor<TestDto> cap = ArgumentCaptor.forClass(TestDto.class);
        verify(testService).update(eq(id), cap.capture());
        assertThat(cap.getValue().getInput()).isEqualTo("xyz");
    }

    @Test @DisplayName("POST /tests/{id} invalid → form")
    void update_withErrors_shouldShowForm() throws Exception {
        mockMvc.perform(post("/tasks/2/tests/{id}", 2)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("taskId", "2")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("test_form"));

        verify(testService, never()).update(anyLong(), any());
    }

    @Test @DisplayName("POST /tests/{id}/delete → delete & redirect")
    void delete_shouldCallServiceAndRedirect() throws Exception {
        mockMvc.perform(post("/tasks/7/tests/8/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webclient/tasks/7/tests"));

        verify(testService).deleteById(8L);
    }

    @Test @DisplayName("POST /tasks/{taskId}/tests/deleteAll → redirect")
    void deleteAll_shouldCallServiceAndRedirect() throws Exception {
        mockMvc.perform(post("/tasks/9/tests/deleteAll"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webclient/tasks/9/tests"));

        verify(testService).deleteAllByTaskId(9L);
    }

    @Test @DisplayName("GET /tests/{id}/view → test_view")
    void viewInput_shouldReturnTestView() throws Exception {
        long id = 3, taskId = 1;
        TestDto dto = new TestDto(id, taskId, "inp");
        when(testService.findById(id)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/tasks/{taskId}/tests/{id}/view", taskId, id))
                .andExpect(status().isOk())
                .andExpect(view().name("test_view"))
                .andExpect(model().attribute("input", "inp"));
    }


    @Test @DisplayName("GET /tasks/{taskId}/tests/{id}/download → restTemplate.execute called")
    void download_shouldInvokeRestTemplate() throws Exception {
        mockMvc.perform(get("/tasks/10/tests/5/download"))
                .andExpect(status().isOk());

        verify(restTemplate).execute(
                eq("http://export-service/export/{id}/download"),
                eq(HttpMethod.GET),
                any(RequestCallback.class),
                any(ResponseExtractor.class),
                eq(5L)
        );
    }

    @Test @DisplayName("GET /tasks/{taskId}/tests/downloadAll → restTemplate.execute called")
    void downloadAll_shouldInvokeRestTemplate() throws Exception {
        mockMvc.perform(get("/tasks/15/tests/downloadAll"))
                .andExpect(status().isOk());

        verify(restTemplate).execute(
                eq("http://export-service/export/{taskId}/downloadAll"),
                eq(HttpMethod.GET),
                any(RequestCallback.class),
                any(ResponseExtractor.class),
                eq(15L)
        );
    }

    @Test @DisplayName("GET /tasks/{taskId}/tests/viewTask → task_form readOnly")
    void viewTask_shouldReturnReadOnlyForm() throws Exception {
        long tid = 22;
        TaskDto dto = new TaskDto();
        dto.setName("Z");
        when(taskService.findById(tid)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/tasks/{taskId}/tests/viewTask", tid))
                .andExpect(status().isOk())
                .andExpect(view().name("task_form"))
                .andExpect(model().attribute("taskDto", dto))
                .andExpect(model().attribute("readOnly", true));
    }

    @Test @DisplayName("POST /tasks/{taskId}/tests/generate → redirect")
    void generate_shouldRequestAndRedirect() throws Exception {
        mockMvc.perform(post("/tasks/33/tests/generate"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webclient/tasks/33/tests"));

        verify(testService).requestTestGeneration(33L);
    }

    @Test @DisplayName("POST /tasks/{taskId}/tests/check → redirect")
    void check_shouldRedirect() throws Exception {
        mockMvc.perform(post("/tasks/44/tests/check"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webclient/tasks/44/tests"));
    }
}
