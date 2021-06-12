package com.sonawane_ad.classroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class AddClassworkActivity extends AppCompatActivity {

    private EditText classworkname, roomid, subject, classworkid;
    private Button submit;

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();
    private Intent intent = new Intent();
    private SharedPreferences file;
    private AlertDialog.Builder dialog;

    public final int REQ_CD_CAMERA = 101;
    private HashMap<String, Object> map1 = new HashMap<>();
    private HashMap<String, Object> map = new HashMap<>();
    private HashMap<String, Object> map2 = new HashMap<>();
    private ArrayList<HashMap<String, Object>> teacherlistmap = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> userlistmap = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> classworklistmap = new ArrayList<>();
    private ArrayList<String> classroomliststring = new ArrayList<>();

    private DatabaseReference classwork = _firebase.getReference("classwork");
    private ChildEventListener _classwork_child_listener;
    private DatabaseReference classroom = _firebase.getReference("classroom");
    private ChildEventListener _classroom_child_listener;
    private Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private File _file_camera;
    private DatabaseReference users = _firebase.getReference("users");
    private ChildEventListener _users_child_listener;

    private FirebaseAuth classroomauth;
    private OnCompleteListener<AuthResult> _classroomauth_create_user_listener;
    private OnCompleteListener<AuthResult> _classroomauth_sign_in_listener;
    private OnCompleteListener<Void> _classroomauth_reset_password_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classwork);

        roomid = findViewById(R.id.roomid);
        subject = findViewById(R.id.subject);
        classworkname = findViewById(R.id.classworkname);
        classworkid = findViewById(R.id.classworkid);
        submit = findViewById(R.id.submit);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);

        int i = 8;
        final String randomText = "1234567890qwertyuiopasdfghjklzxcvbnm";
        StringBuilder result = new StringBuilder();
        while (i > 0) {
            Random random = new Random();
            result.append(randomText.charAt(random.nextInt(randomText.length())));
            i--;
        }
        classworkid.setText(result.toString());

        roomid.setText(file.getString("roomid",""));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roomid.getText().toString().equals("") && subject.getText().toString().equals("") && classworkname.getText().toString().equals(""))
                {
                    Toast.makeText(AddClassworkActivity.this, "Enter Valid Credentials", Toast.LENGTH_SHORT).show();
                }else{
                    map = new HashMap<>();
                    map.put("classworkname", classworkname.getText().toString());
                    map.put("user_uid", file.getString("user_uid",""));
                    map.put("classworkid", classworkid.getText().toString());
                    map.put("subject", subject.getText().toString());
                    map.put("roomid", roomid.getText().toString());
                    map.put("location",roomid.getText().toString().concat("/").concat(classworkid.getText().toString()));
//                    classroommap.put("ownerid",file.getString("studid", ""));
//                    classroommap.put("studentnames", "");
//                    classroommap.put("totalstudents", "0");
//                    classroommap.put("studentemails", "");
//                    classroommap.put("studentids", "");
                    classwork.child(roomid.getText().toString().concat("/").concat(classworkid.getText().toString())).updateChildren(map);
                    finish();
                }
            }
        });

        _classwork_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

            }

            @Override
            public void onChildMoved(DataSnapshot _param1, String _param2) {

            }

            @Override
            public void onChildRemoved(DataSnapshot _param1) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

            }

            @Override
            public void onCancelled(DatabaseError _param1) {
                final int _errorCode = _param1.getCode();
                final String _errorMessage = _param1.getMessage();
            }
        };
    }
}