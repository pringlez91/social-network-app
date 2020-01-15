package com.example.social_network_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;



public class RegisterActivity extends AppCompatActivity {


    private EditText UserEmail, UserPass, UserConPass;
    private Button CreateAccButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        UserEmail = (EditText) findViewById(R.id.reg_email);
        UserPass = (EditText) findViewById(R.id.reg_pass);
        UserConPass = (EditText) findViewById(R.id.reg_passc);
        CreateAccButton = (Button) findViewById(R.id.reg_cre_acc);

    }
}
