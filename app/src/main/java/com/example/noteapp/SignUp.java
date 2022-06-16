package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {
    TextInputLayout phoneNumber, password;
    Button button;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String verificationID;
    TextView textView;
    TextInputLayout code;
    Button buttonVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        phoneNumber = findViewById(R.id.outlinedTextPhone);
        textView = findViewById(R.id.textVerify);
        code = findViewById(R.id.veryCode);
        buttonVerify = findViewById(R.id.buttonVerify);
       // password = findViewById(R.id.outlinedTextSignPassword);
        button = findViewById(R.id.containedButton);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phoneNumber.getEditText().getText().toString();
                if (phone.isEmpty() || phone.length() < 10){
                    phoneNumber.setError("Valid Number is required");
                    phoneNumber.requestFocus();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    sendVerificationCode(phone);
                }

            }
        });

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(code.getEditText().getText().toString())){
                    Toast.makeText(SignUp.this, "Wrong OTP Entered", Toast.LENGTH_SHORT).show();
                }
                verifyCode(code.getEditText().getText().toString());
            }
        });

    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+254" +phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
         final String code = credential.getSmsCode();
         if (code != null){
             verifyCode(code);
         }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            verificationID = s;
            Toast.makeText(SignUp.this, "Code Sent", Toast.LENGTH_SHORT).show();
            buttonVerify.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);

        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,code);
        signInbyCredentials(credential);

    }

    private void signInbyCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SignUp.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUp.this, Welcome.class));
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cuFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (cuFirebaseUser != null){
            startActivity(new Intent(SignUp.this, Welcome.class));
            finish();
        }
    }
}