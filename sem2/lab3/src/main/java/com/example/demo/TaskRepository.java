package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class TaskRepository {

    private final List<Task> taskList = new ArrayList<>();

    public void addTask(Task task) {
        taskList.add(task);
    }

    public List<Task> getAllTasks() {
        return taskList;
    }

    public void deleteTask(Long id) {
        taskList.removeIf(task -> task.getId().equals(id));
    }

    public Optional<Task> findById(Long id) {
        return taskList.stream().filter(task -> task.getId().equals(id)).findFirst();
    }

    public void clear() {
        taskList.clear();
    }
}
