package vn.edu.hcmute.edux.activities.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.adapters.SubjectAdapter;
import vn.edu.hcmute.edux.activities.LoginActivity;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.services.StudentService;
import vn.edu.hcmute.edux.services.SubjectService;
import vn.edu.hcmute.edux.services.TeacherService;
import vn.edu.hcmute.edux.utils.UserSessionManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView listSubject, listNotification;
    private SubjectAdapter subjectAdapter;
    private vn.edu.hcmute.edux.adapters.NotificationAdapter notificationAdapter;
    private Animation botAnim, topAnim;
    private StudentService studentService;
    private TeacherService teacherService;
    private TextView fullnameTextView, mssvTextView, khoaTextView;
    private ImageView profileImageView;
    private ConstraintLayout btnMenu, btnProfile, btnSetting, btnNotification, constraintNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        studentService = new StudentService();
        teacherService = new TeacherService();

        initLayout();

        loadInfo();

        // Sự kiện nút menu
        btnMenu.setOnClickListener(v -> {
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
        });

        btnProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        btnSetting.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingActivity.class)));
        btnNotification.setOnClickListener(v -> {
            if (constraintNotification.getVisibility() == View.GONE) {
                constraintNotification.setVisibility(View.VISIBLE);
                constraintNotification.startAnimation(botAnim);
            } else {
                constraintNotification.setVisibility(View.GONE);
            }
        });
    }

    private void loadInfo() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Kiểm tra xem user có trong "teachers" hay "students"
            db.collection("teachers").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            if ("TEACHER".equals(role)) {
                                // Gọi hàm load teacher
                                UserSessionManager.saveSession(this, uid, "TEACHER");
                                fetchTeacherData(uid);
                                fetchTeacherSubjects(uid,"");
                            }
                        } else {
                            // Nếu không có trong teachers, kiểm tra students
                            db.collection("students").document(uid).get()
                                    .addOnSuccessListener(studentSnapshot -> {
                                        if (studentSnapshot.exists()) {
                                            String role = studentSnapshot.getString("role");
                                            if ("STUDENT".equals(role)) {
                                                UserSessionManager.saveSession(this, uid, "STUDENT");
                                                fetchStudentData(uid);
                                                fetchStudentSubjects(uid, "");
                                            }
                                        } else {
                                            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi kiểm tra quyền: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void initLayout() {
        botAnim = AnimationUtils.loadAnimation(this, R.anim.anim_up_btn);
        topAnim = AnimationUtils.loadAnimation(this, R.anim.anim_down_btn);

        // Khởi tạo views
        fullnameTextView = findViewById(R.id.fullname);
        mssvTextView = findViewById(R.id.tx_mssv);
        khoaTextView = findViewById(R.id.tx_khoa);
        profileImageView = findViewById(R.id.avatar);
        btnMenu = findViewById(R.id.constraint_ic_menu);
        btnProfile = findViewById(R.id.constraint_ic_profile);
        btnSetting = findViewById(R.id.constraint_ic_setting);
        btnNotification = findViewById(R.id.constraint_ic_notification);
        constraintNotification = findViewById(R.id.constrain_notification);

        // Khởi tạo RecyclerView cho danh sách môn học
        listSubject = findViewById(R.id.list_subject);
        listSubject.setLayoutManager(new GridLayoutManager(this, 1));
        subjectAdapter = new SubjectAdapter(new ArrayList<>());
        listSubject.setAdapter(subjectAdapter);

        // Thiết lập sự kiện cho SubjectAdapter
        subjectAdapter.setOnItemClickListener(subject -> {
            // Chuyển sang SubjectActivity và truyền className
            Intent intent = new Intent(MainActivity.this, SubjectActivity.class);
            intent.putExtra("name_subject", subject.getClassName());
            intent.putExtra("class_id", subject.getClassId()); // Truyền thêm classId nếu cần
            startActivity(intent);
        });
        subjectAdapter.setOnDeleteClickListener(subject -> {
            Toast.makeText(this, "Chức năng xóa không khả dụng cho sinh viên", Toast.LENGTH_SHORT).show();
        });

        // Khởi tạo RecyclerView cho thông báo
        listNotification = findViewById(R.id.list_notification);
        listNotification.setLayoutManager(new GridLayoutManager(this, 1));
        listNotification.setAdapter(notificationAdapter);
    }

    private void fetchStudentData(String userId) {
        studentService.loadStudentById(userId,
                student -> {
                    fullnameTextView.setText(student.getFullName() != null ? student.getFullName() : "Unknown");
                    mssvTextView.setText(student.getStudentCode() != null ? "Mssv: " + student.getStudentCode() : "Mssv: N/A");
                    khoaTextView.setText(student.getMajor() != null ? "Khoa: " + student.getMajor() : "Khoa: N/A");
                    if (student.getImageUrl() != null && !student.getImageUrl().isEmpty()) {
                        Glide.with(this).load(student.getImageUrl()).into(profileImageView);
                    } else {
                        Glide.with(this).load(R.drawable.img_1).into(profileImageView);
                    }
                },
                error -> {
                    Toast.makeText(this, "Lỗi khi tải thông tin sinh viên: " + error, Toast.LENGTH_LONG).show();
                    fullnameTextView.setText("Unknown");
                    mssvTextView.setText("Mssv: N/A");
                    khoaTextView.setText("Khoa: N/A");
                    Glide.with(this).load(R.drawable.img_1).into(profileImageView);
                });
    }

    private void fetchTeacherData(String userId) {
        teacherService.loadTeacherById(userId,
                teacher -> {
                    fullnameTextView.setText(teacher.getFullName() != null ? teacher.getFullName() : "Unknown");
                    khoaTextView.setText(teacher.getMajor() != null ? "Khoa: " + teacher.getMajor() : "Khoa: N/A");
                    if (teacher.getImageUrl() != null && !teacher.getImageUrl().isEmpty()) {
                        Glide.with(this).load(teacher.getImageUrl()).into(profileImageView);
                    } else {
                        Glide.with(this).load(R.drawable.img_1).into(profileImageView);
                    }
                },
                error -> {
                    Toast.makeText(this, "Lỗi khi tải thông tin gv: " + error, Toast.LENGTH_LONG).show();
                    fullnameTextView.setText("Unknown");
                    mssvTextView.setText("Mssv: N/A");
                    khoaTextView.setText("Khoa: N/A");
                    Glide.with(this).load(R.drawable.img_1).into(profileImageView);
                });
    }

    private void fetchStudentSubjects(String userId, String classNameFilter) {
        studentService.loadSubjects(userId, classNameFilter,
                subjects -> {
                    Log.d("MainActivity", "Số môn học tải được: " + subjects.size());
                    for (Subject subject : subjects) {
                        Log.d("MainActivity", "Môn học: " + subject.getClassName());
                    }
                    subjectAdapter.updateList(subjects);
                    if (subjects.isEmpty()) {
                        Toast.makeText(this, "Bạn chưa tham gia môn học nào", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("MainActivity", "Lỗi khi tải môn học: " + error.getMessage());
                    subjectAdapter.updateList(new ArrayList<>());
                    Toast.makeText(this, "Lỗi khi tải môn học: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void fetchTeacherSubjects(String userId, String classNameFilter) {
        teacherService.loadSubjects(userId, classNameFilter,
                subjects -> {
                    Log.d("MainActivity", "Số môn học tải được: " + subjects.size());
                    for (Subject subject : subjects) {
                        Log.d("MainActivity", "Môn học: " + subject.getClassName());
                    }
                    subjectAdapter.updateList(subjects);
                    if (subjects.isEmpty()) {
                        Toast.makeText(this, "Bạn chưa được phân công môn học nào", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("MainActivity", "Lỗi khi tải môn học: " + error.getMessage());
                    subjectAdapter.updateList(new ArrayList<>());
                    Toast.makeText(this, "Lỗi khi tải môn học: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}