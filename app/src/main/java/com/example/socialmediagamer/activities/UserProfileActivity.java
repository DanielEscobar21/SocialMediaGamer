package com.example.socialmediagamer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.socialmediagamer.R;
import com.example.socialmediagamer.adapters.MyPostsAdapter;
import com.example.socialmediagamer.models.Post;
import com.example.socialmediagamer.models.User;
import com.example.socialmediagamer.providers.AuthProvider;
import com.example.socialmediagamer.providers.PostProvider;
import com.example.socialmediagamer.providers.UserProvider;
import com.example.socialmediagamer.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    LinearLayout mLinearLayoutEditProfile;
    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewEmail;
    TextView mTextViewPostNumber;
    ImageView mImageViewCover;
    CircleImageView mImageViewProfile;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    RecyclerView mRecyclerView;
    TextView mTextViewPostExist;
    Toolbar mToolbar;
    FloatingActionButton mFabChat;

    String mExtraIdUser;
    MyPostsAdapter mAdapter;
    ListenerRegistration mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mTextViewEmail = findViewById(R.id.textViewEmail);
        mTextViewPostNumber = findViewById(R.id.textViewPostNumber);
        mImageViewCover = findViewById(R.id.imageViewCover);
        mImageViewProfile = findViewById(R.id.circleImageProfile);
        mTextViewPhone = findViewById(R.id.textViewphone);
        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewPostExist = findViewById(R.id.textViewPostExist);
        mRecyclerView = findViewById(R.id.recyclerViewMyPost);
        mToolbar = findViewById(R.id.toolbar);
        mFabChat = findViewById(R.id.fabChat);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider= new PostProvider();

        mExtraIdUser = getIntent().getStringExtra("idUser");

        if(mAuthProvider.getUid().equals(mExtraIdUser)){
            mFabChat.setVisibility(View.GONE);
        }
        mFabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity();
            }
        });
        getUser();
        getPostNumber();
        checkIfExistPost();
    }

    private void goToChatActivity() {
        Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
        intent.putExtra("idUser1", mAuthProvider.getUid());
        intent.putExtra("idUser2", mExtraIdUser);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByUser(mExtraIdUser);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mAdapter = new MyPostsAdapter(options, UserProfileActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, UserProfileActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, UserProfileActivity.this);
    }

    private void checkIfExistPost() {
        mListener = mPostProvider.getPostByUser(mExtraIdUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    private void getPostNumber() {
        mPostProvider.getPostByUser(mExtraIdUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberPost = queryDocumentSnapshots.size();
                mTextViewPostNumber.setText(String.valueOf(numberPost));
            }
        });
    }

    private void getUser(){
        mUserProvider.getUser(mExtraIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                            Picasso.with(UserProfileActivity.this).load(imageProfile).into(mImageViewProfile);
                        }
                    }
                }

                if(documentSnapshot.contains("image_cover")){
                    String imageCover = documentSnapshot.getString("image_cover");
                    if(imageCover !=null){
                        if(!imageCover.isEmpty()){
                            Picasso.with(UserProfileActivity.this).load(imageCover).into(mImageViewCover);
                        }
                    }
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener!=null){
            mListener.remove();
        }
    }
}