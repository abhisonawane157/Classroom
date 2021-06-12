package com.sonawane_ad.classroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.Random;

public class CreateClassroomActivity extends AppCompatActivity {

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();

    private HashMap<String, Object> classroommap = new HashMap<>();

    private LinearLayout linear1;
    private EditText classroomname;
    private EditText department;
    private EditText course;
    private EditText subject;
    private EditText roomid;
    private TextView textview1;
    private Button button1;

    private DatabaseReference classroom = _firebase.getReference("classroom");
    private ChildEventListener _classroom_child_listener;
    private SharedPreferences file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_classroom);
        classroomname = (EditText) findViewById(R.id.classroomname);
        department = (EditText) findViewById(R.id.department);
        course = (EditText) findViewById(R.id.course);
        subject = (EditText) findViewById(R.id.subject);
        roomid = (EditText) findViewById(R.id.roomid);
        textview1 = (TextView) findViewById(R.id.textview1);
        button1 = (Button) findViewById(R.id.button1);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);

        int i = 8;
        final String randomText = "1234567890qwertyuiopasdfghjklzxcvbnm";
        StringBuilder result = new StringBuilder();
        while (i > 0) {
            Random random = new Random();
            result.append(randomText.charAt(random.nextInt(randomText.length())));
            i--;
        }

        roomid.setText(result.toString());


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (!(classroomname.getText().toString().trim().equals("") && (department.getText().toString().trim().equals("") && (course.getText().toString().trim().equals("") && (subject.getText().toString().trim().equals("") && roomid.getText().toString().trim().equals("")))))) {
                    classroommap = new HashMap<>();
                    classroommap.put("classroomname", classroomname.getText().toString());
                    classroommap.put("classroomby", file.getString("name", ""));
                    classroommap.put("owneremailid", file.getString("emailid", ""));
                    classroommap.put("department", department.getText().toString());
                    classroommap.put("course", course.getText().toString());
                    classroommap.put("subject", subject.getText().toString());
                    classroommap.put("roomid", roomid.getText().toString());
                    classroommap.put("ownerid",file.getString("studid", ""));
                    classroommap.put("studentnames", "");
                    classroommap.put("totalstudents", "0");
                    classroommap.put("studentemails", "");
                    classroommap.put("studentids", "");
                    classroom.child(roomid.getText().toString()).updateChildren(classroommap);
                    finish();
                }
                else {
                    Toast.makeText(CreateClassroomActivity.this, "Enter Valid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        _classroom_child_listener = new ChildEventListener() {
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
        classroom.addChildEventListener(_classroom_child_listener);
    }
}