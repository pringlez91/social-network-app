package com.example.social_network_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
    imageRef = FirebaseStorage.getInstance().getReference().child("profile img");

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
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == Pic && resultCode == RESULT_OK && data != null) {
      Uri ImageUri = data.getData();
      CropImage.activity()
          .setGuidelines(CropImageView.Guidelines.ON)
          .setAspectRatio(1, 1)
          .start(this);
    }

    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      if (requestCode == RESULT_OK) {
        Uri resultUri = result.getUri();

        StorageReference path = imageRef.child(currentUser + ".jpg");

        path.putFile(resultUri)
            .addOnCompleteListener(
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                      final String imgURL =
                          task.getResult().getStorage().getDownloadUrl().toString();
                      UsersRef.child("profileimg")
                          .setValue(imgURL)
                          .addOnCompleteListener(
                              new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                  if (task.isSuccessful()) {
                                    Intent setup =
                                        new Intent(SetupActivity.this, SetupActivity.class);
                                    startActivity(setup);
                                    Toast.makeText(
                                            SetupActivity.this,
                                            "Profile Image uploaded",
                                            Toast.LENGTH_LONG)
                                        .show();

                                  } else {
                                    String msg = task.getException().getMessage();
                                    Toast.makeText(
                                            SetupActivity.this, "Error: " + msg, Toast.LENGTH_LONG)
                                        .show();
                                  }
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
