package com.example.social_network_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

  private NavigationView navigationView;
  private DrawerLayout drawerLayout;
  private RecyclerView postList;
  private Toolbar mToolbar;
  private ActionBarDrawerToggle actionBarDrawerToggle;

  private CircleImageView NavProfImg;
  private TextView userName;

  private FirebaseAuth mAuth;
  private DatabaseReference UserRef;

  private ImageButton AddPostButton;



  String currID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mAuth = FirebaseAuth.getInstance();
    UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
    currID = mAuth.getCurrentUser().getUid();

    AddPostButton = (ImageButton) findViewById(R.id.add_new_post_button);

    drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
    navigationView = (NavigationView) findViewById(R.id.nav_view);
    View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
    mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle("Home");
    actionBarDrawerToggle =
        new ActionBarDrawerToggle(
            MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
    drawerLayout.addDrawerListener(actionBarDrawerToggle);
    actionBarDrawerToggle.syncState();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    NavProfImg = (CircleImageView) navView.findViewById(R.id.nav_profile_img);
    userName = (TextView) navView.findViewById(R.id.nav_usernam_full);

    UserRef.child(currID)
        .addValueEventListener(
            new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                  if(dataSnapshot.hasChild("fullname")){

                      String fullname = dataSnapshot.child("fullname").getValue().toString();
                      userName.setText(fullname);


                  }
                  if(dataSnapshot.hasChild("profileimage")){
                      String img = dataSnapshot.child("profileimage").getValue().toString();


                      Picasso.get().load(img).placeholder(R.drawable.profile).into(NavProfImg);

                  }

                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {}
            });

    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            UserMenuSelector(item);
            return false;
          }
        });

    AddPostButton.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v){
            SendUserToPost();
        }
    });
  }


  private void SendUserToPost() {

    Intent NewPostIntent = new Intent(MainActivity.this, PostActivity.class);
    NewPostIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(NewPostIntent);
    finish();

  }


  @Override
  protected void onStart() {
    super.onStart();

    FirebaseUser currentUser = mAuth.getCurrentUser();

    if (currentUser == null) {
      SendUserToLoginActivty();

    } else {

      CheckUserExit();
    }
  }

  private void CheckUserExit() {

    final String U_I_D = mAuth.getCurrentUser().getUid();

    UserRef.addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {

            if (!dataSnapshot.hasChild(U_I_D)) {

              SendUserToSetup();
            }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {}
        });
  }

  private void SendUserToSetup() {

    Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
    setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(setupIntent);
    finish();
  }

  private void SendUserToLoginActivty() {

    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(loginIntent);
    finish();
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void UserMenuSelector(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.nav_profile:
        Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
        break;
      case R.id.nav_home:
        Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
        break;
      case R.id.nav_friends:
        Toast.makeText(this, "Friends", Toast.LENGTH_SHORT).show();
        break;
      case R.id.nav_find_friends:
        Toast.makeText(this, "Add Friends", Toast.LENGTH_SHORT).show();
        break;
      case R.id.nav_Logout:
        mAuth.signOut();
        SendUserToLoginActivty();
        break;
      case R.id.nav_messages:
        Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
        break;
      case R.id.nav_settings:
        Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
        break;
      case R.id.nav_post:
        SendUserToPost();
        break;
    }
  }
}
