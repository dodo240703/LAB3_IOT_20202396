package com.example.lab3_20202396;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiService {
    private static final String BASE_URL = "https://opentdb.com/api.php";
    private final OkHttpClient client;
    private final Gson gson;

    public ApiService() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public List<Question> getQuestions(int amount, String category, String difficulty) throws IOException {
        String url = String.format("%s?amount=%d&category=%s&difficulty=%s&type=multiple",
                BASE_URL, amount, category, difficulty);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response);
            }

            String jsonData = response.body().string();
            JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
            JsonArray results = jsonObject.getAsJsonArray("results");

            List<Question> questions = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                JsonObject result = results.get(i).getAsJsonObject();
                String question = result.get("question").getAsString();
                String correctAnswer = result.get("correct_answer").getAsString();
                JsonArray incorrectAnswers = result.getAsJsonArray("incorrect_answers");
                List<String> incorrectAnswersList = new ArrayList<>();
                for (int j = 0; j < incorrectAnswers.size(); j++) {
                    incorrectAnswersList.add(incorrectAnswers.get(j).getAsString());
                }
                String categoryName = result.get("category").getAsString();
                String difficultyLevel = result.get("difficulty").getAsString();

                questions.add(new Question(question, correctAnswer, incorrectAnswersList, categoryName, difficultyLevel));
            }

            return questions;
        }
    }
} 