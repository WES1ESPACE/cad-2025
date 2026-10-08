package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private static final int MAX_LENGTH = 200;

    private final List<String> userMessages = new ArrayList<>();

    @GetMapping("/")
    public String helloWorld() {
        return "Hello, World!";
    }

    @GetMapping("/messages")
    public List<String> getAllMessages() {
        return userMessages;
    }

    @GetMapping("/messages/{index}")
    public ResponseEntity<?> getMessageByIndex(@PathVariable int index) {
        if (index < 0 || index >= userMessages.size()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Message not found at index " + index);
        }
        return ResponseEntity.ok(userMessages.get(index));
    }

    @GetMapping("/messages/count")
    public int countMessages() {
        return userMessages.size();
    }

    @PostMapping("/messages")
    public ResponseEntity<String> publishMessage(@RequestBody String message) {
        message = stripQuotes(message);
        String error = validateMessage(message);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        userMessages.add(message);
        return ResponseEntity.ok("Message published successfully!");
    }

    @PutMapping("/messages/{index}")
    public ResponseEntity<String> updateMessage(@PathVariable int index, @RequestBody String message) {
        if (index < 0 || index >= userMessages.size()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Message not found at index " + index);
        }
        message = stripQuotes(message);
        String error = validateMessage(message);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        userMessages.set(index, message);
        return ResponseEntity.ok("Message updated successfully!");
    }

    @DeleteMapping("/messages/{index}")
    public ResponseEntity<String> deleteMessage(@PathVariable int index) {
        if (index < 0 || index >= userMessages.size()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Message not found at index " + index);
        }
        userMessages.remove(index);
        return ResponseEntity.ok("Message deleted successfully!");
    }

    @DeleteMapping("/messages")
    public String clearAllMessages() {
        userMessages.clear();
        return "All messages cleared!";
    }

    // иногда в body приходят кавычки вместе со строкой
    private String stripQuotes(String message) {
        if (message == null) {
            return null;
        }
        message = message.trim();
        if (message.length() >= 2 && message.startsWith("\"") && message.endsWith("\"")) {
            return message.substring(1, message.length() - 1).trim();
        }
        return message;
    }

    private String validateMessage(String message) {
        if (message == null || message.isBlank()) {
            return "Message cannot be empty";
        }
        if (message.length() > MAX_LENGTH) {
            return "Message is too long (max " + MAX_LENGTH + " characters)";
        }
        if (userMessages.contains(message)) {
            return "Duplicate message is not allowed";
        }
        return null;
    }
}
