package com.example.social_network_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

  private Toolbar mToolbar;
  private ImageButton PostImage;
  private Button PostButton;
  private EditText PostText;

  private static final int Pic = 1;
  private Uri ImgUri;
  private String Des;

  private String saveRandom, downloadUrl, currentID;

  private StorageReference PostImgRef;
  private DatabaseReference UsersRef, PostsRef;
  private FirebaseAuth mAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_post);

    PostImage = findViewById(R.id.post_image);
    PostText = findViewById(R.id.post_text);
    PostButton = findViewById(R.id.post_button);

    PostImgRef = FirebaseStorage.getInstance().getReference();
    UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    mAuth = FirebaseAuth.getInstance();
    currentID = mAuth.getCurrentUser().getUid();
    PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

    mToolbar = findViewById(R.id.post_page_toolbar);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setTitle("New Post");

    PostImage.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            OpenGallery();
          }
        });

    PostButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            ValidatePost();
          }
        });
  }

  private void ValidatePost() {

    Des = PostText.getText().toString();

    if (ImgUri == null) {
      Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show();
    }
    if (TextUtils.isEmpty(Des)) {
      Toast.makeText(this, "Please provide Description", Toast.LENGTH_SHORT).show();

    } else {

      StorePostImage();
    }
  }

  private void StorePostImage() {

    Calendar stamp = Calendar.getInstance();
    SimpleDateFormat currentStamp = new SimpleDateFormat("dd-MMMM-yyyy'@'HH:mm:ss'@'SSS");
    saveRandom = currentStamp.format(stamp.getTime());

    final StorageReference filePath =
        PostImgRef.child("Post Images").child(ImgUri.getLastPathSegment() + saveRandom + ".jpg");

    filePath
        .putFile(ImgUri)
        .continueWithTask(
            new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
              @Override
              public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                  throw task.getException();
                }
                return filePath.getDownloadUrl();
              }
            })
        .addOnCompleteListener(
            new OnCompleteListener<Uri>() {
              @Override
              public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                  Uri downUri = task.getResult();
                  Toast.makeText(
                          PostActivity.this,
                          "Profile Image stored successfully to Firebase storage...",
                          Toast.LENGTH_SHORT)
                      .show();

                  downloadUrl = downUri.toString();
                  SavingPostInformationToDatabase();
                } else {
                  String message = task.getException().getMessage();
                  Toast.makeText(PostActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT)
                      .show();
                }
              }
            });
  }

  private void SavingPostInformationToDatabase() {
    UsersRef.child(currentID)
        .addValueEventListener(
            new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                  String userFullName = dataSnapshot.child("fullname").getValue().toString();
                  String userProfileImage =
                      dataSnapshot.child("profileimage").getValue().toString();
                  String[] arrOfStr = saveRandom.split("@", 0);


                  HashMap postsMap = new HashMap();
                  postsMap.put("uid", currentID);
                  postsMap.put("date", arrOfStr[0]);
                  postsMap.put("time", arrOfStr[1]);
                  postsMap.put("description", Des);
                  postsMap.put("postimage", downloadUrl);
                  postsMap.put("profileimage", userProfileImage);
                  postsMap.put("fullname", userFullName);
                  PostsRef.child(currentID + saveRandom)
                      .updateChildren(postsMap)
                      .addOnCompleteListener(
                          new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                              if (task.isSuccessful()) {
                                SendUserMain();
                                Toast.makeText(
                                        PostActivity.this,
                                        "New Post is updated successfully.",
                                        Toast.LENGTH_SHORT)
                                    .show();

                              } else {
                                Toast.makeText(
                                        PostActivity.this,
                                        "Error Occured while updating your post.",
                                        Toast.LENGTH_SHORT)
                                    .show();
                              }
                            }
                          });
                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
  }

  private void OpenGallery() {

    Intent galIntent = new Intent();
    galIntent.setAction(Intent.ACTION_GET_CONTENT);
    galIntent.setType("image/*");
    startActivityForResult(galIntent, Pic);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == Pic && resultCode == RESULT_OK && data != null) {

      ImgUri = data.getData();
      PostImage.setImageURI(ImgUri);
    }
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    int id = item.getItemId();

    if (id == android.R.id.home) {
      SendUserMain();
    }
    return super.onOptionsItemSelected(item);
  }

  private void SendUserMain() {

    Intent MainIntent = new Intent(PostActivity.this, MainActivity.class);

    startActivity(MainIntent);
  }
}
