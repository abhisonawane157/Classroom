package com.sonawane_ad.classroom;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();
    private ListView listview1;
    private ImageView logout, profile;
    private FloatingActionButton _fab;
    private Intent intent = new Intent();
    private SharedPreferences file;
    private AlertDialog.Builder dialog, dialog1, dialog2;
    SwipeRefreshLayout refresh;
    private Button back;

    //Database
    public final int REQ_CD_CAMERA = 101;
    private HashMap<String, Object> map1 = new HashMap<>();
    private HashMap<String, Object> usermaps = new HashMap<>();
    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> classroomlistmap = new ArrayList<>();
    private ArrayList<String> classroomliststring = new ArrayList<>();

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
        setContentView(R.layout.activity_home);
        com.google.firebase.FirebaseApp.initializeApp(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        } else {

        }
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        _fab = findViewById(R.id._fabsearch);
        logout = findViewById(R.id.logout);
        refresh = findViewById(R.id.refresh);
        listview1 = findViewById(R.id.listview1);
        back = findViewById(R.id.back);
        profile = findViewById(R.id.imageview1);
        dialog = new AlertDialog.Builder(this);
        dialog1 = new AlertDialog.Builder(this);
        dialog2 = new AlertDialog.Builder(this);
        _file_camera = FileUtil.createNewPictureFile(getApplicationContext());
        Uri _uri_camera = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            _uri_camera = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", _file_camera);
