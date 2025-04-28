package com.example.lab3_20202396;

import java.util.List;

public class Question {
    private String question;
    private String correctAnswer;
    private List<String> incorrectAnswers;
    private String category;
    private String difficulty;

    public Question(String question, String correctAnswer, List<String> incorrectAnswers, String category, String difficulty) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
        this.category = category;
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }
} 