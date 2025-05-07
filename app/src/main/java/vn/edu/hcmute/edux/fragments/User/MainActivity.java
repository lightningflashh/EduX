package vn.edu.hcmute.edux.fragments.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.fragments.LoginActivity;
import vn.edu.hcmute.edux.services.StudentService;


public class MainActivity extends AppCompatActivity {
    private RecyclerView ListSubject, ListNotification;
    private vn.edu.hcmute.edux.Adapter.SubjectAdapter SubjectAdapter ;

    private vn.edu.hcmute.edux.Adapter.NotificationAdapter NotificationAdapter;

    Animation  botAnim,topAnim;
    private FirebaseAuth mAuth;
    private StudentService studentService;
    private TextView fullnameTextView, mssvTextView, khoaTextView;
    private ImageView profileImageView;
    private ConstraintLayout btnMenu, btnProfile,btnSetting,btnNotification,constraintNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        studentService = new StudentService();
        //ania
        botAnim = AnimationUtils.loadAnimation(this, R.anim.anim_up_btn);
        topAnim = AnimationUtils.loadAnimation(this, R.anim.anim_down_btn);

        // anh xa
        fullnameTextView = findViewById(R.id.fullname);
        mssvTextView = findViewById(R.id.tx_mssv);
        khoaTextView = findViewById(R.id.tx_khoa);
        profileImageView = findViewById(R.id.avatar);
        btnMenu = findViewById(R.id.constraint_ic_menu);
        btnProfile = findViewById(R.id.constraint_ic_profile);
        btnSetting = findViewById(R.id.constraint_ic_setting);
        btnNotification = findViewById(R.id.constraint_ic_notification);
        constraintNotification = findViewById(R.id.constrain_notification);
        // Anh xa list subject
        ListSubject = findViewById(R.id.list_subject);
        ListSubject.setLayoutManager(new GridLayoutManager(this, 1));

        ListSubject.setAdapter(SubjectAdapter);
        // Anh xa list  notification
        ListNotification = findViewById(R.id.list_notification);
        ListNotification.setLayoutManager(new GridLayoutManager(this, 1));

        ListNotification.setAdapter(NotificationAdapter);

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Fetch student data using StudentService
            fetchStudentData(currentUser.getUid());
        } else {
            // Redirect to LoginActivity if not logged in
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }


        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnProfile.getVisibility() == View.GONE && btnSetting.getVisibility() == View.GONE && btnNotification.getVisibility() == View.GONE) {
                    btnProfile.setVisibility(View.VISIBLE);
                    btnSetting.setVisibility(View.VISIBLE);
                    btnNotification.setVisibility(View.VISIBLE);

                    btnProfile.startAnimation(botAnim);
                    btnSetting.startAnimation(botAnim);
                    btnNotification.startAnimation(botAnim);
                } else {
                    btnProfile.setVisibility(View.GONE);
                    btnSetting.setVisibility(View.GONE);
                    btnNotification.setVisibility(View.GONE);
                    constraintNotification.setVisibility(View.GONE);

                }
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (constraintNotification.getVisibility() == View.GONE ){
                    constraintNotification.setVisibility(View.VISIBLE);
                    constraintNotification.startAnimation(botAnim);
                } else {
                    constraintNotification.setVisibility(View.GONE);
                }

            }
        });

    }

    private void fetchStudentData(String userId) {
        studentService.loadStudentById(userId,
                student -> {
                    // Update UI with student data
                    fullnameTextView.setText(student.getFullName() != null ? student.getFullName() : "Unknown");
                    mssvTextView.setText(student.getStudentCode() != null ? "Mssv: " + student.getStudentCode() : "Mssv: N/A");
                    khoaTextView.setText(student.getMajor() != null ? "Khoa: " + student.getMajor() : "Khoa: N/A");

                    if (student.getImageUrl() != null && !student.getImageUrl().isEmpty()) {
                        Glide.with(this)
                                .load(student.getImageUrl())
                                .into(profileImageView);
                    } else {
                        Glide.with(this)
                                .load(R.drawable.img_1) // Default image
                                .into(profileImageView);
                    }
                },
                error -> {
                    // Handle error (e.g., show toast or default values)
                    fullnameTextView.setText("Unknown");
                    mssvTextView.setText("Mssv: N/A");
                    khoaTextView.setText("Khoa: N/A");
                    Glide.with(this)
                            .load(R.drawable.img_1)
                            .into(profileImageView);

                });
    }




}