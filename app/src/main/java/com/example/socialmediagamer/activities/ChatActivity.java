package com.example.socialmediagamer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediagamer.R;
import com.example.socialmediagamer.adapters.MessagesAdapter;
import com.example.socialmediagamer.adapters.PostAdapter;
import com.example.socialmediagamer.models.Chat;
import com.example.socialmediagamer.models.Message;
import com.example.socialmediagamer.models.Post;
import com.example.socialmediagamer.providers.AuthProvider;
import com.example.socialmediagamer.providers.ChatsProvider;
import com.example.socialmediagamer.providers.MessagesProvider;
import com.example.socialmediagamer.providers.UserProvider;
import com.example.socialmediagamer.utils.RelativeTime;
import com.example.socialmediagamer.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String mExtraIdUser1;
    String mExtraIdUser2;
    String mExtraIdChat;

    ChatsProvider mChatsProvider;
    MessagesProvider mMessagesProvider;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;

    View mActionBarView;

    EditText mEditTextMessage;
    ImageView mImageViewSendMessage;

    CircleImageView mCircleImageViewProfile;
    TextView mTextViewUsername;
    TextView mTextViewRelativeTime;
    ImageView mImageViewBack;

    ListenerRegistration mListener;
    LinearLayoutManager mLinearLayoutManager;

    RecyclerView mRecyclerViewMessage;
    MessagesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        mChatsProvider = new ChatsProvider();
        mMessagesProvider = new MessagesProvider();
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();



        mEditTextMessage = findViewById(R.id.editTextMessage);
        mImageViewSendMessage = findViewById(R.id.imageViewSendMessage);
        mRecyclerViewMessage = findViewById(R.id.recyclerViewMessage);

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMessage.setLayoutManager(mLinearLayoutManager);

        mExtraIdUser1=getIntent().getStringExtra("idUser1");
        mExtraIdUser2=getIntent().getStringExtra("idUser2");
        mExtraIdChat = getIntent().getStringExtra("idChat");

        mImageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        showCustomToolbar(R.layout.custom_chat_toolbar);

        checkIfChatExist();
    }
    public void onStart() {
        super.onStart();
        if(mAdapter != null){
            mAdapter.startListening();
        }
        ViewedMessageHelper.updateOnline(true, ChatActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, ChatActivity.this);
    }

    private void getMessageChat(){
        Query query = mMessagesProvider.getMessageByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();
        mAdapter = new MessagesAdapter(options, ChatActivity.this);
        mRecyclerViewMessage.setAdapter(mAdapter);
        mAdapter.startListening();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViewed();
                int numberMessage = mAdapter.getItemCount();
                int lastMessagePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(lastMessagePosition == -1 || (positionStart >= (numberMessage-1) && lastMessagePosition == (positionStart-1))){
                    mRecyclerViewMessage.scrollToPosition(positionStart);
                }
            }
        });
    }

    private void sendMessage() {
        String textMessage = mEditTextMessage.getText().toString();
        if (!textMessage.isEmpty()) {
            final Message message = new Message();
            message.setIdChat(mExtraIdChat);
            if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
                message.setIdSender(mExtraIdUser1);
                message.setIdReceiver(mExtraIdUser2);
            }
            else {
                message.setIdSender(mExtraIdUser2);
                message.setIdReceiver(mExtraIdUser1);
            }
            message.setTimestamp(new Date().getTime());
            message.setViewed(false);
            message.setIdChat(mExtraIdChat);
            message.setMessage(textMessage);

            mMessagesProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mEditTextMessage.setText("");
                        mAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(ChatActivity.this, "Error al crear el mensaje", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resource, null);
        actionBar.setCustomView(mActionBarView);
        mCircleImageViewProfile = mActionBarView.findViewById(R.id.circleImageProfile);
        mTextViewUsername = mActionBarView.findViewById(R.id.textViewUsername);
        mTextViewRelativeTime = mActionBarView.findViewById(R.id.textViewRelativeTime);
        mImageViewBack = mActionBarView.findViewById(R.id.imageViewBack);

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUserInfo();
    }

    private void getUserInfo() {
        String idUserInfo = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
            idUserInfo = mExtraIdUser2;
        }
        else {
            idUserInfo = mExtraIdUser1;
        }
        mListener = mUserProvider.getUserRealTime(idUserInfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        String mUsernameChat = documentSnapshot.getString("username");
                        mTextViewUsername.setText(mUsernameChat);
                    }
                    if (documentSnapshot.contains("online")) {
                        boolean online = documentSnapshot.getBoolean("online");
                        if(online){
                            mTextViewRelativeTime.setText("En Linea");
                        }else if(documentSnapshot.contains("lastConnect")){
                            Long lastConnect  = documentSnapshot.getLong("lastConnect");
                            String relativeTime = RelativeTime.getTimeAgo(lastConnect, ChatActivity.this);
                            mTextViewRelativeTime.setText(relativeTime);
                        }
                    }
                    if (documentSnapshot.contains("image_profile")) {
                        String mImageReceiver = documentSnapshot.getString("image_profile");
                        if (mImageReceiver != null) {
                            if (!mImageReceiver.equals("")) {
                                Picasso.with(ChatActivity.this).load(mImageReceiver).into(mCircleImageViewProfile);
                            }
                        }
                    }
                }
            }
        });
    }

    private void checkIfChatExist() {
        mChatsProvider.getChatByUser1AndUser2(mExtraIdUser1, mExtraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size == 0) {
                    createChat();
                }else{
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    getMessageChat();
                    updateViewed();
                }
            }
        });
    }

    private void updateViewed() {
        String idSender = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
            idSender = mExtraIdUser2;
        }
        else {
            idSender = mExtraIdUser1;
        }
        mMessagesProvider.getMessagesByChatAndSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    mMessagesProvider.updateViewed(document.getId(), true);
                }
            }
        });
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        chat.setId(mExtraIdUser1+mExtraIdUser2);
        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUser1);
        ids.add(mExtraIdUser2);
        chat.setIds(ids);
        mChatsProvider.create(chat);
        mExtraIdChat = chat.getId();
        getMessageChat();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener!=null){
            mListener.remove();
        }
    }
}