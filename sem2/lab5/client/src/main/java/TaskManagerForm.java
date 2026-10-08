import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class TaskManagerForm extends JFrame {

    private final JList<String> taskList;
    private final DefaultListModel<String> taskListModel;
    private final String authHeader;
    private final List<Long> taskIds = new ArrayList<>();
    private String currentSort = "created";

    public TaskManagerForm(String authHeader) {
        this.authHeader = authHeader;
        setTitle("Task Manager");
        setSize(480, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        loadTasks();
        add(new JScrollPane(taskList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Обновить задачи");
        JButton addButton = new JButton("Добавить");
        JButton deleteButton = new JButton("Удалить");
        JButton sortButton = new JButton("Сортировка");
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(sortButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadTasks());
        addButton.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteSelected());
        sortButton.addActionListener(e -> {
            currentSort = "priority".equals(currentSort) ? "created" : "priority";
            loadTasks();
        });
    }

    private void loadTasks() {
        try {
            URL url = new URL("http://localhost:8080/api?sort=" + currentSort);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + authHeader);

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder jsonResponse = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    jsonResponse.append(inputLine);
                }
                in.close();
                parseJson(jsonResponse.toString());
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки задач");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Нет связи с сервером. Запустите sem2/lab5/server.");
        }
    }

    private void addTask() {
        String description = JOptionPane.showInputDialog(this, "Описание задачи:");
        if (description == null || description.isBlank()) {
            return;
        }
        try {
            URL url = new URL("http://localhost:8080/api");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Basic " + authHeader);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            String body = "{\"description\":\"" + description.replace("\"", "'") + "\",\"title\":\""
                    + description.replace("\"", "'") + "\",\"completed\":false}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
                loadTasks();
            } else {
                JOptionPane.showMessageDialog(this, "Не удалось добавить задачу (" + conn.getResponseCode() + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteSelected() {
        int index = taskList.getSelectedIndex();
        if (index < 0 || index >= taskIds.size()) {
            JOptionPane.showMessageDialog(this, "Выберите задачу");
            return;
        }
        Long id = taskIds.get(index);
        try {
            URL url = new URL("http://localhost:8080/api/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Basic " + authHeader);
            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                loadTasks();
            } else {
                JOptionPane.showMessageDialog(this, "Удаление запрещено или ошибка (" + code + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJson(String json) {
        json = json.trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1);
        }
        String[] tasksArray = json.isBlank() ? new String[0] : json.split("\\},\\{");
        int taskNumber = 1;
        taskListModel.clear();
        taskIds.clear();
        for (String task : tasksArray) {
            task = task.replace("{", "").replace("}", "").trim();
            if (task.isEmpty()) {
                continue;
            }
            String description = extractField(task, "description");
            String id = extractField(task, "id");
            String category = extractField(task, "category");
            String priority = extractField(task, "priority");
            if (description != null) {
                String line = "Задача " + taskNumber++ + ": " + description;
                if (category != null) {
                    line += " [" + category + "]";
                }
                if (priority != null) {
                    line += " (" + priority + ")";
                }
                taskListModel.addElement(line);
                if (id != null) {
                    taskIds.add(Long.parseLong(id));
                } else {
                    taskIds.add(-1L);
                }
            }
        }
    }

    private String extractField(String task, String fieldName) {
        String[] fields = task.split(",");
        for (String field : fields) {
            String[] keyValue = field.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");
                if (key.equals(fieldName)) {
                    return value;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String authHeader = "VXNlcjpwYXNzd29yZA==";
        SwingUtilities.invokeLater(() -> new TaskManagerForm(authHeader).setVisible(true));
    }
}
