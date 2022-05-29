package com.example.socialmediagamer.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.socialmediagamer.R;
import com.example.socialmediagamer.adapters.MyPostsAdapter;
import com.example.socialmediagamer.adapters.PostAdapter;
import com.example.socialmediagamer.models.Post;
import com.example.socialmediagamer.providers.AuthProvider;
import com.example.socialmediagamer.providers.PostProvider;
import com.example.socialmediagamer.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    LinearLayout mLinearLayoutEditProfile;
    View mView;
    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewEmail;
    TextView mTextViewPostNumber;
    ImageView mImageViewCover;
    CircleImageView mImageViewProfile;
    RecyclerView mRecyclerView;
    TextView mTextViewPostExist;

    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    MyPostsAdapter mAdapter;
    ListenerRegistration mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
        mTextViewEmail = mView.findViewById(R.id.textViewEmail);
        mTextViewPostNumber = mView.findViewById(R.id.textViewPostNumber);
        mImageViewCover = mView.findViewById(R.id.imageViewCover);
        mImageViewProfile = mView.findViewById(R.id.circleImageProfile);
        mTextViewPhone = mView.findViewById(R.id.textViewphone);
        mTextViewUsername = mView.findViewById(R.id.textViewUsername);
        mRecyclerView = mView.findViewById(R.id.recyclerViewMyPost);
        mTextViewPostExist = mView.findViewById(R.id.textViewPostExist);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        getUser();
        getPostNumber();
        checkIfExistPost();
        return mView;
    }
    private void checkIfExistPost() {
        mListener = mPostProvider.getPostByUser(mAuthProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    int numberPost = queryDocumentSnapshots.size();
                    if (numberPost > 0) {
                        mTextViewPostExist.setText("Publicaciones");
                        mTextViewPostExist.setTextColor(Color.BLACK);
                    }
                    else {
                        mTextViewPostExist.setText("No hay publicaciones");
                        mTextViewPostExist.setTextColor(Color.GRAY);
                    }
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByUser(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        mAdapter = new MyPostsAdapter(options, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mListener!=null){
            mListener.remove();
        }
    }

    private void getPostNumber() {
        mPostProvider.getPostByUser(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberPost = queryDocumentSnapshots.size();
                mTextViewPostNumber.setText(String.valueOf(numberPost));
            }
        });
    }

    private void getUser(){
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 if(documentSnapshot.contains("email")){
                    String email = documentSnapshot.getString("email");
                    mTextViewEmail.setText(email);

                 }
                if(documentSnapshot.contains("phone")){
                    String phone = documentSnapshot.getString("phone");
                    mTextViewPhone.setText(phone);

                }
                if(documentSnapshot.contains("username")){
                    String username = documentSnapshot.getString("username");
                    mTextViewUsername.setText(username);

                }
                if(documentSnapshot.contains("email")){
                    String email = documentSnapshot.getString("email");
                    mTextViewEmail.setText(email);

                }
                if(documentSnapshot.contains("image_profile")){
                    String imageProfile = documentSnapshot.getString("image_profile");
                    if(imageProfile !=null){
                        if(!imageProfile.isEmpty()){
                            Picasso.with(getContext()).load(imageProfile).into(mImageViewProfile);
                        }
                    }
                }

                if(documentSnapshot.contains("image_cover")){
                    String imageCover = documentSnapshot.getString("image_cover");
                    if(imageCover !=null){
                        if(!imageCover.isEmpty()){
                            Picasso.with(getContext()).load(imageCover).into(mImageViewCover);
                        }
                    }
                }

            }
        });
    }
}