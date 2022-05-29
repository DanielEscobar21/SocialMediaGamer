package com.example.socialmediagamer.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediagamer.R;
import com.example.socialmediagamer.models.Post;
import com.example.socialmediagamer.providers.AuthProvider;
import com.example.socialmediagamer.providers.ImageProvider;
import com.example.socialmediagamer.providers.PostProvider;
import com.example.socialmediagamer.utils.FileUtil;
import com.example.socialmediagamer.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    ImageView mImageViewPost1;
    ImageView mImageViewPost2;
    File mImageFile;
    File mImageFile2;
    Button mButtonPost;
    ImageProvider mImageProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;
    TextInputEditText mTextinputTitle;
    TextInputEditText mTextinputDescription;
    ImageView mImageViewPC;
    ImageView mImageViewPS;
    ImageView mImageViewXBOX;
    ImageView mImageViewNintendo;
    CircleImageView mCircleImageBack;
    TextView mTextViewCategory;
    private final int GALLERY_REQUEST_CODE=1;
    private final int GALLERY_REQUEST_CODE_2=2;
    String mCategory = "";
    String mTitle = "";
    String mDescription = "";
    AlertDialog mDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();

        mDialog = mDialog = new SpotsDialog(PostActivity.this, "Espere un momento");

        mImageViewPost1 = findViewById(R.id.imageViewPost1);
        mImageViewPost2 = findViewById(R.id.imageViewPost2);

        mButtonPost = findViewById(R.id.btnPost);

        mTextinputTitle = findViewById(R.id.textInputVideoGame);
        mTextinputDescription = findViewById(R.id.textInputDescription);
        mImageViewPC = findViewById(R.id.imageViewPc);
        mImageViewPS = findViewById(R.id.imageViewPS);
        mImageViewXBOX =findViewById(R.id.imageViewXbox);
        mImageViewNintendo = findViewById(R.id.imageViewNintendo);
        mTextViewCategory = findViewById(R.id.textViewCategory);
        mCircleImageBack = findViewById(R.id.circleImageBack);

        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPost();
            }
        });


        mImageViewPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        mImageViewPost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery2();
            }
        });

        mImageViewPC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "PC";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "PS";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewXBOX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "XBOX";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewNintendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "NINTENDO";
                mTextViewCategory.setText(mCategory);
            }
        });

    }

    private void clickPost(){
        mTitle = mTextinputTitle.getText().toString();
        mDescription = mTextinputDescription.getText().toString();
        if(!mTitle.isEmpty() && !mDescription.isEmpty() && !mCategory.isEmpty()){
            if(mImageFile != null){
                saveImage();
            }
            else{
                Toast.makeText(this, "Debe de seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Completa los campos para publicar", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveImage(){
        mDialog.show();
        mImageProvider.save(PostActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot>task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url  = uri.toString();

                            mImageProvider.save(PostActivity.this, mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()){
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String url2 = uri2.toString();
                                                Post post = new Post();
                                                post.setImage1(url);
                                                post.setImage2(url2);
                                                post.setTitle(mTitle);
                                                post.setDescription(mDescription);
                                                post.setCategory(mCategory);
                                                post.setIdUser(mAuthProvider.getUid());
                                                post.setTimestamp(new Date().getTime());
                                                mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                                        mDialog.dismiss();
                                                        if(taskSave.isSuccessful()){
                                                            clearForm();
                                                            Toast.makeText(PostActivity.this, "La infromación se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(PostActivity.this, "No se pudo almacenar la información", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else{
                                        mDialog.dismiss();
                                        Toast.makeText(PostActivity.this, "La imagen 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });

                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void clearForm(){
        mTextinputTitle.setText("");
        mTextinputDescription.setText("");
        mTextViewCategory.setText("");
        mImageViewPost1.setImageResource(R.drawable.upload_image);
        mImageViewPost2.setImageResource(R.drawable.upload_image);
        mTitle ="";
        mDescription = "";
        mCategory = "";
        mImageFile = null;
        mImageFile2=null;

    }

    public void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        someActivityResultLauncher.launch(galleryIntent);
    }

    public void openGallery2(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        someActivityResultLauncher2.launch(galleryIntent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        try{
                            mImageFile = FileUtil.from(PostActivity.this,data.getData());
                            mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
                        }catch (Exception e){
                            Log.d("ERROR","Se produjo un error" + e.getMessage());
                            Toast.makeText(PostActivity.this, "Se produjo un erro" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });

    ActivityResultLauncher<Intent> someActivityResultLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data2 = result.getData();
                        try{
                            mImageFile2 = FileUtil.from(PostActivity.this,data2.getData());
                            mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
                        }catch (Exception e){
                            Log.d("ERROR","Se produjo un error" + e.getMessage());
                            Toast.makeText(PostActivity.this, "Se produjo un erro" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, PostActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostActivity.this);
    }
}

