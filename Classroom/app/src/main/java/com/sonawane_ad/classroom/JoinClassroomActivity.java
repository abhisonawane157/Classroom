package com.sonawane_ad.classroom;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class JoinClassroomActivity extends AppCompatActivity {


    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();

    private HashMap<String, Object> classroommap = new HashMap<>();
    private double n = 0;
    private int e = 0;
    private HashMap<String, Object> usermap = new HashMap<>();
    private String imagelink= "";
    private String roomid = "";
    private ArrayList<HashMap<String, Object>> classroomlistmap = new ArrayList<>();

    private LinearLayout linear1;
    private EditText edittext1;
    private Button button1;

    private DatabaseReference classroom = _firebase.getReference("classroom");
    private ChildEventListener _classroom_child_listener;
    private Intent intent = new Intent();
    private SharedPreferences file;
    private DatabaseReference users = _firebase.getReference("users");
    private ChildEventListener _users_child_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_classroom);
        n = 0;
        edittext1 = (EditText) findViewById(R.id.edittext1);
        button1 = (Button) findViewById(R.id.button1);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);

        edittext1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                classroom.removeEventListener(_classroom_child_listener);
                classroom.addChildEventListener(_classroom_child_listener);
                roomid = edittext1.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                classroom.removeEventListener(_classroom_child_listener);
                classroom.addChildEventListener(_classroom_child_listener);
                roomid = edittext1.getText().toString();

                final String word = Character.toString(edittext1.getText().toString().charAt(0));
                if(word.equals("a") || word.equals("s") || word.equals("d") || word.equals("1") || word.equals("2") || word.equals("3"))
                {
//                    background.setImageResource(R.drawable.back1);
                    imagelink = "https://images.wallpaperscraft.com/image/net_color_background_dark_85551_800x600.jpg";
                }
                if(word.equals("q") || word.equals("w") || word.equals("e") || word.equals("4") || word.equals("0") || word.equals("9"))
                {
//                    background.setImageResource(R.drawable.back2);
                    imagelink = "https://images.wallpaperscraft.com/image/net_texture_multicolored_154324_800x600.jpg";
                }
                if(word.equals("z") || word.equals("x") || word.equals("c") || word.equals("f") || word.equals("g") || word.equals("h"))
                {
//                    background.setImageResource(R.drawable.back3);
                    imagelink = "https://images.wallpaperscraft.com/image/shapes_pattern_abstraction_167348_800x600.jpg";
                }
                if(word.equals("r") || word.equals("t") || word.equals("y") || word.equals("5") || word.equals("6") || word.equals("7"))
                {
//                    background.setImageResource(R.drawable.back4);
                    imagelink = "https://images.wallpaperscraft.com/image/background_paint_stains_light_76087_800x600.jpg";
                }
                if(word.equals("v") || word.equals("b") || word.equals("n") || word.equals("m") || word.equals("j") || word.equals("8"))
                {
//                    background.setImageResource(R.drawable.back5);
                    imagelink = "https://images.wallpaperscraft.com/image/background_blemishes_dark_91678_800x600.jpg";
                }
                if(word.equals("l") || word.equals("p") || word.equals("o") || word.equals("i") || word.equals("u") || word.equals("y"))
                {
//                    background.setImageResource(R.drawable.back6);
                    imagelink = "https://images.wallpaperscraft.com/image/lines_circles_stripes_rotation_118127_800x600.jpg";
                }
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (edittext1.getText().toString().trim().equals("")) {
                    Toast.makeText(JoinClassroomActivity.this, "Enter Valid Room ID", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (n == 1) {
                        if( e== 0) {
                            classroommap = new HashMap<>();
                            classroommap.put("studentnames", file.getString("studentnames", "").concat(" , ".concat(file.getString("name", ""))));
                            classroommap.put("totalstudents", String.valueOf((long) (Double.parseDouble(file.getString("totalstudents", "")) + 1)));
                            classroommap.put("studentemails", file.getString("studentemails", "").concat(" , ".concat(file.getString("emailid", ""))));
                            classroommap.put("studentids", file.getString("studentids", "").concat(" , ".concat(file.getString("studid", ""))));
                            classroom.child(edittext1.getText().toString().trim()).updateChildren(classroommap);
                            usermap = new HashMap<>();
                            usermap.put("rooms", file.getString("rooms", "").concat(" , ".concat(edittext1.getText().toString().trim())));
                            users.child(file.getString("studid", "")).updateChildren(usermap);
                            intent.setClass(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            showNotificationImage();
                            finish();
                        }else{
                            Toast.makeText(JoinClassroomActivity.this, "Already a member of Classroom", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    else {
                        Toast.makeText(JoinClassroomActivity.this, "No Classroom Found.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        _classroom_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (edittext1.getText().toString().trim().equals(_childValue.get("roomid").toString().trim())) {
                    file.edit().putString("studentnames", _childValue.get("studentnames").toString()).commit();
                    file.edit().putString("totalstudents", _childValue.get("totalstudents").toString()).commit();
                    file.edit().putString("studentids", _childValue.get("studentids").toString()).commit();
                    file.edit().putString("studentemails", _childValue.get("studentemails").toString()).commit();
                    file.edit().putString("classroomname", _childValue.get("classroomname").toString()).commit();
                    n = 1;
                    if((_childValue.get("studentids").toString()).contains(file.getString("studid","")))
                    {
                        e = 1;
                    }else{
                        e = 0;
                    }
                }
                else {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                classroom.removeEventListener(_classroom_child_listener);
                n = 0;
                e = 0;
                classroom.addChildEventListener(_classroom_child_listener);
            }

            @Override
            public void onChildMoved(DataSnapshot _param1, String _param2) {

            }

            @Override
            public void onChildRemoved(DataSnapshot _param1) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                classroom.removeEventListener(_classroom_child_listener);
                n = 0;
                e = 0;
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
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (_childValue.get("emailid").toString().equals(file.getString("emailid", ""))) {
                    file.edit().putString("name", _childValue.get("name").toString()).commit();
                    file.edit().putString("studid", _childValue.get("studid").toString()).commit();
                    file.edit().putString("rooms", _childValue.get("rooms").toString()).commit();
                }
                else {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (_childValue.get("emailid").toString().equals(file.getString("emailid", ""))) {
                    file.edit().putString("name", _childValue.get("name").toString()).commit();
                    file.edit().putString("studid", _childValue.get("studid").toString()).commit();
                    file.edit().putString("rooms", _childValue.get("rooms").toString()).commit();
                }
                else {

                }
            }

            @Override
            public void onChildMoved(DataSnapshot _param1, String _param2) {

            }

            @Override
            public void onChildRemoved(DataSnapshot _param1) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (_childValue.get("emailid").toString().equals(file.getString("emailid", ""))) {
                    file.edit().putString("name", _childValue.get("name").toString()).commit();
                    file.edit().putString("studid", _childValue.get("studid").toString()).commit();
                    file.edit().putString("rooms", _childValue.get("rooms").toString()).commit();
                }
                else {

                }
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
        }.execute(imagelink);
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
        builder.setContentText("Welcome!, "+file.getString("name", "")+" in "+file.getString("classroomname", "")+" Classroom");
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

}