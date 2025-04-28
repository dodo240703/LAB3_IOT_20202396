package com.example.lab3_20202396;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    private TextView textViewCorrect;
    private TextView textViewIncorrect;
    private TextView textViewUnanswered;
    private Button buttonPlayAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textViewCorrect = findViewById(R.id.textViewCorrect);
        textViewIncorrect = findViewById(R.id.textViewIncorrect);
        textViewUnanswered = findViewById(R.id.textViewUnanswered);
        buttonPlayAgain = findViewById(R.id.buttonPlayAgain);

        int correct = getIntent().getIntExtra("correct", 0);
        int incorrect = getIntent().getIntExtra("incorrect", 0);
        int unanswered = getIntent().getIntExtra("unanswered", 0);

        textViewCorrect.setText(String.format("Respuestas correctas: %d", correct));
        textViewIncorrect.setText(String.format("Respuestas incorrectas: %d", incorrect));
        textViewUnanswered.setText(String.format("Preguntas sin responder: %d", unanswered));

        buttonPlayAgain.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
} 