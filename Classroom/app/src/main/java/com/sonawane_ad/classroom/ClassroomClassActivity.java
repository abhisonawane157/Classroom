package com.sonawane_ad.classroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class ClassroomClassActivity extends AppCompatActivity {

    private ImageView  unenroll, bot1, bot2, bot3, back;

    private TextView classname, classroomname, subject;
    private ListView teachers, students, classroomlist;
    private ImageView background;

    private CardView add_classwork;

    private LinearLayout classroomll, peoplell, toolbar, addwork;
    private String ownerid = NULL;
    private String profileurl = NULL;


    private String chatcopy = "";

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();
    private Intent intent = new Intent();
    private SharedPreferences file;
    private AlertDialog.Builder dialog;

    public final int REQ_CD_CAMERA = 101;
    private HashMap<String, Object> map1 = new HashMap<>();
    private HashMap<String, Object> map3 = new HashMap<>();
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
        setContentView(R.layout.activity_classroom_class);
        com.google.firebase.FirebaseApp.initializeApp(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        } else {

        }
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        dialog = new AlertDialog.Builder(this);
        _file_camera = FileUtil.createNewPictureFile(getApplicationContext());
        Uri _uri_camera = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            _uri_camera = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", _file_camera);
//        } else {
            _uri_camera = Uri.fromFile(_file_camera);
        }
        camera.putExtra(MediaStore.EXTRA_OUTPUT, _uri_camera);
        camera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        background = findViewById(R.id.background);
        back = findViewById(R.id.back);
        toolbar = findViewById(R.id.toolbar1);
        unenroll = findViewById(R.id.uneroll);
        bot1 = findViewById(R.id.bot1);
        //bot2 = findViewById(R.id.bot2);
        bot3 = findViewById(R.id.bot3);
        classroomll = findViewById(R.id.classroom);
        peoplell = findViewById(R.id.people);
        add_classwork = findViewById(R.id.q);

        classname = findViewById(R.id.classname);
        classroomname = findViewById(R.id.classroomname);
        subject = findViewById(R.id.classroomsub);

        classroomlist = findViewById(R.id.listview);//Classwork
        teachers = findViewById(R.id.listview1);//teachers
        students = findViewById(R.id.listview2);//people
        addwork = findViewById(R.id.addwork);//Add Work Linear



        add_classwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(getApplicationContext(),AddClassworkActivity.class);
                startActivity(intent);
            }
        });

        bot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classroomll.setVisibility(View.VISIBLE);
                peoplell.setVisibility(View.GONE);
            }
        });
//        bot2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                classroomll.setVisibility(View.VISIBLE);
//                peoplell.setVisibility(View.GONE);
//            }
//        });
        bot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classroomll.setVisibility(View.GONE);
                peoplell.setVisibility(View.VISIBLE);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intent.setClass(getApplicationContext(),HomeActivity.class);
