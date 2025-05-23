package vn.edu.hcmute.edux.activities.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.activities.LoginActivity;
import vn.edu.hcmute.edux.adapters.NotificationAdapter;
import vn.edu.hcmute.edux.adapters.SubjectAdapter;
import vn.edu.hcmute.edux.controllers.user.MainController;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.model.Teacher;
import vn.edu.hcmute.edux.views.iMainView;

public class MainActivity extends AppCompatActivity implements iMainView {

    private MainController controller;
    private SubjectAdapter subjectAdapter;
    private NotificationAdapter notificationAdapter;

    private RecyclerView listSubject, listNotification;
    private TextView fullnameTextView, mssvTextView, khoaTextView;
    private ImageView profileImageView;
    private ConstraintLayout btnMenu, btnProfile, btnSetting, btnNotification, constraintNotification;
    private Animation botAnim, topAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initLayout();
        controller = new MainController(this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            controller.loadUserInfo(currentUser.getUid());
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void initLayout() {
        botAnim = AnimationUtils.loadAnimation(this, R.anim.anim_up_btn);
        topAnim = AnimationUtils.loadAnimation(this, R.anim.anim_down_btn);

        fullnameTextView = findViewById(R.id.fullname);
        mssvTextView = findViewById(R.id.tx_mssv);
        khoaTextView = findViewById(R.id.tx_khoa);
        profileImageView = findViewById(R.id.avatar);

        btnMenu = findViewById(R.id.constraint_ic_menu);
        btnProfile = findViewById(R.id.constraint_ic_profile);
        btnSetting = findViewById(R.id.constraint_ic_setting);
        btnNotification = findViewById(R.id.constraint_ic_notification);
        constraintNotification = findViewById(R.id.constrain_notification);

        listSubject = findViewById(R.id.list_subject);
        listSubject.setLayoutManager(new GridLayoutManager(this, 1));
        subjectAdapter = new SubjectAdapter(new ArrayList<>());
        listSubject.setAdapter(subjectAdapter);

        listNotification = findViewById(R.id.list_notification);
        listNotification.setLayoutManager(new GridLayoutManager(this, 1));
//        notificationAdapter = new NotificationAdapter(new ArrayList<>());
        listNotification.setAdapter(notificationAdapter);

        btnMenu.setOnClickListener(v -> toggleMenuVisibility());
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnSetting.setOnClickListener(v -> startActivity(new Intent(this, SettingActivity.class)));
        btnNotification.setOnClickListener(v -> toggleNotificationVisibility());

        subjectAdapter.setOnItemClickListener(subject -> {
            Intent intent = new Intent(this, SubjectActivity.class);
            intent.putExtra("name_subject", subject.getClassName());
            intent.putExtra("class_id", subject.getClassId());
            startActivity(intent);
        });

        subjectAdapter.setOnDeleteClickListener(subject -> {
            Toast.makeText(this, "Chức năng xóa không khả dụng cho sinh viên", Toast.LENGTH_SHORT).show();
        });
    }

    private void toggleMenuVisibility() {
        boolean hidden = btnProfile.getVisibility() == View.GONE;
        btnProfile.setVisibility(hidden ? View.VISIBLE : View.GONE);
        btnSetting.setVisibility(hidden ? View.VISIBLE : View.GONE);
        btnNotification.setVisibility(hidden ? View.VISIBLE : View.GONE);

        if (!hidden) constraintNotification.setVisibility(View.GONE);
        else {
            btnProfile.startAnimation(botAnim);
            btnSetting.startAnimation(botAnim);
            btnNotification.startAnimation(botAnim);
        }
    }

    private void toggleNotificationVisibility() {
        boolean hidden = constraintNotification.getVisibility() == View.GONE;
        constraintNotification.setVisibility(hidden ? View.VISIBLE : View.GONE);
        if (hidden) constraintNotification.startAnimation(botAnim);
    }

    @Override
    public void displayStudent(Student student) {
        fullnameTextView.setText(student.getFullName());
        mssvTextView.setText("Mssv: " + student.getStudentCode());
        khoaTextView.setText("Khoa: " + student.getMajor());
        Glide.with(this).load(student.getImageUrl()).into(profileImageView);
    }

    @Override
    public void displayTeacher(Teacher teacher) {
        fullnameTextView.setText(teacher.getFullName());
        mssvTextView.setText("");
        khoaTextView.setText("Khoa: " + teacher.getMajor());
        Glide.with(this).load(teacher.getImageUrl()).into(profileImageView);
    }

    @Override
    public void updateSubjectList(List<Subject> subjects) {
        subjectAdapter.updateList(subjects);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
