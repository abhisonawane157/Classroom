package com.sonawane_ad.classroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    private EditText name,studid,emailid,password,department,course,rollnumber;
    private ImageView profileimg;
    private Button register;
    private TextView login;

    private Intent intent = new Intent();
    private SharedPreferences file;

    private String profileimgpath = "";
    private String profileimgname = "";
    private double img = 0;
    private double n = 0;
    private double exist = 0;

    public final int REQ_CD_PROFILEPIC = 101;
    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();

    private HashMap<String, Object> hashmap = new HashMap<>();

    private ArrayList<String> liststring = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private DatabaseReference users = _firebase.getReference("users");
    private ChildEventListener _users_child_listener;
    private FirebaseAuth usersauth;
    private OnCompleteListener<AuthResult> _usersauth_create_user_listener;
    private OnCompleteListener<AuthResult> _usersauth_sign_in_listener;
    private OnCompleteListener<Void> _usersauth_reset_password_listener;
    private StorageReference profile = _firebase_storage.getReference("profile");
    private OnCompleteListener<Uri> _profile_upload_success_listener;
    private OnSuccessListener<FileDownloadTask.TaskSnapshot> _profile_download_success_listener;
    private OnSuccessListener _profile_delete_success_listener;
    private OnProgressListener _profile_upload_progress_listener;
    private OnProgressListener _profile_download_progress_listener;
    private OnFailureListener _profile_failure_listener;
    private Intent profilepic = new Intent(Intent.ACTION_GET_CONTENT);
    private long profileimgsize=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        com.google.firebase.FirebaseApp.initializeApp(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);

        }
        img = 0;
        name = findViewById(R.id.name);
        studid = findViewById(R.id.studid);
        emailid = findViewById(R.id.emailid);
        password = findViewById(R.id.password);
        department = findViewById(R.id.department);
        course = findViewById(R.id.course);
        rollnumber = findViewById(R.id.rollnumber);
        register = findViewById(R.id.signup);
        profileimg = findViewById(R.id.profile);
        login = findViewById(R.id.login);
        profilepic.setType("image/*");
        profilepic.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);

        usersauth = FirebaseAuth.getInstance();
        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(profilepic,REQ_CD_PROFILEPIC);
            }
        });


        int i = 6;
        final String randomText = "1234567890qwertyuiopasdfghjklzxcvbnm";
        StringBuilder result = new StringBuilder();
        while (i > 0) {
            Random random = new Random();
            result.append(randomText.charAt(random.nextInt(randomText.length())));
            i--;
        }

        studid.setText(result.toString());

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (studid.getText().toString().trim().equals("") && name.getText().toString().trim().equals("") && (emailid.getText().toString().trim().equals("") && (password.getText().toString().trim().equals("") && (course.getText().toString().trim().equals("") && (department.getText().toString().trim().equals("") && rollnumber.getText().toString().trim().equals(""))))))
                {
                    Toast.makeText(getApplicationContext(), "Please Enter all credentials", Toast.LENGTH_LONG).show();
                }
                else {
                    if (password.length() >= 8){
                        if (img == 0) {
                            n = 0;
                            exist = 0;
                            for (int _repeat156 = 0; _repeat156 < (int) (listmap.size()); _repeat156++) {
                                if (listmap.get((int) n).get("memberid").toString().toLowerCase().equals(studid.getText().toString().toLowerCase())) {
                                    Toast.makeText(getApplicationContext(), "Id Already in Use", Toast.LENGTH_LONG).show();

                                    exist = 1;
                                }
                                n++;
                            }
                            if (exist == 0) {
                                usersauth.createUserWithEmailAndPassword(emailid.getText().toString(), password.getText().toString()).addOnCompleteListener(SignUpActivity.this, _usersauth_create_user_listener);
                                hashmap = new HashMap<>();
                                hashmap.put("studid", studid.getText().toString());
                                hashmap.put("name", name.getText().toString());
                                hashmap.put("emailid", emailid.getText().toString());
                                hashmap.put("password", password.getText().toString());
                                hashmap.put("course", course.getText().toString());
                                hashmap.put("department", department.getText().toString());
                                hashmap.put("rollnumber", rollnumber.getText().toString());
                                hashmap.put("profileurl", "-");
                                hashmap.put("rooms", " ");
                                hashmap.put("profilename", "-");

                                users.child(studid.getText().toString()).updateChildren(hashmap);
                                Toast.makeText(getApplicationContext(), "Successfully SignUp",Toast.LENGTH_LONG).show();
                                finish();
                            } else {

                            }
                        } else {
                            n = 0;
                            exist = 0;
                            for (int _repeat196 = 0; _repeat196 < (int) (listmap.size()); _repeat196++) {
                                if (listmap.get((int) n).get("studid").toString().toLowerCase().equals(studid.getText().toString().toLowerCase())) {
                                    Toast.makeText(getApplicationContext(), "Id Already in Use", Toast.LENGTH_LONG).show();
                                    exist = 1;
                                }
                                n++;
                            }
                            if (exist == 0) {
                                profile.child(profileimgname).putFile(Uri.fromFile(new File(profileimgpath))).addOnFailureListener(_profile_failure_listener).addOnProgressListener(_profile_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        return profile.child(profileimgname).getDownloadUrl();
                                    }
                                }).addOnCompleteListener(_profile_upload_success_listener);

                            } else {

                            }
                        }
                    }else{
                        password.setText("");
                        Toast.makeText(getApplicationContext(), "Password Length is Small", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        _users_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot _dataSnapshot) {
                        listmap = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                HashMap<String, Object> _map = _data.getValue(_ind);
                                listmap.add(_map);
                            }
                        }
                        catch (Exception _e) {
                            _e.printStackTrace();
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError _databaseError) {
                    }
                });
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
        users.addChildEventListener(_users_child_listener);

        _profile_upload_progress_listener = new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();

            }
        };

        _profile_download_progress_listener = new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();

            }
        };

        _profile_upload_success_listener = new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(Task<Uri> _param1) {
                final String _downloadUrl = _param1.getResult().toString();
                usersauth.createUserWithEmailAndPassword(emailid.getText().toString(), password.getText().toString()).addOnCompleteListener(SignUpActivity.this, _usersauth_create_user_listener);
                hashmap = new HashMap<>();
                hashmap.put("studid", studid.getText().toString());
                hashmap.put("name", name.getText().toString());
                hashmap.put("emailid", emailid.getText().toString());
                hashmap.put("password", password.getText().toString());
                hashmap.put("course", course.getText().toString());
                hashmap.put("department", department.getText().toString());
                hashmap.put("rollnumber", rollnumber.getText().toString());
                hashmap.put("profilename", profileimgname);
                hashmap.put("rooms", " ");
                hashmap.put("profileurl", _downloadUrl);
                hashmap.put("user_uid", "");
                users.child(studid.getText().toString()).updateChildren(hashmap);

                Toast.makeText(getApplicationContext(), "Successfully SignUp", Toast.LENGTH_LONG).show();
                hashmap.clear();
                finish();
            }
        };

        _profile_download_success_listener = new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot _param1) {
                final long _totalByteCount = _param1.getTotalByteCount();

            }
        };

        _profile_delete_success_listener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object _param1) {

            }
        };

        _profile_failure_listener = new OnFailureListener() {
            @Override
            public void onFailure(Exception _param1) {
                final String _message = _param1.getMessage();

            }
        };


        _usersauth_create_user_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {
                final boolean _success = _param1.isSuccessful();
                final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";

            }
        };

        _usersauth_sign_in_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {
                final boolean _success = _param1.isSuccessful();
                final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";

            }
        };

        _usersauth_reset_password_listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> _param1) {
                final boolean _success = _param1.isSuccessful();

            }
        };





    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

        switch (_requestCode) {
            case REQ_CD_PROFILEPIC:
                if (_resultCode == Activity.RESULT_OK) {
                    ArrayList<String> _filePath = new ArrayList<>();
                    if (_data != null) {
                        if (_data.getClipData() != null) {
                            for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
                                ClipData.Item _item = _data.getClipData().getItemAt(_index);
                                _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
                            }
                        }
                        else {
                            _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
                        }
                    }

                    profileimgname = "";
                    profileimgpath = "";
                    profileimgsize = 0;
                    img = 1;
                    profileimgpath = _filePath.get((int)(0));
                    profileimgname = Uri.parse(_filePath.get((int)(0))).getLastPathSegment();
                    profileimg.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath.get((int)(0)), 1024, 1024));
                    profileimg.setVisibility(View.VISIBLE);
                    profileimgsize= new java.io.File(profileimgpath).length()/1024;
                    if ((profileimgsize < 2000) && (profileimgsize > 0)) {
                        profileimgpath = _filePath.get((int)(0));
                        profileimgname = Uri.parse(profileimgpath).getLastPathSegment();
                        profileimg.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(profileimgpath, 1024, 1024));
                        img=1;
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Profile Pic must be less than 2Mb",Toast.LENGTH_LONG).show();
                        profileimgpath = "";
                        profileimgname = "";
                        profileimg.setImageResource(R.drawable.default_profile);
                        img = 0;
                    }

                }
                else {

                }
                break;
            default:
                break;
        }
    }

}

