package com.example.social_network_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

  private EditText UserName, FullName, Country;
  private Button SaveInfo;
  private CircleImageView profileImage;

  private FirebaseAuth mAuth;
  private DatabaseReference UsersRef;

  String currentUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setup);

    mAuth = FirebaseAuth.getInstance();
    currentUser = mAuth.getCurrentUser().getUid();
    UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);

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
