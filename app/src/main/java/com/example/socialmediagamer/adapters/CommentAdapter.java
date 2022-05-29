package com.example.socialmediagamer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediagamer.R;
import com.example.socialmediagamer.activities.PostDetailActivity;
import com.example.socialmediagamer.models.Comment;
import com.example.socialmediagamer.models.Post;
import com.example.socialmediagamer.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;

    public CommentAdapter(FirestoreRecyclerOptions<Comment> options, Context context){
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comment comment) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String commentId = document.getId();
        String idUser = document.getString("idUser");

        holder.textViewComment.setText(comment.getComment());

        getUserInfo(idUser, holder);
    }
    private void getUserInfo(String idUser, ViewHolder holder){
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.textViewUserName.setText(username);
                    }
                    if(documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if(imageProfile != null){
                            if(!imageProfile.isEmpty()){
                                Picasso.with(context).load(imageProfile).into(holder.imageViewComment);
                            }
                        }
                    }
                }
            }
        });
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewUserName;
        TextView textViewComment;
        ImageView imageViewComment;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewUserName = view.findViewById(R.id.textViewUsername);
            textViewComment = view.findViewById(R.id.textViewComment);
            imageViewComment = view.findViewById(R.id.circleImageComment);
            viewHolder=view;
        }
    }
}
