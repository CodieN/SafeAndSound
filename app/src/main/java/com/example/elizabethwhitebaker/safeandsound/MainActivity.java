package com.example.elizabethwhitebaker.safeandsound;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = "MainActivity";

    private static final int REQUEST_SMS = 1;
    private static final int REQUEST_READ_SMS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSignIn = findViewById(R.id.signin_button);
        Button btnSignUp = findViewById(R.id.signup_button);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasReadSMSPermission = checkSelfPermission(READ_SMS);
            if (hasReadSMSPermission != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{READ_SMS}, REQUEST_READ_SMS);
            int hasSendSMSPermission = checkSelfPermission(SEND_SMS);
            if(hasSendSMSPermission != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{SEND_SMS}, REQUEST_SMS);
        }
    }
}
