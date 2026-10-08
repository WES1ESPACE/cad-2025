package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void addTask(Task task) {
        if (task.getCategory() == null || task.getCategory().isBlank()) {
            task.setCategory("Личные дела");
        }
        taskRepository.addTask(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.getAllTasks();
    }

    public void deleteTask(Long id) {
        taskRepository.deleteTask(id);
    }

    public void updateCompleted(Long id, boolean completed) {
        taskRepository.findById(id).ifPresent(task -> task.setCompleted(completed));
    }
}
