package hse.dss.controller;

import hse.dss.dto.TaskDto;
import hse.dss.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService service;

    @InjectMocks
    private TaskController controller;

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
            return new View() {
                @Override
                public String getContentType() {
                    return MediaType.TEXT_HTML_VALUE;
                }
                @Override
                public void render(Map<String, ?> model,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
                }
            };
        };

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .setViewResolvers(vr)
                .build();
    }

    @Test
    @DisplayName("GET /tasks → view=tasks, model contains tasks and q")
    void list_shouldPopulateModelAndReturnView() throws Exception {
        TaskDto dto1 = new TaskDto(); dto1.setName("A");
        TaskDto dto2 = new TaskDto(); dto2.setName("B");
        when(service.findAll(Optional.of("foo"))).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/tasks").param("q", "foo"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks"))
                .andExpect(model().attribute("tasks", List.of(dto1, dto2)))
                .andExpect(model().attribute("q", "foo"));

        verify(service).findAll(Optional.of("foo"));
    }

    @Test
    @DisplayName("GET /tasks/new → empty TaskDto, view=task_form")
    void createForm_shouldReturnForm() throws Exception {
        mockMvc.perform(get("/tasks/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("task_form"))
                .andExpect(model().attributeExists("taskDto"));
    }

    @Test
    @DisplayName("POST /tasks invalid → show form")
    void create_withValidationErrors_shouldShowForm() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("statement", "Missing name"))
                .andExpect(status().isOk())
                .andExpect(view().name("task_form"));

        verify(service, never()).create(any());
    }

    @Test
    @DisplayName("GET /tasks/{id}/edit exists → show form with dto")
    void editForm_existingId_shouldPopulateForm() throws Exception {
        TaskDto dto = new TaskDto(); dto.setName("X");
        when(service.findById(5L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/tasks/5/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("task_form"))
                .andExpect(model().attribute("taskDto", dto));

        verify(service).findById(5L);
    }


    @Test
    @DisplayName("POST /tasks/{id} invalid → show form")
    void update_withValidationErrors_shouldShowForm() throws Exception {
        mockMvc.perform(post("/tasks/7")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("statement", "No name"))
                .andExpect(status().isOk())
                .andExpect(view().name("task_form"));

        verify(service, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("POST /tasks/{id}/delete → call delete and redirect")
    void delete_shouldCallServiceAndRedirect() throws Exception {
        mockMvc.perform(post("/tasks/123/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webclient/tasks"));

        verify(service).deleteById(123L);
    }
}
