package com.example.social_network_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {


    private EditText UserEmail, UserPass, UserConPass;
    private Button CreateAccButton;
    private ProgressBar LoadBar;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        UserEmail = (EditText) findViewById(R.id.reg_email);
        UserPass = (EditText) findViewById(R.id.reg_pass);
        UserConPass = (EditText) findViewById(R.id.reg_passc);
        CreateAccButton = (Button) findViewById(R.id.reg_cre_acc);

        mAuth = FirebaseAuth.getInstance();
        LoadBar = new ProgressBar(this);
        
        
        
        CreateAccButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                CreateAcc();
            }
        });

    }

    private void CreateAcc() {

        String email = UserEmail.getText().toString();
        String pass = UserPass.getText().toString();
        String passc = UserConPass.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(passc)) {

            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_LONG).show();

        }else if(!(pass.equals(passc))){

            Toast.makeText(this,"Pass Dont Match",Toast.LENGTH_LONG).show();

        }else if(pass.length() < 6){

            Toast.makeText(this,"Password must be longer than 6 characters",Toast.LENGTH_LONG).show();

        }else{



            mAuth.createUserWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){
                            if(task.isSuccessful()){

                                SendUserToSetup();

                                Toast.makeText(RegisterActivity.this,"You successfully Registered",Toast.LENGTH_LONG).show();

                            }else{

                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this,"Error: "+message,Toast.LENGTH_LONG).show();


                            }
                        }
                    });


        }


    }

    private void SendUserToSetup() {

        Intent SetupIntent = new Intent(RegisterActivity.this,SetupActivity.class);
        SetupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(SetupIntent);
        finish();
    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            SendUserMain();

        }
    }

    private void SendUserMain() {

        Intent MainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();

    }


}
