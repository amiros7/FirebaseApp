package com.example.firebaseapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebase.FirebaseUtils;
import com.example.firebase.OnAuthListener;
import com.example.firebase.OnDataBaseChangeListener;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView name = findViewById(R.id.name);
        ImageView image = findViewById(R.id.profile_img);
        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(v -> delete());
        Button logOut = findViewById(R.id.log_out);
        logOut.setOnClickListener(v -> logOut());

        FirebaseUtils.getObject(User.class, "users", FirebaseUtils.getAuthUser().getEmail(), new OnDataBaseChangeListener<User>() {
            @Override
            public void onSuccess(User object) {
                name.setText(object.getName());
                Picasso.get().load(object.getImageUrl())
                        .centerCrop()
                        .fit()
                        .into(image);

            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    private void logOut() {
        FirebaseUtils.logOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private void delete() {
        FirebaseUtils.deleteItem("users", FirebaseUtils.getAuthUser().getEmail()
                , new OnDataBaseChangeListener<Void>() {
                    @Override
                    public void onSuccess(Void object) {
                        FirebaseUtils.deleteAuthUser(new OnAuthListener<Void>() {
                            @Override
                            public void onSuccess(Void object) {
                                logOut();
                            }

                            @Override
                            public void onError(String errorMessage) {

                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        new AlertDialog.Builder(getApplicationContext())
                                .setTitle(R.string.error)
                                .setMessage(errorMessage)
                                .show();
                    }
                });
    }
}