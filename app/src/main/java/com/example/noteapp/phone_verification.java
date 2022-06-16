package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class phone_verification extends AppCompatActivity {
    TextView textView;
    TextInputLayout code;
    Button button;
    ProgressBar bar;
    //FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verfication);

        textView = findViewById(R.id.textVerify);
        code = findViewById(R.id.outlinedCode);
        button = findViewById(R.id.buttonVerify);
        bar = findViewById(R.id.progressBar);
    }
}