//        } else {
            _uri_camera = Uri.fromFile(_file_camera);
        }
        camera.putExtra(MediaStore.EXTRA_OUTPUT, _uri_camera);
        camera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                classroom.removeEventListener(_classroom_child_listener);
                classroomlistmap.clear();
                listview1.setAdapter(new Listview1Adapter(classroomlistmap));
                ((BaseAdapter) listview1.getAdapter()).notifyDataSetChanged();
                classroom.addChildEventListener(_classroom_child_listener);
                refresh.setRefreshing(false);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.setTitle("Logout");
                dialog1.setMessage("Do you want to logout?");
                dialog1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        FirebaseAuth.getInstance().signOut();
                        intent.setClass(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {

                    }
                });
                dialog1.create().show();
            }
        });

        _fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                dialog.setTitle("Classroom");
                dialog.setMessage("Do you want to Create or Join a classroom ?");
                dialog.setPositiveButton("Create ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
//                        showNotificationImage();
                        intent.setClass(getApplicationContext(), CreateClassroomActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.setNegativeButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        intent.setClass(getApplicationContext(), JoinClassroomActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.create().show();


            }
        });

        _classroom_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (_childValue.get("studentemails").toString().contains(file.getString("emailid", "")) && _childValue.get("studentids").toString().contains(file.getString("studid", ""))) {
                    map = new HashMap<>();
                    map.put("classroomname", _childValue.get("classroomname").toString());
                    map.put("classroomby", _childValue.get("classroomby").toString());
                    map.put("owneremailid", _childValue.get("owneremailid").toString());
                    map.put("course", _childValue.get("course").toString());
                    map.put("studentnames", _childValue.get("studentnames").toString());
                    map.put("studentids", _childValue.get("studentids").toString());
                    map.put("studentemails", _childValue.get("studentemails").toString());
                    map.put("department", _childValue.get("department").toString());
                    map.put("roomid", _childValue.get("roomid").toString());
                    map.put("totalstudents", _childValue.get("totalstudents").toString());
                    classroomlistmap.add(map);
                } else {

                }
                listview1.setAdapter(new Listview1Adapter(classroomlistmap));
                ((BaseAdapter) listview1.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                classroom.removeEventListener(_classroom_child_listener);
                classroomlistmap.clear();
                listview1.setAdapter(new Listview1Adapter(classroomlistmap));
                ((BaseAdapter) listview1.getAdapter()).notifyDataSetChanged();
                classroom.addChildEventListener(_classroom_child_listener);
            }

            @Override
            public void onChildMoved(DataSnapshot _param1, String _param2) {

            }

            @Override
            public void onChildRemoved(DataSnapshot _param1) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                classroom.removeEventListener(_classroom_child_listener);
                classroomlistmap.clear();
                listview1.setAdapter(new Listview1Adapter(classroomlistmap));
                ((BaseAdapter) listview1.getAdapter()).notifyDataSetChanged();
                classroom.addChildEventListener(_classroom_child_listener);
            }

            @Override
            public void onCancelled(DatabaseError _param1) {
                final int _errorCode = _param1.getCode();
                final String _errorMessage = _param1.getMessage();

            }
        };
        classroom.addChildEventListener(_classroom_child_listener);


        _users_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (file.getString("emailid", "").equals(_childValue.get("emailid").toString())) {
                    file.edit().putString("name", _childValue.get("name").toString()).commit();
                    file.edit().putString("rooms", _childValue.get("rooms").toString()).commit();
                    file.edit().putString("studid", _childValue.get("studid").toString()).commit();
                    file.edit().putString("course", _childValue.get("course").toString()).commit();
                    file.edit().putString("department", _childValue.get("department").toString()).commit();
                    file.edit().putString("user_uid", _childValue.get("user_uid").toString()).commit();
                    if(_childValue.get("profileurl").toString().equals(""))
                    {
                            profile.setImageResource(R.drawable.default_profile);
                        }else{
                            Glide.with(getApplicationContext()).load(_childValue.get("profileurl").toString()).into(profile);
                            //file.edit().putString("profileurl",_childValue.get("profileurl").toString()).commit();
                            file.edit().putString("profileurl",_childValue.get("profileurl").toString()).commit();
                    }

                    if(_childValue.get("user_uid").toString().equals(""))
                    {
                        map1.clear();
                        map1.put("user_uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        users.child(file.getString("studid","")).updateChildren(map1);
                        map1.clear();
                    }
                } else {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                users.removeEventListener(_users_child_listener);
                users.addChildEventListener(_users_child_listener);
            }

            @Override
            public void onChildMoved(DataSnapshot _param1, String _param2) {

            }

            @Override
            public void onChildRemoved(DataSnapshot _param1) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                users.removeEventListener(_users_child_listener);
                users.addChildEventListener(_users_child_listener);
            }

            @Override
            public void onCancelled(DatabaseError _param1) {
                final int _errorCode = _param1.getCode();
                final String _errorMessage = _param1.getMessage();

            }
        };
        users.addChildEventListener(_users_child_listener);

    }

    @SuppressLint("StaticFieldLeak")
    private void showNotificationImage()
    {
        new AsyncTask<String, Void, Bitmap>()
        {

            @Override
            protected Bitmap doInBackground(String... strings) {
                InputStream inputStream;
                try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    inputStream = connection.getInputStream();
                    return BitmapFactory.decodeStream(inputStream);
                }catch(Exception e)
                {

                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                showNotification(bitmap);
            }
        }.execute("https://www.infinityandroid.com/images/fasteners.jpg");
    }
    private void showNotification( Bitmap bitmap)
    {
        int notificationId = new Random().nextInt(100);
        String channelID = "notification_channel_2";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.drawable.work);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentTitle("Classroom");
        builder.setContentText("Hello Classroom");
