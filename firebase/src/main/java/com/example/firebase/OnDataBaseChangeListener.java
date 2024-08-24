package com.example.firebase;

public interface OnDataBaseChangeListener<T> {
    void onSuccess(T object);

    void onError(String errorMessage);
}
