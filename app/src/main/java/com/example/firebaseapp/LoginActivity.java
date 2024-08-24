package com.example.firebaseapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebase.FirebaseUtils;
import com.example.firebase.OnAuthListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        TextInputEditText email = findViewById(R.id.email);
        TextInputEditText password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        login.setOnClickListener(v -> login(email.getText().toString(), password.getText().toString()));
        TextView signUp = findViewById(R.id.sign_up);
        signUp.setOnClickListener(v -> signUp());
        TextView forgetPassword = findViewById(R.id.forgetPassword);
        forgetPassword.setOnClickListener(v -> forgetPassword(email));


    }

    private void forgetPassword(TextInputEditText emailET) {
        if (emailET.getText().toString().isEmpty()) {
            emailET.setError(getString(R.string.no_email));
        } else {
            FirebaseUtils.sendPasswordResetEmail(emailET.getText().toString(), new OnAuthListener<Void>() {
                @Override
                public void onSuccess(Void object) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage(getString(R.string.forget_password))
                            .setTitle(R.string.alert)
                            .show();
                }

                @Override
                public void onError(String errorMessage) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage(errorMessage)
                            .setTitle(R.string.error)
                            .show();
                }
            });
        }

    }

    private void signUp() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void login(String email, String password) {
        progressDialog.show();
        FirebaseUtils.signInWithEmailAndPassword(email, password, new OnAuthListener<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser user) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, getString(R.string.success_sign_in), Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                new AlertDialog.Builder(LoginActivity.this)
                        .setMessage(errorMessage)
                        .setTitle(R.string.error)
                        .show();
            }
        });
    }
}