//                startActivity(intent);
                finish();
            }
        });



        _classroom_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (_childValue.get("roomid").toString().contains(file.getString("roomid", "")))
                {
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
                    map.put("subject", _childValue.get("subject").toString());
                    map.put("ownerid", _childValue.get("ownerid").toString());
                    map.put("totalstudents", _childValue.get("totalstudents").toString());
                    teacherlistmap.add(map);

                    if(file.getString("emailid","").equals(_childValue.get("owneremailid").toString()))
                    {
                        add_classwork.setVisibility(View.VISIBLE);
                    }
                    
                    String word = Character.toString(_childValue.get("roomid").toString().charAt(0));
                    if(word.equals("a") || word.equals("s") || word.equals("d") || word.equals("1") || word.equals("2") || word.equals("3"))
                    {
                        background.setImageResource(R.drawable.back1);
                        toolbar.setBackgroundResource(R.drawable.back1);
                    }
                    if(word.equals("q") || word.equals("w") || word.equals("e") || word.equals("4") || word.equals("0") || word.equals("9"))
                    {
                        background.setImageResource(R.drawable.back2);
                        toolbar.setBackgroundResource(R.drawable.back2);
                    }
                    if(word.equals("z") || word.equals("x") || word.equals("c") || word.equals("f") || word.equals("g") || word.equals("h"))
                    {
                        background.setImageResource(R.drawable.back3);
                        toolbar.setBackgroundResource(R.drawable.back3);
                    }
                    if(word.equals("r") || word.equals("t") || word.equals("y") || word.equals("5") || word.equals("6") || word.equals("7"))
                    {
                        background.setImageResource(R.drawable.back4);
                        toolbar.setBackgroundResource(R.drawable.back4);
                    }
                    if(word.equals("v") || word.equals("b") || word.equals("n") || word.equals("m") || word.equals("j") || word.equals("8"))
                    {
                        background.setImageResource(R.drawable.back5);
                        toolbar.setBackgroundResource(R.drawable.back5);
                    }
                    if(word.equals("l") || word.equals("p") || word.equals("o") || word.equals("i") || word.equals("u") || word.equals("y"))
                    {
                        background.setImageResource(R.drawable.back6);
                        toolbar.setBackgroundResource(R.drawable.back6);
                    }

                    subject.setText(_childValue.get("subject").toString());
                    classname.setText(_childValue.get("classroomname").toString());
                    classroomname.setText(_childValue.get("classroomname").toString());
                    ownerid = _childValue.get("ownerid").toString();
                } else {

                }
                teachers.setAdapter(new Listview1Teacher(teacherlistmap));
                ((BaseAdapter) teachers.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                classroom.removeEventListener(_classroom_child_listener);
                teacherlistmap.clear();
                teachers.setAdapter(new Listview1Teacher(teacherlistmap));
                ((BaseAdapter) teachers.getAdapter()).notifyDataSetChanged();
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
                teacherlistmap.clear();
                teachers.setAdapter(new Listview1Teacher(teacherlistmap));
                ((BaseAdapter) teachers.getAdapter()).notifyDataSetChanged();
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
                if ((_childValue.get("rooms").toString().contains(file.getString("roomid", ""))))
                {
                    map1 = new HashMap<>();
                    map1.put("profileurl",_childValue.get("profileurl").toString());
                    map1.put("rollnumber", _childValue.get("rollnumber").toString());
                    map1.put("name", _childValue.get("name").toString());
                    map1.put("emailid", _childValue.get("emailid").toString());
                    map1.put("user_uid",_childValue.get("user_uid").toString());
                    userlistmap.add(map1);
                }
                if(_childValue.get("user_uid").toString().equals(""))
                {
                    map1.clear();
                    map1.put("user_uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    users.child(file.getString("studid","")).updateChildren(map1);
                    map1.clear();
                }

                if(ownerid.equals(""))
                {

                }else{
                    if(_childValue.get("studid").toString().equals(ownerid))
                    {
                        profileurl = _childValue.get("profileurl").toString();
                    }
                }
                students.setAdapter(new Listview1User(userlistmap));
                ((BaseAdapter) students.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                users.removeEventListener(_users_child_listener);
                userlistmap.clear();
                students.setAdapter(new Listview1User(userlistmap));
                ((BaseAdapter) students.getAdapter()).notifyDataSetChanged();
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
                userlistmap.clear();
                students.setAdapter(new Listview1User(userlistmap));
                ((BaseAdapter) students.getAdapter()).notifyDataSetChanged();
                users.addChildEventListener(_users_child_listener);
            }

            @Override
            public void onCancelled(DatabaseError _param1) {
                final int _errorCode = _param1.getCode();
                final String _errorMessage = _param1.getMessage();

            }
        };
        users.addChildEventListener(_users_child_listener);

        _classwork_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

                if (_childValue.get("roomid").toString().contains(file.getString("roomid", "")))
                {
                    map3 = new HashMap<>();
                    map3.put("classworkname", _childValue.get("classworkname").toString());
                    map3.put("classworkid", _childValue.get("classworkid").toString());
                    map3.put("location", _childValue.get("location").toString());
                    map3.put("roomid", _childValue.get("roomid").toString());
                    map3.put("subject", _childValue.get("subject").toString());
                    map3.put("user_uid", _childValue.get("user_uid").toString());
                    classworklistmap.add(map3);

//                    subject.setText(_childValue.get("subject").toString());
//                    classname.setText(_childValue.get("classroomname").toString());
//                    classroomname.setText(_childValue.get("classroomname").toString());
//                    ownerid = _childValue.get("ownerid").toString();
                } else {

                }
                classroomlist.setAdapter(new Listview1Classwork(classworklistmap));
                ((BaseAdapter) classroomlist.getAdapter()).notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                classwork.removeEventListener(_classwork_child_listener);
                classworklistmap.clear();
                classroomlist.setAdapter(new Listview1User(classworklistmap));
                ((BaseAdapter) classroomlist.getAdapter()).notifyDataSetChanged();
                users.addChildEventListener(_classwork_child_listener);
            }

            @Override
            public void onChildRemoved(DataSnapshot _param1) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                classwork.removeEventListener(_classwork_child_listener);
                classworklistmap.clear();
                classroomlist.setAdapter(new Listview1User(classworklistmap));
                ((BaseAdapter) classroomlist.getAdapter()).notifyDataSetChanged();
                users.addChildEventListener(_classwork_child_listener);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError _param1) {
                final int _errorCode = _param1.getCode();
                final String _errorMessage = _param1.getMessage();
            }
        };

        classwork.removeEventListener(_classwork_child_listener);
        chatcopy = "classwork/".concat(file.getString("roomid",""));
        classwork = _firebase.getReference(chatcopy);
        classwork.addChildEventListener(_classwork_child_listener);
    }

    public class Listview1Teacher extends BaseAdapter {
        ArrayList<HashMap<String, Object>> _data;
        public Listview1Teacher(ArrayList<HashMap<String, Object>> _arr) {
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
                _v = _inflater.inflate(R.layout.custom_teacher, null);
            }

            final TextView teachersname = (TextView) _v.findViewById(R.id.teachername);
            final ImageView profile =  _v.findViewById(R.id.imageview1);
            final ImageView mail = (ImageView) _v.findViewById(R.id.mail);

            teachersname.setText(teacherlistmap.get((int)_position).get("classroomby").toString());

            mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",teacherlistmap.get((int)_position).get("owneremailid").toString(), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Classroom");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            });
            Glide.with(getApplicationContext()).load(Uri.parse(profileurl)).into(profile);

            return _v;
        }
    }


    public class Listview1User extends BaseAdapter {
        ArrayList<HashMap<String, Object>> _data;
        public Listview1User(ArrayList<HashMap<String, Object>> _arr) {
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
                _v = _inflater.inflate(R.layout.custom_teacher, null);
            }

            final TextView teachersname = (TextView) _v.findViewById(R.id.teachername);
            final ImageView profile = (ImageView) _v.findViewById(R.id.imageview1);
            final ImageView mail = (ImageView) _v.findViewById(R.id.mail);

            teachersname.setText(userlistmap.get((int)_position).get("name").toString());

            Log.e("emilid", userlistmap.get((int)_position).get("emailid").toString());
            mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",userlistmap.get((int)_position).get("emailid").toString(), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Classroom");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            });
            if(userlistmap.get((int)_position).get("profileurl").toString().equals(""))
            {
                profile.setImageResource(R.drawable.default_profile);
            }else{
                Glide.with(getApplicationContext()).load(Uri.parse(userlistmap.get((int)_position).get("profileurl").toString())).into(profile);
            }
            return _v;
        }
    }

    public class Listview1Classwork extends BaseAdapter {
        ArrayList<HashMap<String, Object>> _data;
        public Listview1Classwork(ArrayList<HashMap<String, Object>> _arr) {
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
                _v = _inflater.inflate(R.layout.custom_classwork, null);
            }

            final CardView UI_cardview2 = _v.findViewById(R.id.UI_cardview2);
            final TextView classworkname = (TextView) _v.findViewById(R.id.classworkname);
            final ImageView background = (ImageView) _v.findViewById(R.id.background);
            final CardView q = (CardView) _v.findViewById(R.id.q);

            classworkname.setText(classworklistmap.get((int)_position).get("classworkname").toString());

            String word = Character.toString(classworklistmap.get((int)_position).get("classworkid").toString().charAt(0));
            String flag = "0";
            if(word.equals("a") || word.equals("s") || word.equals("d") || word.equals("1") || word.equals("2") || word.equals("3"))
            {
                flag = "1";
                background.setImageResource(R.drawable.work1);
            }
            if(word.equals("q") || word.equals("w") || word.equals("e") || word.equals("4") || word.equals("0") || word.equals("9"))
            {
                flag = "2";
                background.setImageResource(R.drawable.work2);
            }
            if(word.equals("z") || word.equals("x") || word.equals("c") || word.equals("f") || word.equals("g") || word.equals("h"))
            {
                flag = "3";
                background.setImageResource(R.drawable.work3);
            }
            if(word.equals("r") || word.equals("t") || word.equals("y") || word.equals("5") || word.equals("6") || word.equals("7"))
            {
                flag = "4";
                background.setImageResource(R.drawable.work4);
            }
            if(word.equals("v") || word.equals("b") || word.equals("n") || word.equals("m") || word.equals("j") || word.equals("8"))
            {
                flag = "5";
                background.setImageResource(R.drawable.work5);
            }
            if(word.equals("l") || word.equals("p") || word.equals("o") || word.equals("i") || word.equals("u") || word.equals("k"))
            {
                flag = "6";
                background.setImageResource(R.drawable.work6);
            }

            String finalFlag = flag;
            UI_cardview2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    file.edit().putString("classworkid",classworklistmap.get((int)_position).get("classworkid").toString()).commit();
                    file.edit().putString("classworkname",classworklistmap.get((int)_position).get("classworkname").toString()).commit();
                    file.edit().putString("img",finalFlag).commit();

                    intent.setClass(getApplicationContext(),ChatRoomActivity.class);
                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(UI_cardview2,"imageview1Trans");
                    pairs[1] = new Pair<View, String>(classworkname,"txt1Trans");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ClassroomClassActivity.this,pairs);
                    startActivity(intent,options.toBundle());
                }
            });
            return _v;
        }
    }
}