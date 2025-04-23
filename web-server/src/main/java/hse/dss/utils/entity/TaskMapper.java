package hse.dss.utils.entity;

import hse.dss.dto.TaskDto;
import hse.dss.entity.Task;
import org.springframework.stereotype.Component;


@Component
public class TaskMapper implements Mapper<TaskDto, Task> {

    @Override
    public TaskDto fromEntity(Task entity) {
        return TaskDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .timeLimit(entity.getTimeLimit())
                .memoryLimit(entity.getMemoryLimit())
                .legend(entity.getLegend())
                .inputData(entity.getInputData())
                .outputData(entity.getOutputData())
                .build();
    }

    @Override
    public Task toEntity(TaskDto dto) {
        return Task.builder()
                .name(dto.getName())
                .timeLimit(dto.getTimeLimit())
                .memoryLimit(dto.getMemoryLimit())
                .legend(dto.getLegend())
                .inputData(dto.getInputData())
                .outputData(dto.getOutputData())
                .build();
    }
}
