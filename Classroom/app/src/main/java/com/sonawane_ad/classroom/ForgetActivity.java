package com.sonawane_ad.classroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetActivity extends AppCompatActivity {
    private EditText useremail;
    private Button forgetbtn;
    private FirebaseAuth classroomauth;
    private OnCompleteListener<AuthResult> _classroomauth_create_user_listener;
    private OnCompleteListener<AuthResult> _classroomauth_sign_in_listener;
    private OnCompleteListener<Void> _classroomauth_reset_password_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        useremail = findViewById(R.id.UI_email_et);
        forgetbtn = findViewById(R.id.UI_forget_btn);
        classroomauth = FirebaseAuth.getInstance();
        forgetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if(!(useremail.getText().toString().equals(""))) {
                    classroomauth.sendPasswordResetEmail(useremail.getText().toString()).addOnCompleteListener(_classroomauth_reset_password_listener);
                    Toast.makeText(getApplicationContext(), "Email Sent Successfully", Toast.LENGTH_LONG).show();
                    supportFinishAfterTransition();
                }
                else
                    Toast.makeText(getApplicationContext(), "Enter Valid Email ID", Toast.LENGTH_LONG).show();
            }
        });

        _classroomauth_create_user_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {

            }
        };

        _classroomauth_sign_in_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {

            }
        };

        _classroomauth_reset_password_listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> _param1) {

            }
        };

    }
}