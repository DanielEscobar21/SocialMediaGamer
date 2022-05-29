package com.example.socialmediagamer.providers;

import com.example.socialmediagamer.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProvider {
    
    private CollectionReference mCollection;
    
    public  UserProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    public Task<DocumentSnapshot> getUser(String id){
       return mCollection.document(id).get();
    }

    public DocumentReference getUserRealTime(String id){
        return mCollection.document(id);
    }


    public Task<Void> create(User user){
       return mCollection.document(user.getId()).set(user);
    }

    public Task<Void> updateOnline(String idUser, boolean status){
        Map<String, Object> map = new HashMap<>();
        map.put("online", status);
        map.put("lastConnect", new Date().getTime());
        return mCollection.document(idUser).update(map);

    }


}
