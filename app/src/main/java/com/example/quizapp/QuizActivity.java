package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    TextView tvWelcome, tvQuestion, tvProgress;
    Button btnOption1, btnOption2, btnOption3, btnOption4, btnSubmit, btnNext;
    ProgressBar progressBar;
    Switch switchTheme;

    ArrayList<Question> questionList;
    int currentQuestionIndex = 0;
    int selectedAnswerIndex = -1;
    int score = 0;
    boolean answered = false;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences("QuizAppPrefs", MODE_PRIVATE);

        boolean isDark = preferences.getBoolean("dark_mode", false);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgress = findViewById(R.id.tvProgress);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnNext = findViewById(R.id.btnNext);
        progressBar = findViewById(R.id.progressBar);
        switchTheme = findViewById(R.id.switchTheme);

        String userName = getIntent().getStringExtra("user_name");
        tvWelcome.setText("Welcome, " + userName + "!");

        switchTheme.setChecked(isDark);
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("dark_mode", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate();
        });

        questionList = new ArrayList<>();
        loadQuestions();

        setOptionClick(btnOption1, 0);
        setOptionClick(btnOption2, 1);
        setOptionClick(btnOption3, 2);
        setOptionClick(btnOption4, 3);

        btnSubmit.setOnClickListener(v -> checkAnswer());
        btnNext.setOnClickListener(v -> goToNextQuestion());

        displayQuestion();
    }

    private void loadQuestions() {
        questionList.add(new Question("What does CPU stand for?",
                new String[]{"Central Process Unit", "Central Processing Unit", "Computer Personal Unit", "Central Processor Utility"}, 1));

        questionList.add(new Question("Which language is used in Android Studio?",
                new String[]{"Java", "Python", "HTML", "PHP"}, 0));

        questionList.add(new Question("What is 2 + 2?",
                new String[]{"3", "4", "5", "6"}, 1));

        questionList.add(new Question("Which company created Android?",
                new String[]{"Apple", "Microsoft", "Google", "Samsung"}, 2));

        questionList.add(new Question("Which component is used for user input?",
                new String[]{"TextView", "Button", "EditText", "ImageView"}, 2));
    }

    private void displayQuestion() {
        answered = false;
        selectedAnswerIndex = -1;
        resetOptionColors();
        enableOptions(true);

        Question currentQuestion = questionList.get(currentQuestionIndex);

        tvQuestion.setText(currentQuestion.getQuestionText());
        btnOption1.setText(currentQuestion.getOptions()[0]);
        btnOption2.setText(currentQuestion.getOptions()[1]);
        btnOption3.setText(currentQuestion.getOptions()[2]);
        btnOption4.setText(currentQuestion.getOptions()[3]);

        int progress = (int) (((currentQuestionIndex) / (float) questionList.size()) * 100);
        progressBar.setProgress(progress);
        tvProgress.setText("Question " + (currentQuestionIndex + 1) + "/" + questionList.size());
    }

    private void setOptionClick(Button button, int index) {
        button.setOnClickListener(v -> {
            if (!answered) {
                selectedAnswerIndex = index;
                resetOptionColors();
                button.setBackgroundColor(Color.LTGRAY);
            }
        });
    }

    private void checkAnswer() {
        if (selectedAnswerIndex == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        if (answered) return;

        answered = true;
        enableOptions(false);

        Question currentQuestion = questionList.get(currentQuestionIndex);
        int correctIndex = currentQuestion.getCorrectAnswerIndex();

        Button[] optionButtons = {btnOption1, btnOption2, btnOption3, btnOption4};

        optionButtons[correctIndex].setBackgroundColor(Color.GREEN);

        if (selectedAnswerIndex != correctIndex) {
            optionButtons[selectedAnswerIndex].setBackgroundColor(Color.RED);
        } else {
            score++;
        }
    }

    private void goToNextQuestion() {
        if (!answered) {
            Toast.makeText(this, "Please submit your answer first", Toast.LENGTH_SHORT).show();
            return;
        }

        currentQuestionIndex++;

        if (currentQuestionIndex < questionList.size()) {
            displayQuestion();
        } else {
            progressBar.setProgress(100);
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("score", score);
            intent.putExtra("total", questionList.size());
            startActivity(intent);
            finish();
        }
    }

    private void resetOptionColors() {
        btnOption1.setBackgroundColor(Color.parseColor("#D3D3D3"));
        btnOption2.setBackgroundColor(Color.parseColor("#D3D3D3"));
        btnOption3.setBackgroundColor(Color.parseColor("#D3D3D3"));
        btnOption4.setBackgroundColor(Color.parseColor("#D3D3D3"));
    }

    private void enableOptions(boolean enable) {
        btnOption1.setEnabled(enable);
        btnOption2.setEnabled(enable);
        btnOption3.setEnabled(enable);
        btnOption4.setEnabled(enable);
    }
}