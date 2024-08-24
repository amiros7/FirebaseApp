package com.example.firebase;

import android.app.Activity;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FirebaseUtils {

    public static void signUpWithEmailAndPassword(String email, String password, OnAuthListener<FirebaseUser> onCompleteListener) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> onCompleteListener.onSuccess(authResult.getUser()))
                .addOnFailureListener(e -> onCompleteListener.onError(e.getMessage()));
    }

    public static void signUpWithPhoneNumber(Activity activity, String phone, long timeoutSecondsSendSms, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout for the code sent via SMS
                .setActivity(activity) // The activity to which the user is navigated to enter the code
                .setCallbacks(callbacks) // Implement PhoneAuthProvider.OnVerificationStateChangedCallbacks
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public static void signInWithSmsCode(String verificationId, String smsCode,  OnCompleteListener<AuthResult> onCompleteListener) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, smsCode);

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(onCompleteListener);
    }

    public static void deleteAuthUser(  OnAuthListener<Void> onCompleteListener) {
        FirebaseAuth.getInstance()
                .getCurrentUser().delete().addOnSuccessListener(onCompleteListener::onSuccess)
                .addOnFailureListener(e -> onCompleteListener.onError(e.getMessage()));
    }

    public static void logOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public static void signInWithEmailAndPassword(@NonNull String email, @NonNull String password, OnAuthListener<FirebaseUser> onCompleteListener) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> onCompleteListener.onSuccess(authResult.getUser()))
                .addOnFailureListener(e -> onCompleteListener.onError(e.getMessage()));
    }

    public static void sendPasswordResetEmail(@NonNull String email, OnAuthListener<Void> onCompleteListener) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(onCompleteListener::onSuccess)
                .addOnFailureListener(e -> onCompleteListener.onError(e.getMessage()));
    }

    public static FirebaseUser getAuthUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static <T> void saveObject(String collectionName, String id, T object, OnDataBaseChangeListener<T> onCompleteListener) {
        FirebaseFirestore.getInstance().collection(collectionName)
                .document(id).set(object)
                .addOnSuccessListener(unused -> onCompleteListener.onSuccess(object))
                .addOnFailureListener(e -> {
                    onCompleteListener.onError(e.getMessage());
                });
    }

    public static <T> void deleteItem(String collectionName, String docId, OnDataBaseChangeListener<Void> onCompleteListener) {
        FirebaseFirestore.getInstance().collection(collectionName).document(docId).delete()
                .addOnSuccessListener(onCompleteListener::onSuccess)
                .addOnFailureListener(e -> onCompleteListener.onError(e.getMessage()));
    }

    public static <T> void getObject(Class<T> clazz, String collectionName, String id, OnDataBaseChangeListener<T> onDone) {
        FirebaseFirestore.getInstance().collection(collectionName).document(id).get()
                .addOnSuccessListener(documentSnapshot -> onDone.onSuccess(documentSnapshot.toObject(clazz)))
                .addOnFailureListener(e -> onDone.onError(e.getMessage()));
    }

    public static void updateDocument(String collectionName, String documentId, Map<String, Object> data, OnCompleteListener<Void> onCompleteListener){
        FirebaseFirestore.getInstance()
                .collection(collectionName)
                .document(documentId)
                .update(data)
                .addOnCompleteListener(onCompleteListener);
    }


    public static <T> void getAllCollectionObjects(String collectionName, Class<T> clazz, OnDataBaseChangeListener<List<T>> onDone) {
        List<T> items = new ArrayList<>();
        FirebaseFirestore.getInstance()
                .collection(collectionName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    queryDocumentSnapshots.forEach(it->{
                        items.add(it.toObject(clazz));
                    });
                    onDone.onSuccess(items);
                })
                .addOnFailureListener(e -> onDone.onError(e.getMessage()));
    }

    public static void uploadFile(Uri fileUri, String  path, String fileId, OnUploadFileListener onDone) {
        if (fileUri != null) {
            String fileExtension = getFileExtension(fileUri);

            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(path + "/" + fileId + "." + fileExtension);

            UploadTask uploadTask = fileRef.putFile(fileUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    onDone.onSuccess(downloadUrl);
                });
            }).addOnFailureListener(exception -> {
                onDone.onError(exception.getMessage());
            });
        } else {
            onDone.onError("No file selected");
        }
    }

    public void deleteFile(String storagePath, OnCompleteListener<Void> onCompleteListener) {
        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(storagePath);
        fileRef.delete().addOnCompleteListener(onCompleteListener);
    }

    private static String getFileExtension(Uri uri) {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = mime.getExtensionFromMimeType(uri.getScheme());
        return extension != null ? extension : "";
    }

    public static <T> void listenToDocumentChanges(String collectionName, String documentId, Class<T> clazz, OnDataBaseChangeListener<T> onDone) {
        FirebaseFirestore.getInstance()
                .collection(collectionName)
                .document(documentId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // An error occurred
                        onDone.onError(error.getMessage());
                        return;
                    }

                    if (value != null && value.exists()) {
                        // Document has changed
                        onDone.onSuccess(value.toObject(clazz));
                    } else {
                        // Document does not exist
                        onDone.onError(null);
                    }
                });
    }
}
