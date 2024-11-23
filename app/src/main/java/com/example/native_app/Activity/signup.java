package com.example.native_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.native_app.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class signup extends AppCompatActivity {

    private EditText phoneNumber, otpField;
    private Button signupBtn, verifyOtpBtn;
    private ImageView googleSignInBtn;

    private String verificationId;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        phoneNumber = findViewById(R.id.signupPhoneNumber);
        otpField = findViewById(R.id.otpField);
        signupBtn = findViewById(R.id.signupBtn);
        verifyOtpBtn = findViewById(R.id.verifyOtpBtn);
        googleSignInBtn = findViewById(R.id.google);
        TextView loginRedirectBtn = findViewById(R.id.loginRedirect);

        // Set up Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Get from google-services.json
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set listeners
        signupBtn.setOnClickListener(view -> sendOtp());
        verifyOtpBtn.setOnClickListener(view -> verifyOtp());
        googleSignInBtn.setOnClickListener(view -> initiateGoogleSignIn());
        loginRedirectBtn.setOnClickListener(view -> redirectToLogin());

        // Handle Edge-to-Edge design
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void sendOtp() {
        String phone = phoneNumber.getText().toString().trim();

        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Signup", "Sending OTP to: " + phone);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+91" + phone) // Ensure the correct phone format
                .setTimeout(120L, TimeUnit.SECONDS) // Increase timeout to 2 minutes
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e("Signup", "OTP Verification Failed: " + e.getMessage());
                        Toast.makeText(signup.this, "OTP Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        signup.this.verificationId = verificationId;
                        Log.d("Signup", "OTP sent to: " + phone);  // Log OTP sending
                        Toast.makeText(signup.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                        phoneNumber.setVisibility(View.GONE);
                        signupBtn.setVisibility(View.GONE);
                        otpField.setVisibility(View.VISIBLE);
                        verifyOtpBtn.setVisibility(View.VISIBLE);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOtp() {
        String otp = otpField.getText().toString().trim();

        if (TextUtils.isEmpty(otp) || otp.length() < 6) {
            Toast.makeText(this, "Enter a valid OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(signup.this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
                        redirectToMain();
                    } else {
                        Log.e("Signup", "Authentication failed: " + task.getException().getMessage());
                        Toast.makeText(signup.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initiateGoogleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(Exception.class);
            if (account != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();
                                redirectToMain();
                            } else {
                                Log.e("Signup", "Google Sign-In Failed: " + task1.getException().getMessage());
                                Toast.makeText(this, "Google Sign-In Failed: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } catch (Exception e) {
            Log.e("Signup", "Google Sign-In Failed: " + e.getMessage());
            Toast.makeText(this, "Google Sign-In Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToMain() {
        Intent intent = new Intent(signup.this, profile.class);
        startActivity(intent);
        finish();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(signup.this, login.class);
        startActivity(intent);
        finish();
    }
}
