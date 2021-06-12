package com.sonawane_ad.classroom;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SharedMemory;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private EditText loginid, password;
    private TextView register, forget;
    private Intent intent = new Intent();
    private SharedPreferences file;

    //Database
    private FirebaseAuth classroomauth;
    private OnCompleteListener<AuthResult> _classroomauth_create_user_listener;
    private OnCompleteListener<AuthResult> _classroomauth_sign_in_listener;
    private OnCompleteListener<Void> _classroomauth_reset_password_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.UI_login_btn);
        loginid = findViewById(R.id.UI_email_et);
        password = findViewById(R.id.UI_password_et);
        register = findViewById(R.id.UI_signup_txt);
        forget = findViewById(R.id.UI_forget_txt);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);

        classroomauth = FirebaseAuth.getInstance();
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getApplicationContext(), ForgetActivity.class);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginid.getText().toString().trim() != null && password.getText().toString().trim() != null)
                {
                    classroomauth.signInWithEmailAndPassword(loginid.getText().toString(), password.getText().toString()).addOnCompleteListener(LoginActivity.this, _classroomauth_sign_in_listener);
                }
            }
        });

        _classroomauth_create_user_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {
                final boolean _success = _param1.isSuccessful();
                final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";

            }
        };

        _classroomauth_sign_in_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {
                final boolean _success = _param1.isSuccessful();
                final String errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
                if (_success) {
                    file.edit().putString("emailid", loginid.getText().toString()).commit();
                    file.edit().putString("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid()).commit();
                    intent.setClass(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: "+errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        };

        _classroomauth_reset_password_listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> _param1) {
                final boolean _success = _param1.isSuccessful();

            }
        };

        if ((FirebaseAuth.getInstance().getCurrentUser() != null)) {
            intent.setClass(getApplicationContext(), HomeActivity.class);
            Toast.makeText(getApplicationContext(), "Welcome back, ", Toast.LENGTH_LONG).show();
            startActivity(intent);
            finish();
        }
        else {

        }
    }
}