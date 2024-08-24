package com.example.firebase;

import com.google.firebase.auth.FirebaseUser;

public interface OnAuthListener<T> {
    void onSuccess(T object);

    void onError(String errorMessage);
}
