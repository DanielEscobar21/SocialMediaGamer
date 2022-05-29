package com.example.socialmediagamer.providers;

import com.example.socialmediagamer.models.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

public class TokenProvider {
    CollectionReference mCollection;

    public TokenProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Tokens");
    }
}
