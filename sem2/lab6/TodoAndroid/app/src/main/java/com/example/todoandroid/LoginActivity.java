package com.example.todoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            String encodedAuth = Base64.encodeToString(
                    (username + ":" + password).getBytes(), Base64.NO_WRAP);

            Intent intent = new Intent(LoginActivity.this, TaskManagerActivity.class);
            intent.putExtra("authHeader", encodedAuth);
            startActivity(intent);
        });
    }
}
