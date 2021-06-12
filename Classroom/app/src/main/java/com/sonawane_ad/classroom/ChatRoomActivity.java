package com.sonawane_ad.classroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.source.DocumentSource;
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
import java.util.Timer;
import java.util.TimerTask;

public class ChatRoomActivity extends AppCompatActivity {

    private ListView listview1;
    private ImageView back,currprofile;
    private TextView currname;
    private ImageView imageview1,attach;
    private LinearLayout linear1;
    private ProgressBar progress;
    private TextView downloading;
    private EditText edittext1;
    private Timer _timer = new Timer();
    private ImageView attachment;
    private ImageView camera;
    private ImageView button1;
    private TextView textview1;
    private String imgpath = "";
    private VideoView videoView;
    private String imgurl = "";
    private String docurl = "";
    private String docpath = "";
    private double docsize = 0;
    private double imgsize = 0;
    private double videosize = 0;
//    private String imgPath = "";
//    private String imgName = "";
    private String path = "";
    private String filename = "";
    private String myurl = "";
    private String result = "";
    private double size = 0;
    private double sumCount = 0;
    private TimerTask timer;
    private String chatroom = "";
    private String chatcopy = "";
    private String user1 = "";
    private String user2 = "";
    private HashMap<String, Object> map = new HashMap<>();
    private double img = 0;
    private double doc = 0;
    private LinearLayout attachex;
    private File _file_camer;

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();
    private Intent intent = new Intent();
    private SharedPreferences file;
    private AlertDialog.Builder dialog;

    public final int REQ_CD_CAMERA = 101;
    public final int REQ_CD_FILEPICKER = 102;
    private Calendar calendar = Calendar.getInstance();
    private HashMap<String, Object> map1 = new HashMap<>();
    private HashMap<String, Object> map3 = new HashMap<>();
    private HashMap<String, Object> map2 = new HashMap<>();
    private ArrayList<HashMap<String, Object>> teacherlistmap = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> userlistmap = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> classworklistmap = new ArrayList<>();
    private ArrayList<String> classroomliststring = new ArrayList<>();

    private DatabaseReference classwork = _firebase.getReference("classwork");
    private ChildEventListener _classwork_child_listener;
    private DatabaseReference classroom = _firebase.getReference("classroom");
    private ChildEventListener _classroom_child_listener;
    private DatabaseReference classchat = _firebase.getReference("classchat");
    private ChildEventListener _classchat_child_listener;
    private Intent camer = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private File _file_camera;
    private DatabaseReference users = _firebase.getReference("users");
    private ChildEventListener _users_child_listener;
    private StorageReference classchatstore = _firebase_storage.getReference("classchatstore");
    private OnCompleteListener<Uri> _classchatstore_upload_success_listener;
    private OnSuccessListener<FileDownloadTask.TaskSnapshot> _classchatstore_download_success_listener;
    private OnSuccessListener _classchatstore_delete_success_listener;
    private OnProgressListener _classchatstore_upload_progress_listener;
    private OnProgressListener _classchatstore_download_progress_listener;
    private OnFailureListener _classchatstore_failure_listener;

    private FirebaseAuth classroomauth;
    private OnCompleteListener<AuthResult> _classroomauth_create_user_listener;
    private OnCompleteListener<AuthResult> _classroomauth_sign_in_listener;
    private OnCompleteListener<Void> _classroomauth_reset_password_listener;

    private Intent filepicker = new Intent(Intent.ACTION_GET_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        } else {

        }

