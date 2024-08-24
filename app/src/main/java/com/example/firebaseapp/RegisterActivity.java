package com.example.firebaseapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;


import com.example.firebase.FirebaseUtils;
import com.example.firebase.OnAuthListener;
import com.example.firebase.OnDataBaseChangeListener;
import com.example.firebase.OnUploadFileListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputEditText etFullname, etPhonenumber, regEmail, regPassword;
    Button btnRegister;
    Toolbar toolbar;
    AppCompatImageView galleryImg, profileImg;
    Uri imageUri;
    private ProgressDialog progressBar;
    private String imageUrl;


    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    imageUri = result.getData().getData();
                    profileImg.setImageURI(imageUri);
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle(R.string.create_a_new_account);
        progressBar = new ProgressDialog(this);
        progressBar.setMessage(getString(R.string.please_wait));
        etFullname = findViewById(R.id.etFullname);
        etPhonenumber = findViewById(R.id.etPhonenumber);
        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        profileImg = findViewById(R.id.profile);
        galleryImg = findViewById(R.id.gallery);
        galleryImg.setOnClickListener(this);

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        galleryLauncher.launch(intent);
    }


    @Override
    public void onClick(View v) {
        if (v == btnRegister) {
            register();
        } else if (v == galleryImg) {
            openGallery();
        }
    }

    private void register() {
        progressBar.show();
        FirebaseUtils.signUpWithEmailAndPassword(regEmail.getText().toString(), regPassword.getText().toString(), new OnAuthListener<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser user) {
                uploadImage();
            }

            @Override
            public void onError(String errorMessage) {
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle(R.string.error)
                        .setMessage(errorMessage)
                        .show();
            }
        });

    }


    private void saveUserToDataBase() {
        User user = new User(regEmail.getText().toString(), etFullname.getText().toString(), etPhonenumber.getText().toString(), imageUrl);
        FirebaseUtils.saveObject("users", user.getEmail(), user, new OnDataBaseChangeListener<User>() {
            @Override
            public void onSuccess(User object) {
                Toast.makeText(RegisterActivity.this, R.string.succees_register, Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                showError(errorMessage);
            }
        });

    }

    private void uploadImage() {
        if (imageUri == null) { // it is update mode
            saveUserToDataBase();
        } else {
            FirebaseUtils.uploadFile(imageUri, "images/", UUID.randomUUID().toString(), new OnUploadFileListener() {
                @Override
                public void onSuccess(String url) {
                    RegisterActivity.this.imageUrl = url;
                    saveUserToDataBase();
                }

                @Override
                public void onError(String errorMessage) {
                    showError(errorMessage);
                }
            });
        }
    }

    private void showError(String errorMessage) {
        new AlertDialog.Builder(getApplicationContext())
                .setTitle(R.string.error)
                .setMessage(errorMessage)
                .show();
    }
}
