package com.example.native_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.native_app.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100; // Request code for Google Sign-In
    private static final String TAG = "GoogleSignIn";
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private String verificationId; // For OTP verification
    private PhoneAuthProvider.ForceResendingToken resendToken; // For resending OTP
    private EditText phoneNumberEditText, otpField;
    private Button sendOtpBtn, verifyOtpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        otpField = findViewById(R.id.otpField);
        sendOtpBtn = findViewById(R.id.sendOtpBtn);
        verifyOtpBtn = findViewById(R.id.verifyOtpBtn);

        sendOtpBtn.setOnClickListener(view -> sendOtp());
        verifyOtpBtn.setOnClickListener(view -> verifyOtp());

        TextView loginRedirectBtn = findViewById(R.id.loginRedirect);
        loginRedirectBtn.setOnClickListener(view -> redirectToSignup());

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Web Client ID from Firebase
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        ImageView googleSignInButton = findViewById(R.id.google);
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, navigate to the main activity
            Intent intent = new Intent(login.this, profile.class);
            startActivity(intent);
            finish();
        }
    }

    private void sendOtp() {
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            phoneNumberEditText.setError("Please enter a valid phone number");
            phoneNumberEditText.requestFocus();
            return;
        }

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+91" + phoneNumber) // Add country code if required
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(phoneAuthCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOtp() {
        String otp = otpField.getText().toString().trim();
        if (otp.isEmpty() || otp.length() < 6) {
            otpField.setError("Enter a valid OTP");
            otpField.requestFocus();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        Toast.makeText(login.this, "Welcome, " + (user != null ? user.getDisplayName() : "User"), Toast.LENGTH_SHORT).show();

                        // Navigate to the profile activity
                        Intent intent = new Intent(login.this, profile.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data)
                        .getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign-in failed", e);
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d(TAG, "signInWithCredential:success");
                        Toast.makeText(this, "Welcome, " + (user != null ? user.getDisplayName() : "User"), Toast.LENGTH_SHORT).show();

                        // Navigate to the main activity
                        Intent intent = new Intent(login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToSignup() {
        // Navigate to the Signup Activity
        Intent intent = new Intent(login.this, signup.class);
        startActivity(intent);
        finish();
    }

    // PhoneAuthProvider callbacks
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    // Automatically handle the OTP when verification is completed
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // Handle failure (e.g., invalid phone number, too many requests)
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(login.this, "Invalid phone number.", Toast.LENGTH_SHORT).show();
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        Toast.makeText(login.this, "Too many requests. Please try again later.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(login.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                    // Save the verification ID and resending token for later use
                    login.this.verificationId = verificationId;
                    resendToken = token;

                    // Update UI to show the OTP field and verify button
                    otpField.setVisibility(View.VISIBLE);
                    verifyOtpBtn.setVisibility(View.VISIBLE);
                    sendOtpBtn.setVisibility(View.GONE);
                    Toast.makeText(login.this, "OTP sent successfully.", Toast.LENGTH_SHORT).show();
                }
            };

}
