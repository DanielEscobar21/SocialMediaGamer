package com.example.socialmediagamer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediagamer.R;
import com.example.socialmediagamer.activities.PostDetailActivity;
import com.example.socialmediagamer.models.Like;
import com.example.socialmediagamer.models.Post;
import com.example.socialmediagamer.providers.AuthProvider;
import com.example.socialmediagamer.providers.LikesProvider;
import com.example.socialmediagamer.providers.PostProvider;
import com.example.socialmediagamer.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Locale;

public class PostAdapter extends FirestoreRecyclerAdapter<Post, PostAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    ListenerRegistration mListener;

    public PostAdapter(FirestoreRecyclerOptions<Post> options, Context context){
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mLikesProvider = new LikesProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();
        String userId = document.getString("idUser");

        holder.textViewTitle.setText(post.getTitle().toUpperCase());
        holder.textViewDescription.setText(post.getDescription());
        if(post.getImage1()!=null){
            if(!post.getImage1().isEmpty()){
                Picasso.with(context).load(post.getImage1()).into(holder.imageViewPost);
            }
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("id",postId);
                context.startActivity(intent);
            }
        });

        holder.imageViewLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Like like = new Like();
                like.setIdUser(mAuthProvider.getUid());
                like.setIdPost(postId);
                like.setTimestamp(new Date().getTime());
                like(like, holder);
            }
        });
        getUserInfo(post.getIdUser(),holder);
        getNumberLikesByPost(postId, holder);
        checkIfExistLike(postId, mAuthProvider.getUid(), holder);
    }

    private void getNumberLikesByPost(String idPost, final ViewHolder holder) {
        mListener = mLikesProvider.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    int numberLikes = queryDocumentSnapshots.size();
                    holder.textViewLikes.setText(String.valueOf(numberLikes) + " Me gustas");
                }
            }
        });
    }

    private void like(final Like like, final ViewHolder holder) {
        mLikesProvider.getLikeByPostAndUser(like.getIdPost(), mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0) {
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imageViewLikes.setImageResource(R.drawable.ic_thumb_up_off);
                    mLikesProvider.delete(idLike);
                }
                else {
                    holder.imageViewLikes.setImageResource(R.drawable.ic_thumb_up_on);
                    mLikesProvider.create(like);
                }
            }
        });

    }

    private void checkIfExistLike(String idPost, String idUser, final ViewHolder holder) {
        mLikesProvider.getLikeByPostAndUser(idPost, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0) {
                    holder.imageViewLikes.setImageResource(R.drawable.ic_thumb_up_on);
                }
                else {
                    holder.imageViewLikes.setImageResource(R.drawable.ic_thumb_up_off);
                }
            }
        });

    }

    private void getUserInfo(String idUser, final ViewHolder holder) {
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.textViewUserName.setText("By: "+ username);
                    }
                }
            }
        });
    }

    public ListenerRegistration getListener(){
        return mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewLikes;
        ImageView imageViewPost;
        ImageView imageViewLikes;
        TextView textViewUserName;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewDescription = view.findViewById(R.id.textViewDescriptionPostCard);
            textViewUserName = view.findViewById(R.id.textViewUsernamePostCard);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            textViewLikes = view.findViewById(R.id.textViewLikes);
            imageViewLikes = view.findViewById(R.id.imageViewLike);
            viewHolder=view;
        }
    }
}
