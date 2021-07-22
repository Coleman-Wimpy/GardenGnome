package com.example.gardengnome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPassButton;
    private ProgressBar progressBar;
    private TextView loginText;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = (EditText) findViewById(R.id.forgotPassEmail);
        resetPassButton = (Button) findViewById(R.id.resetPasswordButton);
        progressBar = (ProgressBar) findViewById(R.id.resetProgressBar);
        loginText = (TextView) findViewById(R.id.loginText);

        auth = FirebaseAuth.getInstance();

        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPassword.this, MainActivity.class));
            }
        });
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if(email.isEmpty()) {
            emailEditText.setError("Please enter an email.");
            emailEditText.requestFocus();
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email.");
            emailEditText.requestFocus();
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    Toast.makeText(ForgotPassword.this, "Check email to reset password.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }else {
                    Toast.makeText(ForgotPassword.this, "Error, Try again!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}