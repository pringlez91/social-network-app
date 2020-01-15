package com.example.social_network_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NewAccLink;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NewAccLink = (TextView) findViewById(R.id.reg_acc_link);
        UserEmail = (EditText) findViewById(R.id.log_email);
        UserPassword = (EditText) findViewById(R.id.log_pass);
        LoginButton = (Button) findViewById(R.id.log_button);



        NewAccLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendUserToRegister();

            }
        });


    }

    private void SendUserToRegister() {

        Intent registerIntent = new Intent (LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);

    }
}
