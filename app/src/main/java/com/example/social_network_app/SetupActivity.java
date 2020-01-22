package com.example.social_network_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

  private EditText UserName, FullName, Country;
  private Button SaveInfo;
  private CircleImageView profileImage;

  private FirebaseAuth mAuth;
  private DatabaseReference UsersRef;
  private StorageReference imageRef;

  String currentUser;
  static final int Pic = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setup);

    mAuth = FirebaseAuth.getInstance();
    currentUser = mAuth.getCurrentUser().getUid();
    UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
    imageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

    UserName = (EditText) findViewById(R.id.setup_username);
    FullName = (EditText) findViewById(R.id.setup_fn);
    Country = (EditText) findViewById(R.id.setup_cou);
    SaveInfo = (Button) findViewById(R.id.setup_button);
    profileImage = (CircleImageView) findViewById(R.id.setup_img);

    SaveInfo.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            SaveAccount();
          }
        });

    profileImage.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {

            Intent galIntent = new Intent();
            galIntent.setAction(Intent.ACTION_GET_CONTENT);
            galIntent.setType("image/*");
            startActivityForResult(galIntent, Pic);
          }
        });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == Pic && resultCode == RESULT_OK && data != null) {
      Uri imageUri = data.getData();

      CropImage.activity(imageUri)
          .setGuidelines(CropImageView.Guidelines.ON)
          .setAspectRatio(1, 1)
          .start(this);
    }

    
    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);

      if (resultCode == RESULT_OK) {

        Uri resultUri = result.getUri();

        StorageReference filePath = imageRef.child(currentUser + ".jpg");

        filePath
            .putFile(resultUri)
            .addOnCompleteListener(
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                      Task<Uri> result =
                          task.getResult().getMetadata().getReference().getDownloadUrl();

                      result.addOnSuccessListener(
                          new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                              final String downloadUrl = uri.toString();

                              UsersRef.child("profileimage")
                                  .setValue(downloadUrl)
                                  .addOnCompleteListener(
                                      new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                          if (task.isSuccessful()) {
                                            Intent selfIntent =
                                                new Intent(SetupActivity.this, SetupActivity.class);
                                            startActivity(selfIntent);

                                            Toast.makeText(
                                                    SetupActivity.this,
                                                    "Image Uploaded",
                                                    Toast.LENGTH_SHORT)
                                                .show();

                                          } else {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(
                                                    SetupActivity.this,
                                                    "Error: " + message,
                                                    Toast.LENGTH_SHORT)
                                                .show();
                                          }
                                        }
                                      });
                            }
                          });
                    }
                  }
                });
      }
    }
  }

  private void SaveAccount() {

    String username = UserName.getText().toString();
    String fullname = FullName.getText().toString();
    String country = Country.getText().toString();

    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(fullname) || TextUtils.isEmpty(country)) {

      Toast.makeText(this, "Please Enter all fields", Toast.LENGTH_LONG).show();

    } else {

      HashMap userMap = new HashMap();
      userMap.put("username", username);
      userMap.put("fullname", fullname);
      userMap.put("country", country);
      userMap.put("status", "none");
      userMap.put("gender", "none");
      userMap.put("DOB", "none");
      userMap.put("relationship", "single");

      UsersRef.updateChildren(userMap)
          .addOnCompleteListener(
              new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                  if (task.isSuccessful()) {

                    SendUserMain();
                    Toast.makeText(SetupActivity.this, "Information Saved", Toast.LENGTH_LONG)
                        .show();

                  } else {

                    String msg = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, msg, Toast.LENGTH_LONG).show();
                  }
                }
              });
    }
  }

  private void SendUserMain() {

    Intent MainIntent = new Intent(SetupActivity.this, MainActivity.class);
    MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(MainIntent);
    finish();
  }
}
