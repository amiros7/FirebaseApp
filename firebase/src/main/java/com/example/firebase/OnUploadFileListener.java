package com.example.firebase;

import com.google.firebase.auth.FirebaseUser;

public interface OnUploadFileListener {
    void onSuccess(String url);

    void onError(String errorMessage);
}
