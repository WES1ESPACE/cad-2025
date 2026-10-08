package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SubTaskRepository subTaskRepository;

    @GetMapping("/")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
    public String index(Model model) {
        List<Task> tasks = taskRepository.findAll();
        model.addAttribute("tasks", tasks);
        model.addAttribute("categories", new String[]{"Учеба", "Личные дела", "Работа"});
        model.addAttribute("priorities", new String[]{"low", "normal", "high"});
        return "index";
    }

    @PostMapping("/addTask")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
    public String addTask(@ModelAttribute Task task) {
        taskRepository.save(task);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/deleteTask/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PostMapping("/updateTask/{id}")
    public String updateTask(@PathVariable Long id, @RequestParam boolean completed) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setCompleted(completed);
        taskRepository.save(task);
        return "redirect:/";
    }

    @PostMapping("/addSubTask/{taskId}")
    public String addSubTask(@PathVariable Long taskId, @RequestParam String description) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        SubTask subTask = new SubTask();
        subTask.setDescription(description);
        subTask.setCompleted(false);
        subTask.setTask(task);
        subTaskRepository.save(subTask);
        return "redirect:/";
    }

    @GetMapping("/api")
    @ResponseBody
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
    public ResponseEntity<List<Task>> getAllTasks(@RequestParam(required = false) String sort) {
        List<Task> tasks = new ArrayList<>(taskRepository.findAll());
        if ("priority".equalsIgnoreCase(sort)) {
            Collections.sort(tasks, Comparator.comparing(Task::getPriority, Comparator.nullsLast(String::compareTo)));
        } else if ("created".equalsIgnoreCase(sort)) {
            Collections.sort(tasks, Comparator.comparing(Task::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        }
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/api")
    @ResponseBody
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
    public ResponseEntity<Task> createTaskApi(@RequestBody Task task) {
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteTaskApi(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