//        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Haalo"));
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            if(notificationManager != null && notificationManager.getNotificationChannel(channelID) == null)
            {
                NotificationChannel notificationChannel = new NotificationChannel(channelID, "Notification Channel 1",NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("Description");
                notificationChannel.enableVibration(true);
                notificationChannel.enableLights(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        Notification notification = builder.build();
        if(notificationManager != null)
        {
            notificationManager.notify(notificationId, notification);
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
                _v = _inflater.inflate(R.layout.custom_classroom_class, null);
            }

            final LinearLayout linear1 = (LinearLayout) _v.findViewById(R.id.linear1);
            final LinearLayout linear2 = (LinearLayout) _v.findViewById(R.id.linear2);
            final TextView classroomby = (TextView) _v.findViewById(R.id.classroomby);
            final TextView totalstudents = (TextView) _v.findViewById(R.id.totalstudents);
            final TextView classroomname = (TextView) _v.findViewById(R.id.classroomname);
            final TextView course = (TextView) _v.findViewById(R.id.course);
            final ImageView unenroll = (ImageView) _v.findViewById(R.id.uneroll);
            final ImageView background = (ImageView) _v.findViewById(R.id.background);

            final String word = Character.toString(classroomlistmap.get((int)_position).get("roomid").toString().charAt(0));
            if(word.equals("a") || word.equals("s") || word.equals("d") || word.equals("1") || word.equals("2") || word.equals("3"))
            {
                background.setImageResource(R.drawable.back1);
            }
            if(word.equals("q") || word.equals("w") || word.equals("e") || word.equals("4") || word.equals("0") || word.equals("9"))
            {
                background.setImageResource(R.drawable.back2);
            }
            if(word.equals("z") || word.equals("x") || word.equals("c") || word.equals("f") || word.equals("g") || word.equals("h"))
            {
                background.setImageResource(R.drawable.back3);
            }
            if(word.equals("r") || word.equals("t") || word.equals("y") || word.equals("5") || word.equals("6") || word.equals("7"))
            {
                background.setImageResource(R.drawable.back4);
            }
            if(word.equals("v") || word.equals("b") || word.equals("n") || word.equals("m") || word.equals("j") || word.equals("8"))
            {
                background.setImageResource(R.drawable.back5);
            }
            if(word.equals("l") || word.equals("p") || word.equals("o") || word.equals("i") || word.equals("u") || word.equals("y"))
            {
                background.setImageResource(R.drawable.back6);
            }
            classroomname.setText(classroomlistmap.get((int)_position).get("classroomname").toString());

            classroomby.setText(classroomlistmap.get((int)_position).get("classroomby").toString().concat(" || ".concat(classroomlistmap.get((int)_position).get("owneremailid").toString())));
            course.setText(classroomlistmap.get((int)_position).get("course").toString());
            totalstudents.setText("Total Students : ".concat(classroomlistmap.get((int)_position).get("totalstudents").toString()));

            unenroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    PopupMenu popup = new PopupMenu(HomeActivity.this, unenroll);
                    Menu menu = popup.getMenu();
                    menu.add("Unenroll");
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                        @Override
                        public boolean onMenuItemClick(MenuItem item){
                            switch (item.getTitle().toString()) {
                                case "Unenroll":
                                    dialog2.setTitle("Unenroll");
                                    dialog2.setMessage("Do you want to uneroll " + classroomlistmap.get((int) _position).get("classroomname").toString() + " ?");
                                    dialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface _dialog, int _which) {

                                            String studentemails = classroomlistmap.get((int) _position).get("studentemails").toString().replace(file.getString("emailid", ""), "");
                                            String studentids = classroomlistmap.get((int) _position).get("studentids").toString().replace(file.getString("studid", ""), "");
                                            String studentnames = classroomlistmap.get((int) _position).get("studentnames").toString().replace(file.getString("username", ""), "");
                                            String rooms = file.getString("rooms","").replace(classroomlistmap.get((int) _position).get("roomid").toString(),"");

                                            map = new HashMap<>();
                                            map.put("studentemails", studentemails);
                                            map.put("studentids", studentids);
                                            map.put("totalstudents", String.valueOf((long) (Double.parseDouble(file.getString("totalstudents", "")) - 1)));
                                            map.put("studentnames", studentnames);
                                            classroom.child(classroomlistmap.get((int) _position).get("roomid").toString()).updateChildren(map);

                                            usermaps = new HashMap<>();
                                            usermaps.put("rooms", rooms);
                                            users.child(file.getString("studid", "")).updateChildren(usermaps);

                                            classroom.removeEventListener(_classroom_child_listener);
                                            classroomlistmap.clear();
                                            listview1.setAdapter(new Listview1Adapter(classroomlistmap));
                                            ((BaseAdapter) listview1.getAdapter()).notifyDataSetChanged();
                                            classroom.addChildEventListener(_classroom_child_listener);
                                        }
                                    });
                                    dialog2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface _dialog, int _which) {

                                        }
                                    });
                                    dialog2.create().show();
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });

            linear1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    file.edit().putString("roomid",classroomlistmap.get((int)_position).get("roomid").toString()).apply();
                    file.edit().putString("username",file.getString("name","")).apply();
                    file.edit().putString("emailid",file.getString("emailid","")).apply();
                    intent.setClass(getApplicationContext(),ClassroomClassActivity.class);
//                    startActivity(intent);

                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(background,"trans");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this,pairs);
                    startActivity(intent,options.toBundle());
//                    finish();
                }
            });
            return _v;
        }
    }
}