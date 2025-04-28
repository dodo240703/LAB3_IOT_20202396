package com.example.lab3_20202396;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Spinner spinnerCategory;
    private EditText editTextAmount;
    private Spinner spinnerDifficulty;
    private Button buttonCheckConnection;
    private Button buttonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        editTextAmount = findViewById(R.id.editTextAmount);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        buttonCheckConnection = findViewById(R.id.buttonCheckConnection);
        buttonStart = findViewById(R.id.buttonStart);

        String[] categories = {
            "Cultura General", "Libros", "Películas", "Música",
            "Computación", "Matemática", "Deportes", "Historia"
        };
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        String[] difficulties = {"fácil", "medio", "difícil"};
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, difficulties);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);

        buttonCheckConnection.setOnClickListener(v -> checkConnection());
        buttonStart.setOnClickListener(v -> startTrivia());
    }

    private void checkConnection() {
        if (!validateInputs()) {
            return;
        }

        if (InternetChecker.isInternetAvailable(this)) {
            Toast.makeText(this, "Conexión exitosa", Toast.LENGTH_SHORT).show();
            buttonStart.setEnabled(true);
        } else {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
            buttonStart.setEnabled(false);
        }
    }

    private boolean validateInputs() {
        if (spinnerCategory.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return false;
        }

        String amountStr = editTextAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Ingrese la cantidad de preguntas", Toast.LENGTH_SHORT).show();
            return false;
        }

        int amount = Integer.parseInt(amountStr);
        if (amount <= 0) {
            Toast.makeText(this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinnerDifficulty.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione una dificultad", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void startTrivia() {
        if (!validateInputs()) {
            return;
        }

        String category = spinnerCategory.getSelectedItem().toString();
        int amount = Integer.parseInt(editTextAmount.getText().toString());
        String difficulty = spinnerDifficulty.getSelectedItem().toString();

        int categoryCode = getCategoryCode(category);
        String apiDifficulty = getApiDifficulty(difficulty);

        Intent intent = new Intent(this, TriviaActivity.class);
        intent.putExtra("category", categoryCode);
        intent.putExtra("amount", amount);
        intent.putExtra("difficulty", apiDifficulty);
        startActivity(intent);
    }

    private int getCategoryCode(String category) {
        switch (category) {
            case "Cultura General": return 9;
            case "Libros": return 10;
            case "Películas": return 11;
            case "Música": return 12;
            case "Computación": return 18;
            case "Matemática": return 19;
            case "Deportes": return 21;
            case "Historia": return 23;
            default: return 9;
        }
    }

    private String getApiDifficulty(String difficulty) {
        switch (difficulty) {
            case "fácil": return "easy";
            case "medio": return "medium";
            case "difícil": return "hard";
            default: return "easy";
        }
    }
}