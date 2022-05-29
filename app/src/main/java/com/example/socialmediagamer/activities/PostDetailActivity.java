package com.example.socialmediagamer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.socialmediagamer.R;
import com.example.socialmediagamer.adapters.CommentAdapter;
import com.example.socialmediagamer.adapters.PostAdapter;
import com.example.socialmediagamer.adapters.SliderAdapter;
import com.example.socialmediagamer.models.Comment;
import com.example.socialmediagamer.models.Post;
import com.example.socialmediagamer.models.SliderItem;
import com.example.socialmediagamer.providers.AuthProvider;
import com.example.socialmediagamer.providers.CommentsProvider;
import com.example.socialmediagamer.providers.LikesProvider;
import com.example.socialmediagamer.providers.PostProvider;
import com.example.socialmediagamer.providers.UserProvider;
import com.example.socialmediagamer.utils.RelativeTime;
import com.example.socialmediagamer.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {
    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();

    UserProvider mUserProvider;
    PostProvider mPostProvider;
    CommentsProvider mCommentsProvider;
    AuthProvider mAuthProvider;
    LikesProvider mLikesProvider;

    CommentAdapter mAdapter;
    String mExtraPostId;

    TextView mTextViewTitle;
    TextView mTextViewDescription;
    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewNameCategory;
    TextView mTextViewRelativeTime;
    TextView mTextViewLikes;
    ImageView mImageViewCategory;
    CircleImageView mCircleImageViewProfile;
    Button mButtonShowProfile;
    FloatingActionButton mFabComment;
    RecyclerView mRecyclerView;
    Toolbar mToolbar;

    ListenerRegistration mListener;

    String mIdUser="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mSliderView=findViewById(R.id.imageSlider);
        mTextViewTitle = findViewById(R.id.textViewTitle);
        mTextViewDescription = findViewById(R.id.textViewDescription);
        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewNameCategory = findViewById(R.id.textViewNameCategory);
        mTextViewRelativeTime = findViewById(R.id.textViewRelativeTime);
        mTextViewLikes = findViewById(R.id.textViewLikes);
        mImageViewCategory = findViewById(R.id.imageViewCategory);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mButtonShowProfile = findViewById(R.id.btnShowProfile);
        mFabComment = findViewById(R.id.fabComment);
        mRecyclerView = findViewById(R.id.recyclerViewComments);
        mToolbar = findViewById(R.id.toolbarPost);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);



        mPostProvider = new PostProvider();
        mUserProvider = new UserProvider();
        mCommentsProvider = new CommentsProvider();
        mAuthProvider = new AuthProvider();
        mLikesProvider = new LikesProvider();

        mExtraPostId = getIntent().getStringExtra("id");

        mButtonShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShowProfile();
            }
        });

        getPost();
        getNumberLikes();

        mFabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComment();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = mCommentsProvider.getCommentsByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment.class).build();
        mAdapter = new CommentAdapter(options, PostDetailActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, PostDetailActivity.this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostDetailActivity.this);
    }

    private void showDialogComment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);
        alert.setTitle("Nuevo Comentario");
        alert.setMessage("Ingresa un comentario.");

        EditText editText = new EditText(PostDetailActivity.this);
        editText.setHint("Ingresa aquí el comentario que quisieras compartir.");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(36,0,36,36);
        editText.setLayoutParams(params);
        RelativeLayout container = new RelativeLayout(PostDetailActivity.this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        container.setLayoutParams(relativeParams);
        container.addView(editText);

        alert.setView(container);
        alert.setPositiveButton("Publicar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString();
                if(!value.isEmpty()){
                    createComment(value);
                }else{
                    Toast.makeText(PostDetailActivity.this, "Debe ingresar el comentario.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void createComment(String value) {
        Comment comment = new Comment();
        comment.setComment(value);
        comment.setIdPost(mExtraPostId);
        comment.setIdUser(mAuthProvider.getUid());
        comment.setTimestamp(new Date().getTime());
        mCommentsProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PostDetailActivity.this, "Se ha publicado el comentario.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PostDetailActivity.this, "Error al publicar el comentario.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void instanceSlider(){
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this,mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }

    private void getPost(){
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("image1")){
                        String image1 = documentSnapshot.getString("image1");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image1);
                        mSliderItems.add(item);
                    }
                    if(documentSnapshot.contains("image2")){
                        String image2 = documentSnapshot.getString("image2");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image2);
                        mSliderItems.add(item);
                    }
                    if (documentSnapshot.contains("title")) {
                        String title = documentSnapshot.getString("title");
                        mTextViewTitle.setText(title.toUpperCase());
                    }
                    if (documentSnapshot.contains("description")) {
                        String description = documentSnapshot.getString("description");
                        mTextViewDescription.setText(description);
                    }
                    if (documentSnapshot.contains("category")) {
                        String category = documentSnapshot.getString("category");
                        mTextViewNameCategory.setText(category);

                        if (category.equals("PS4")) {
                            mImageViewCategory.setImageResource(R.drawable.icon_ps4);
                        }
                        else if (category.equals("XBOX")) {
                            mImageViewCategory.setImageResource(R.drawable.icon_xbox);
                        }
                        else if (category.equals("PC")) {
                            mImageViewCategory.setImageResource(R.drawable.icon_pc);
                        }
                        else if (category.equals("NINTENDO")) {
                            mImageViewCategory.setImageResource(R.drawable.icon_nintendo);
                        }
                    }
                    if (documentSnapshot.contains("idUser")) {
                        mIdUser = documentSnapshot.getString("idUser");
                        getUserInfo(mIdUser);
                    }
                    if (documentSnapshot.contains("timestamp")) {
                        long timestamp = documentSnapshot.getLong("timestamp");
                        String relativeTime = RelativeTime.getTimeAgo(timestamp, PostDetailActivity.this);
                        mTextViewRelativeTime.setText(relativeTime);
                    }
                    instanceSlider();
                }
            }
        });
    }

    private void goToShowProfile() {
        if (!mIdUser.equals("")) {
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", mIdUser);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "El id del usuario aun no se carga", Toast.LENGTH_SHORT).show();
        }
    }

    private void getNumberLikes() {
        mListener = mLikesProvider.getLikesByPost(mExtraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    int numberLikes = queryDocumentSnapshots.size();
                    if (numberLikes == 1) {
                        mTextViewLikes.setText(numberLikes + " Me gusta");
                    }
                    else {
                        mTextViewLikes.setText(numberLikes + " Me gustas");
                    }
                }

            }
        });
    }

    private void getUserInfo(String idUser) {
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if (documentSnapshot.contains("phone")) {
                        String phone = documentSnapshot.getString("phone");
                        mTextViewPhone.setText(phone);
                    }
                    if (documentSnapshot.contains("image_profile")) {
                        String imageProfile = documentSnapshot.getString("image_profile");
                        Picasso.with(PostDetailActivity.this).load(imageProfile).into(mCircleImageViewProfile);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener!=null){
            mListener.remove();
        }
    }
}