        listview1 = (ListView) findViewById(R.id.listview1);
        imageview1 = (ImageView) findViewById(R.id.imageview1);
        linear1 = (LinearLayout) findViewById(R.id.linear1);
        edittext1 = (EditText) findViewById(R.id.edittext1);
        attachment = (ImageView) findViewById(R.id.attachment);
        camera = (ImageView) findViewById(R.id.camera);
        button1 = (ImageView) findViewById(R.id.button1);
        back = (ImageView) findViewById(R.id.back);
        currname = (TextView) findViewById(R.id.currname);
        currprofile = (ImageView) findViewById(R.id.currprofile);
        attach = (ImageView) findViewById(R.id.attach);
        textview1 = (TextView) findViewById(R.id.textview1);
        videoView = findViewById(R.id.videoview);
        attachex = findViewById(R.id.attachex);
        progress = findViewById(R.id.progress);
        downloading = findViewById(R.id.downloading);
        classroomauth = FirebaseAuth.getInstance();
        dialog = new AlertDialog.Builder(this);
        _file_camer = FileUtil.createNewPictureFile(getApplicationContext());
        Uri _uri_camer = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                _uri_camer= FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", _file_camer);
            }
            else {
            _uri_camer = Uri.fromFile(_file_camer);
        }
        camer.putExtra(MediaStore.EXTRA_OUTPUT, _uri_camer);
        camer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        filepicker.setType("*/*");
        filepicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        downloading.setVisibility(View.GONE);
        currname.setText(file.getString("classworkname",""));
        if(file.getString("img","").equals("1"))
        {
            currprofile.setImageResource(R.drawable.work1);
        }else if(file.getString("img","").equals("2"))
        {
            currprofile.setImageResource(R.drawable.work2);
        }else if(file.getString("img","").equals("3"))
        {
            currprofile.setImageResource(R.drawable.work3);
        }else if(file.getString("img","").equals("4"))
        {
            currprofile.setImageResource(R.drawable.work4);
        }else if(file.getString("img","").equals("5"))
        {
            currprofile.setImageResource(R.drawable.work5);
        }else if(file.getString("img","").equals("6"))
        {
            currprofile.setImageResource(R.drawable.work6);
        }else{
            currprofile.setImageResource(R.drawable.default_image);
        }

        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
                final int _position = _param3;
                attachex.setVisibility(View.GONE);
                if (classworklistmap.get((int)_position).containsKey("image")) {
                    dialog.setTitle(classworklistmap.get((int)_position).get("username").toString());
                    dialog.setMessage(classworklistmap.get((int)_position).get("image").toString());
                    dialog.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {
                            myurl = classworklistmap.get((int)_position).get("image").toString();
                            new DownloadTask().execute(myurl);
                            Toast.makeText(getApplicationContext(), "Downloading......", Toast.LENGTH_LONG).show();
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {

                        }
                    });
                    dialog.create().show();
                }
                else if (classworklistmap.get((int)_position).containsKey("video")) {
                    dialog.setTitle(classworklistmap.get((int)_position).get("username").toString());
                    dialog.setMessage(classworklistmap.get((int)_position).get("video").toString());
                    dialog.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {
                            myurl = classworklistmap.get((int)_position).get("video").toString();
                            new DownloadTask().execute(myurl);
                            Toast.makeText(getApplicationContext(), "Downloading......", Toast.LENGTH_LONG).show();
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {

                        }
                    });
                    dialog.create().show();
                }
                else if (classworklistmap.get((int)_position).containsKey("document")) {
                    dialog.setTitle(classworklistmap.get((int)_position).get("documentname").toString());
                    dialog.setMessage(classworklistmap.get((int)_position).get("document").toString());
                    dialog.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {
                            myurl = classworklistmap.get((int)_position).get("document").toString();
                            new DownloadTask().execute(myurl);
                            Toast.makeText(getApplicationContext(), "Downloading......", Toast.LENGTH_LONG).show();
                        }
                    });
                    dialog.setNegativeButton("View", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {
                            file.edit().putString("docurl", classworklistmap.get((int)_position).get("document").toString()).commit();
                            intent.setClass(getApplicationContext(),ViewDocumentActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.create().show();
                }
                else {
                    dialog.setTitle(classworklistmap.get((int)_position).get("username").toString());
                    dialog.setMessage(classworklistmap.get((int)_position).get("message").toString());
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {

                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {

                        }
                    });
                    dialog.create().show();
                }
            }
        });

        listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
                final int _position = _param3;
                attachex.setVisibility(View.GONE);
                if (classworklistmap.get((int)_position).get("user_uid").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    dialog.setMessage("Delete message?");
                    dialog.setPositiveButton("DELETE FOR EVERYONE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {
                            classchat.child(classroomliststring.get((int)(_position))).removeValue();
                            classworklistmap.remove((int)(_position));
                            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                            if (classworklistmap.get((int)_position).containsKey("image")) {
                                _firebase_storage.getReferenceFromUrl(classworklistmap.get((int)_position).get("image").toString()).delete().addOnSuccessListener(_classchatstore_delete_success_listener).addOnFailureListener(_classchatstore_failure_listener);
                                Toast.makeText(getApplicationContext(), "Deleted",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {

                        }
                    });
                    dialog.create().show();
                }
                listview1.setAdapter(new Listview1Adapter(classworklistmap));
                ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
                return true;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(attachex.getVisibility() == View.GONE)
                {
                    attachex.setVisibility(View.VISIBLE);
                }else{
                    attachex.setVisibility(View.GONE);
                }
            }
        });
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                startActivityForResult(filepicker, REQ_CD_FILEPICKER);
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                startActivityForResult(camer, REQ_CD_CAMERA);
            }
        });


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (img == 0) {
                    if (edittext1.getText().toString().trim().equals("")) {
                        Toast.makeText(getApplicationContext(), "Enter Message", Toast.LENGTH_LONG).show();
                        edittext1.setText("");
                    }
                    else {
                        button1.setEnabled(false);
                        edittext1.setEnabled(false);
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("message", edittext1.getText().toString().trim());
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        classchat.push().updateChildren(map);
                        map.clear();
                        edittext1.setText("");
                        button1.setEnabled(true);
                        edittext1.setEnabled(true);
                    }
                }
                else {
                    if (img == 1) {
                        if ((imgsize < 2000) && (imgsize > 0)) {
                            doc = 0;
                            classchatstore.child(imgurl).putFile(Uri.fromFile(new File(imgpath))).addOnFailureListener(_classchatstore_failure_listener).addOnProgressListener(_classchatstore_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    return classchatstore.child(imgurl).getDownloadUrl();
                                }}).addOnCompleteListener(_classchatstore_upload_success_listener);
                            button1.setEnabled(false);
                            edittext1.setEnabled(false);
                            progress.setVisibility(View.VISIBLE);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Attachment size must be less than 1Mb", Toast.LENGTH_LONG).show();
                            Toast.makeText(ChatRoomActivity.this, "", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (img == 2) {
                        if ((videosize < 5000) && (videosize > 0)) {
                            doc = 0;
                            classchatstore.child(imgurl).putFile(Uri.fromFile(new File(imgpath))).addOnFailureListener(_classchatstore_failure_listener).addOnProgressListener(_classchatstore_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    return classchatstore.child(imgurl).getDownloadUrl();
                                }}).addOnCompleteListener(_classchatstore_upload_success_listener);
                            button1.setEnabled(false);
                            edittext1.setEnabled(false);
                            progress.setVisibility(View.VISIBLE);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Attachment size must be less than 5Mb", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        _classchat_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

                classchat.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot _dataSnapshot) {
                        classworklistmap = new ArrayList<>();
                        try {
                            GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            for (DataSnapshot _data : _dataSnapshot.getChildren()) {
                                HashMap<String, Object> _map = _data.getValue(_ind);
                                classworklistmap.add(_map);
                            }
                        }
                        catch (Exception _e) {
                            _e.printStackTrace();
                        }
                        classroomliststring.add(_childKey);
                        listview1.setAdapter(new Listview1Adapter(classworklistmap));
                        ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
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
                classchat.removeEventListener(_classchat_child_listener);
                classworklistmap.clear();
                listview1.setAdapter(new Listview1Adapter(classworklistmap));
                ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
                classchat.addChildEventListener(_classchat_child_listener);
            }

            @Override
            public void onChildMoved(DataSnapshot _param1, String _param2) {

            }

            @Override
            public void onChildRemoved(DataSnapshot _param1) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                classchat.removeEventListener(_classchat_child_listener);
                classworklistmap.clear();
                listview1.setAdapter(new Listview1Adapter(classworklistmap));
                ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
                classchat.addChildEventListener(_classchat_child_listener);
            }

            @Override
            public void onCancelled(DatabaseError _param1) {
                final int _errorCode = _param1.getCode();
                final String _errorMessage = _param1.getMessage();

            }
        };
        classchat.addChildEventListener(_classchat_child_listener);


        _classchatstore_upload_progress_listener = new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();

            }
        };

        _classchatstore_download_progress_listener = new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();

            }
        };

        _classchatstore_upload_success_listener = new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(Task<Uri> _param1) {
                final String _downloadUrl = _param1.getResult().toString();
                if (edittext1.getText().toString().trim().equals("")) {
                    if(img == 1)
                    {
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("image", _downloadUrl);
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        classchat.push().updateChildren(map);
                        map.clear();
                        img = 0;
                        button1.setEnabled(true);
                        edittext1.setText("");
                        imageview1.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        edittext1.setEnabled(true);
                    }
                    else if(img == 2)
                    {
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("video", _downloadUrl);
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        classchat.push().updateChildren(map);
                        map.clear();
                        img = 0;
                        button1.setEnabled(true);
                        edittext1.setText("");
                        imageview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        edittext1.setEnabled(true);
                    }
                    else if(doc == 1)
                    {
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("document", _downloadUrl);
                        map.put("documentname",docurl);
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        classchat.push().updateChildren(map);
                        map.clear();
                        doc = 0;
                        button1.setEnabled(true);
                        edittext1.setText("");
                        imageview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        edittext1.setEnabled(true);
                    }
                    else if(doc == 2)
                    {
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("document", _downloadUrl);
                        map.put("documentname",docurl);
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        classchat.push().updateChildren(map);
                        map.clear();
                        doc = 0;
                        button1.setEnabled(true);
                        edittext1.setText("");
                        imageview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        edittext1.setEnabled(true);
                    }
                    else{
                        Toast.makeText(ChatRoomActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if(img == 1)
                    {
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("message", edittext1.getText().toString().trim());
                        map.put("image", _downloadUrl);
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        classchat.push().updateChildren(map);
                        map.clear();
                        img = 0;
                        button1.setEnabled(true);
                        edittext1.setText("");
                        imageview1.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        edittext1.setEnabled(true);
                    }
                    else if(img == 2)
                    {
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("message", edittext1.getText().toString().trim());
                        map.put("video", _downloadUrl);
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        classchat.push().updateChildren(map);
                        map.clear();
                        img = 0;
                        button1.setEnabled(true);
                        edittext1.setText("");
                        imageview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        edittext1.setEnabled(true);
                    }
                    else if(doc == 1)
                    {
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("message", edittext1.getText().toString().trim());
                        map.put("document", _downloadUrl);
                        map.put("documentname",docurl);
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        classchat.push().updateChildren(map);
                        map.clear();
                        doc = 0;
                        button1.setEnabled(true);
                        edittext1.setText("");
                        imageview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        edittext1.setEnabled(true);
                    }
                    else if(doc == 2)
                    {
                        map = new HashMap<>();
                        map.put("username", file.getString("name", ""));
                        map.put("message", edittext1.getText().toString().trim());
                        map.put("document", _downloadUrl);
                        map.put("documentname",docurl);
                        map.put("user_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("profileurl", file.getString("profileurl", ""));
                        map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        calendar = Calendar.getInstance();
                        map.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));
                        classchat.push().updateChildren(map);
                        map.clear();
                        doc = 0;
                        button1.setEnabled(true);
                        edittext1.setText("");
                        imageview1.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        edittext1.setEnabled(true);
                    }
                    else{
                        Toast.makeText(ChatRoomActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        _classchatstore_download_success_listener = new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot _param1) {
                final long _totalByteCount = _param1.getTotalByteCount();

            }
        };

        _classchatstore_delete_success_listener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object _param1) {

            }
        };

        _classchatstore_failure_listener = new OnFailureListener() {
            @Override
            public void onFailure(Exception _param1) {
                final String _message = _param1.getMessage();

            }
        };

        classchat.removeEventListener(_classchat_child_listener);
        chatroom = "classchat/".concat(file.getString("roomid","").concat("/"+file.getString("classworkid","")));
        classchat = _firebase.getReference(chatroom);
        classchat.addChildEventListener(_classchat_child_listener);
        imageview1.setVisibility(View.GONE);
        textview1.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }
    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

        switch (_requestCode) {
            case REQ_CD_CAMERA:
                if (_resultCode == Activity.RESULT_OK) {
                    String _filePath = _file_camer.getAbsolutePath();
                    attachex.setVisibility(View.GONE);
                    imgpath = "";
                    imgurl = "";
                    imgsize = 0;
                    videosize = 0;
                    img = 1;
                    imgpath = _filePath;
                    imgurl = Uri.parse(_filePath).getLastPathSegment();
                    imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath, 1024, 1024));
                    imageview1.setVisibility(View.VISIBLE);
                    imgsize = new java.io.File(imgpath).length() / 1024;
                    if ((imgsize < 2000) && (imgsize > 0)) {
                        textview1.setVisibility(View.VISIBLE);
                        textview1.setText("File Size: ".concat(String.valueOf(imgsize).concat("Kb")));
                    } else {
                        Toast.makeText(getApplicationContext(), "Captured image must be less than 2Mb", Toast.LENGTH_LONG);
                        imageview1.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        imgpath = "";
                        imgurl = "";
                        img = 0;
                    }
                } else {

                }
                break;

            case REQ_CD_FILEPICKER:
                if (_resultCode == Activity.RESULT_OK) {
                    ArrayList<String> _filePath = new ArrayList<>();
                    if (_data != null) {
                        if (_data.getClipData() != null) {
                            for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
                                ClipData.Item _item = _data.getClipData().getItemAt(_index);
                                _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
                            }
                        } else {
                            _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
                        }
                    }

                    if (Uri.parse(_filePath.get((int) (0))).getLastPathSegment().endsWith(".mp4")) {
                        Log.e("MP4","mp4");
                        attachex.setVisibility(View.GONE);
                        imgpath = "";
                        imgurl = "";
                        imageview1.setVisibility(View.GONE);
                        img = 2;
                        imgpath = _filePath.get((int) (0));
                        imgurl = Uri.parse(_filePath.get((int) (0))).getLastPathSegment();
                        videosize = new java.io.File(imgpath).length() / 1024;
                        if ((videosize < 5000) && (videosize > 0)) {
                            videoView.setVisibility(View.VISIBLE);
                            videoView.setVideoURI(Uri.parse(imgpath));
                            videoView.requestFocus();
                            videoView.start();
                            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    videoView.start();
                                }
                            });
                            textview1.setVisibility(View.VISIBLE);
                            textview1.setText("File Size: ".concat(String.valueOf(videosize).concat("Kb")));
                        } else {
                            Toast.makeText(getApplicationContext(), "Attachment must be less than 5Mb", Toast.LENGTH_LONG);
                            attachex.setVisibility(View.VISIBLE);
                            textview1.setText("");
                            attachex.setVisibility(View.VISIBLE);
                            textview1.setVisibility(View.GONE);
                            imageview1.setVisibility(View.GONE);
                            videoView.setVisibility(View.GONE);
                            imgpath = "";
                            imgurl = "";
                            img = 0;
                        }
                    }
                    else if (Uri.parse(_filePath.get((int) (0))).getLastPathSegment().endsWith(".jpg")) {
                        Log.e("JPG","jpg");
                        imgurl = "";
                        imgpath = "";
                        imgsize = 0;
                        videosize = 0;
                        attachex.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        img = 1;
                        imgpath = _filePath.get((int) (0));
                        imgurl = Uri.parse(_filePath.get((int) (0))).getLastPathSegment();
                        imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath.get((int) (0)), 1024, 1024));
                        imageview1.setVisibility(View.VISIBLE);
                        imgsize = new java.io.File(imgpath).length() / 1024;
                        if ((imgsize < 2000) && (imgsize > 0)) {
                            textview1.setVisibility(View.VISIBLE);
                            textview1.setText("File Size: ".concat(String.valueOf(imgsize).concat("Kb")));
                        } else {
                            Toast.makeText(getApplicationContext(), "Attachment must be less than 2Mb", Toast.LENGTH_LONG);
                            imageview1.setVisibility(View.GONE);
                            textview1.setVisibility(View.GONE);
                            attachex.setVisibility(View.VISIBLE);
                            videoView.setVisibility(View.GONE);
                            textview1.setText("");
                            imgpath = "";
                            imgurl = "";
                            img = 0;
                        }
                    }
                    else if (Uri.parse(_filePath.get((int) (0))).getLastPathSegment().endsWith(".png")) {
                        Log.e("PNG","png");
                        imgurl = "";
                        imgpath = "";
                        imgsize = 0;
                        videosize = 0;
                        attachex.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        img = 1;
                        imgpath = _filePath.get((int) (0));
                        imgurl = Uri.parse(_filePath.get((int) (0))).getLastPathSegment();
                        imageview1.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath.get((int) (0)), 1024, 1024));
                        imageview1.setVisibility(View.VISIBLE);
                        imgsize = new java.io.File(imgpath).length() / 1024;
                        if ((imgsize < 2000) && (imgsize > 0)) {
                            textview1.setVisibility(View.VISIBLE);
                            textview1.setText("File Size: ".concat(String.valueOf(imgsize).concat("Kb")));
                        } else {
                            Toast.makeText(getApplicationContext(), "Attachment must be less than 2Mb", Toast.LENGTH_LONG);
                            imageview1.setVisibility(View.GONE);
                            textview1.setVisibility(View.GONE);
                            attachex.setVisibility(View.VISIBLE);
                            videoView.setVisibility(View.GONE);
                            textview1.setText("");
                            imgpath = "";
                            imgurl = "";
                            img = 0;
                        }
                    }
                    else if (Uri.parse(_filePath.get((int) (0))).getLastPathSegment().endsWith(".pdf")) {
                            Log.e("PDF","pdf");
                            doc = 1;
                            docpath = "";
                            docurl = "";
                            docsize = 0;
                            img = 0;
                            Toast.makeText(getApplicationContext(), "Attachment is doc", Toast.LENGTH_LONG);
                            docpath = _filePath.get((int) (0));
                            docurl = Uri.parse(Uri.parse(_filePath.get((int) (0))).getLastPathSegment()).getLastPathSegment();
                            docsize = new java.io.File(docpath).length() / 1024;
                            dialog.setTitle("Upload");
                            dialog.setMessage(Uri.parse(_filePath.get((int) (0))).getLastPathSegment()+"\nSize : "+docsize+" Kb");
                            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface _dialog, int _which) {
                                doc = 1;
                                classchatstore.child(docurl).putFile(Uri.fromFile(new File(docpath))).addOnFailureListener(_classchatstore_failure_listener).addOnProgressListener(_classchatstore_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        return classchatstore.child(docurl).getDownloadUrl();
                                    }}).addOnCompleteListener(_classchatstore_upload_success_listener);
                                button1.setEnabled(false);
                                edittext1.setEnabled(false);
                                progress.setVisibility(View.VISIBLE);
                            }
                        });
                        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface _dialog, int _which) {

                            }
                        });
                        dialog.create().show();

                        }
                    else if (Uri.parse(_filePath.get((int) (0))).getLastPathSegment().endsWith(".docx")) {
                        Log.e("DOCX","docx");
                        doc = 2;
                        docpath = "";
                        docurl = "";
                        docsize = 0;
                        img = 0;
                        attachex.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Attachment is doc", Toast.LENGTH_LONG);
                        docpath = _filePath.get((int) (0));
                        docurl = Uri.parse(Uri.parse(_filePath.get((int) (0))).getLastPathSegment()).getLastPathSegment();
                        docsize = new java.io.File(docpath).length() / 1024;
                        dialog.setTitle("Upload");
                        dialog.setMessage(Uri.parse(_filePath.get((int) (0))).getLastPathSegment()+"\nSize : "+docsize+" Kb");
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface _dialog, int _which) {
                                doc = 2;
                                classchatstore.child(docurl).putFile(Uri.fromFile(new File(docpath))).addOnFailureListener(_classchatstore_failure_listener).addOnProgressListener(_classchatstore_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        return classchatstore.child(docurl).getDownloadUrl();
                                    }}).addOnCompleteListener(_classchatstore_upload_success_listener);
                                button1.setEnabled(false);
                                edittext1.setEnabled(false);
                                progress.setVisibility(View.VISIBLE);
                            }
                        });
                        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface _dialog, int _which) {

                            }
                        });
                        dialog.create().show();

                    }
                    else {
                        imgurl = "";
                        imgpath = "";
                        imgsize = 0;    
                        videosize = 0;
                        attachex.setVisibility(View.GONE);
                        videoView.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "File Not Supported here", Toast.LENGTH_LONG);
                    }

                } else {
                }
                break;
            default:
                break;
        }
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {


        @Override

        protected void onPreExecute() {
            downloading.setVisibility(View.VISIBLE);
        }
        protected String doInBackground(String... address) {
            try {
                filename= URLUtil.guessFileName(address[0], null, null);
                int resCode = -1;
                java.io.InputStream in = null;
                java.net.URL url = new java.net.URL(address[0]);
                java.net.URLConnection urlConn = url.openConnection();
                if (!(urlConn instanceof java.net.HttpURLConnection)) {
                    throw new java.io.IOException("URL is not an Http URL"); }
                java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) urlConn; httpConn.setAllowUserInteraction(false); httpConn.setInstanceFollowRedirects(true); httpConn.setRequestMethod("GET"); httpConn.connect();
                resCode = httpConn.getResponseCode();
                if (resCode == java.net.HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                    size = httpConn.getContentLength();

                } else { result = "There was an error"; }

                path = FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS).concat("/".concat(filename));
                FileUtil.writeFile(path, "");
                java.io.File file = new java.io.File(path);

                java.io.OutputStream output = new java.io.FileOutputStream(file);
                try {
                    int bytesRead;
                    sumCount = 0;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = in.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                        sumCount += bytesRead;
                        if (size > 0) {
                            publishProgress((int)Math.round(sumCount*100 / size));
                        }
                    }
                } finally {
                    output.close();
                }
                result = filename + " saved";
                in.close();
            } catch (java.net.MalformedURLException e) {
                result = e.getMessage();
            } catch (java.io.IOException e) {
                result = e.getMessage();
            } catch (Exception e) {
                result = e.toString();
            }
            return result;

        }
        protected void onPostExecute(String s){
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            downloading.setVisibility(View.GONE);
        }
    }


    public class Listview1Adapter extends BaseAdapter {
        ArrayList<HashMap<String, Object>> _data;
        public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public int getCount() {
            return _data.size();
        }

        @Override
        public HashMap<String, Object> getItem(int _index) {
            return _data.get(_index);
        }

        @Override
        public long getItemId(int _index) {
            return _index;
        }
        @Override
        public View getView(final int _position, View _view, ViewGroup _viewGroup) {
            LayoutInflater _inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View _v = _view;
            if (_v == null) {
                _v = _inflater.inflate(R.layout.custom_chat_message, null);
            }

            final LinearLayout linear1 = (LinearLayout) _v.findViewById(R.id.linear1);
            final ImageView noprofile = (ImageView) _v.findViewById(R.id.noprofile);
            final LinearLayout linear2 = (LinearLayout) _v.findViewById(R.id.linear2);
            final ImageView profile = (ImageView) _v.findViewById(R.id.profile);
            final TextView username = (TextView) _v.findViewById(R.id.username);
            final ImageView imageview1 = (ImageView) _v.findViewById(R.id.imageview1);
            final LinearLayout linear3 = (LinearLayout) _v.findViewById(R.id.linear3);
            final TextView message = (TextView) _v.findViewById(R.id.message);
            final TextView docname = (TextView) _v.findViewById(R.id.docname);
            final TextView time = (TextView) _v.findViewById(R.id.time);
            final VideoView videoView = (VideoView) _v.findViewById(R.id.videoview);


            if (classworklistmap.get((int)_position).containsKey("message") || classworklistmap.get((int)_position).containsKey("document") || classworklistmap.get((int)_position).containsKey("image") || classworklistmap.get((int)_position).containsKey("video")) {
                username.setText(classworklistmap.get((int)_position).get("username").toString());
                time.setText(classworklistmap.get((int)_position).get("time").toString());

                if (classworklistmap.get((int)_position).get("profileurl").toString().trim().equals("")) {
                    noprofile.setBackgroundResource(R.drawable.default_profile);
                    profile.setBackgroundResource(R.drawable.default_profile);
                }else{
                    Glide.with(getApplicationContext()).load(Uri.parse(classworklistmap.get((int)_position).get("profileurl").toString())).into(noprofile);
                    Glide.with(getApplicationContext()).load(Uri.parse(classworklistmap.get((int)_position).get("profileurl").toString())).into(profile);
                }
            }
            if (classworklistmap.get((int)_position).containsKey("message")) {
                message.setText(classworklistmap.get((int)_position).get("message").toString());
                message.setVisibility(View.VISIBLE);
            }
            else {
                message.setVisibility(View.GONE);
            }
            if (classworklistmap.get((int)_position).containsKey("document")) {
                docname.setText(classworklistmap.get((int)_position).get("documentname").toString());
                docname.setVisibility(View.VISIBLE);
            }
            else {
                docname.setVisibility(View.GONE);
            }

            if (classworklistmap.get((int)_position).containsKey("image")) {
                imageview1.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(Uri.parse(classworklistmap.get((int)_position).get("image").toString())).into(imageview1);
            }
            else {
                imageview1.setVisibility(View.GONE);
            }

            if ((classworklistmap.get((int)_position).containsKey("video"))) {
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(Uri.parse(classworklistmap.get((int)_position).get("video").toString()));
                videoView.requestFocus();
                videoView.start();
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        videoView.start();
                    }
                });
            }
            else {
                videoView.setVisibility(View.GONE);
            }
            if (classworklistmap.get((int)_position).get("user_uid").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                linear1.setGravity(Gravity.RIGHT);
                noprofile.setVisibility(View.GONE);
                username.setText("me");
                username.setGravity(Gravity.RIGHT);
                profile.setVisibility(View.VISIBLE);
                linear2.setBackgroundResource(R.drawable.chat_sender);
            }
            else {
                linear1.setGravity(Gravity.LEFT);
                profile.setVisibility(View.GONE);
                username.setGravity(Gravity.LEFT);
                noprofile.setVisibility(View.VISIBLE);
                linear2.setBackgroundResource(R.drawable.chat_receiver);

            }

            return _v;
        }
    }
}
