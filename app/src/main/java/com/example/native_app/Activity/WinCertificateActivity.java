package com.example.native_app.Activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.native_app.R;

public class WinCertificateActivity extends AppCompatActivity {

    private EditText etAnswer;
    private TextView tvTimer;
    private Button btnSubmit;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_certificate);

        etAnswer = findViewById(R.id.et_answer);
        tvTimer = findViewById(R.id.tv_timer);
        btnSubmit = findViewById(R.id.btn_submit);

        startCountdown();
        btnSubmit.setOnClickListener(v -> {
            // Handle answer submission logic
            String answer = etAnswer.getText().toString().trim();
            // Submit answer logic here (e.g., send to server or store locally)
        });
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(millisUntilFinished / 1000 + " seconds remaining");
            }

            @Override
            public void onFinish() {
                tvTimer.setText("Time is up!");
                etAnswer.setEnabled(false); // Disable input
                btnSubmit.setEnabled(true); // Enable submit button
                btnSubmit.setBackgroundTintList(getResources().getColorStateList(R.color.purple));
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
