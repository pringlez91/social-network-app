package com.example.social_network_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NewAccLink;

    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NewAccLink = (TextView) findViewById(R.id.reg_acc_link);
        UserEmail = (EditText) findViewById(R.id.log_email);
        UserPassword = (EditText) findViewById(R.id.log_pass);
        LoginButton = (Button) findViewById(R.id.log_button);

        mAuth = FirebaseAuth.getInstance();



        NewAccLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendUserToRegister();

            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllowLogin();

            }
        });


    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            SendUserMain();

        }
    }

    private void AllowLogin(){

        String email = UserEmail.getText().toString();
        String pass = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){

            Toast.makeText(this,"Please enter your email and password",Toast.LENGTH_LONG);

        }else{

            mAuth.signInWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){
                            if(task.isSuccessful()){

                                SendUserMain();
                                Toast.makeText(LoginActivity.this,"You successfully Logged n",Toast.LENGTH_LONG).show();

                            }else{

                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error: "+message,Toast.LENGTH_LONG).show();


                            }
                        }
                    });

        }

    }

    private void SendUserMain() {

        Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();

    }

    private void SendUserToRegister() {

        Intent registerIntent = new Intent (LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);

    }
}
