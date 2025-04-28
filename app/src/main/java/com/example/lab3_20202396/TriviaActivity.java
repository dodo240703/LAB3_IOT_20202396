package com.example.lab3_20202396;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TriviaActivity extends AppCompatActivity {
    private TextView textViewQuestion;
    private RadioGroup radioGroupAnswers;
    private RadioButton[] radioButtons;
    private Button buttonNext;
    private TextView textViewTimer;
    private TimerService timerService;
    private boolean bound = false;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int incorrectAnswers = 0;
    private int unansweredQuestions = 0;
    private ApiService apiService;
    private Handler handler;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            timerService = binder.getService();
            bound = true;
            startTimer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        textViewQuestion = findViewById(R.id.textViewQuestion);
        radioGroupAnswers = findViewById(R.id.radioGroupAnswers);
        radioButtons = new RadioButton[]{
            findViewById(R.id.radioButton1),
            findViewById(R.id.radioButton2),
            findViewById(R.id.radioButton3),
            findViewById(R.id.radioButton4)
        };
        buttonNext = findViewById(R.id.buttonNext);
        textViewTimer = findViewById(R.id.textViewTimer);

        apiService = new ApiService();
        handler = new Handler(Looper.getMainLooper());

        int category = getIntent().getIntExtra("category", 9);
        int amount = getIntent().getIntExtra("amount", 10);
        String difficulty = getIntent().getStringExtra("difficulty");

        Intent intent = new Intent(this, TimerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        loadQuestions(category, amount, difficulty);

        buttonNext.setOnClickListener(v -> handleNextQuestion());
    }

    private void loadQuestions(int category, int amount, String difficulty) {
        new Thread(() -> {
            try {
                questions = apiService.getQuestions(amount, String.valueOf(category), difficulty);
                handler.post(this::displayCurrentQuestion);
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Error al cargar las preguntas", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private void displayCurrentQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishGame();
            return;
        }

        Question currentQuestion = questions.get(currentQuestionIndex);
        textViewQuestion.setText(currentQuestion.getQuestion());

        List<String> answers = new ArrayList<>();
        answers.add(currentQuestion.getCorrectAnswer());
        answers.addAll(currentQuestion.getIncorrectAnswers());
        Collections.shuffle(answers);

        for (int i = 0; i < radioButtons.length; i++) {
            radioButtons[i].setText(answers.get(i));
        }

        radioGroupAnswers.clearCheck();
    }

    private void handleNextQuestion() {
        int selectedId = radioGroupAnswers.getCheckedRadioButtonId();
        if (selectedId == -1) {
            unansweredQuestions++;
        } else {
            RadioButton selectedButton = findViewById(selectedId);
            String selectedAnswer = selectedButton.getText().toString();
            Question currentQuestion = questions.get(currentQuestionIndex);

            if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
                correctAnswers++;
            } else {
                incorrectAnswers++;
            }
        }

        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            displayCurrentQuestion();
        } else {
            finishGame();
        }
    }

    private void startTimer() {
        if (bound && questions != null) {
            long totalTime = calculateTotalTime();
            timerService.startTimer(totalTime);
            updateTimerDisplay();
        }
    }

    private long calculateTotalTime() {
        if (questions == null) return 0;

        long timePerQuestion;
        String difficulty = questions.get(0).getDifficulty();
        switch (difficulty) {
            case "easy":
                timePerQuestion = 5000; // 5 seconds
                break;
            case "medium":
                timePerQuestion = 7000; // 7 seconds
                break;
            case "hard":
                timePerQuestion = 10000; // 10 seconds
                break;
            default:
                timePerQuestion = 5000;
        }

        return timePerQuestion * questions.size();
    }

    private void updateTimerDisplay() {
        if (bound) {
            long timeLeft = timerService.getTimeLeftInMillis();
            if (timeLeft > 0) {
                textViewTimer.setText(String.format("Tiempo restante: %d segundos", timeLeft / 1000));
                handler.postDelayed(this::updateTimerDisplay, 1000);
            } else {
                finishGame();
            }
        }
    }

    private void finishGame() {
        if (bound) {
            timerService.stopTimer();
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("correct", correctAnswers);
        intent.putExtra("incorrect", incorrectAnswers);
        intent.putExtra("unanswered", unansweredQuestions);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }
} 