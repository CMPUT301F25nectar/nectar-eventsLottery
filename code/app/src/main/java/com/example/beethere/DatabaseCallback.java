package com.example.beethere;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public interface DatabaseCallback<T> {
    void onCallback(T result);
    void onError(Exception e);

}
