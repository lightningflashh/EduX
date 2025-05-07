package vn.edu.hcmute.edux.fragments.Admin;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import vn.edu.hcmute.edux.Adapter.TeacherAdapter;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.EUserRole;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.model.Teacher;


public class ManageTeacherActivity extends AppCompatActivity {

    private ImageView fabAddTeacher, btnBack;
    private RecyclerView rvTeachers;
    private TextView tvTeacherCount;
    private EditText etSearch;
    private TeacherAdapter teacherAdapter;
    private List<Teacher> teacherList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_manage_teacher);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các view từ layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        fabAddTeacher = findViewById(R.id.fabAddTeacher);
        btnBack = findViewById(R.id.btn_Back);
        rvTeachers = findViewById(R.id.rvTeachers);
        tvTeacherCount = findViewById(R.id.tvTeacherCount);
        etSearch = findViewById(R.id.etSearch);

        // Thiết lập Toolbar
        setSupportActionBar(toolbar);

        // Khởi tạo danh sách giảng viên và adapter
        teacherList = new ArrayList<>();
        teacherAdapter = new TeacherAdapter(teacherList);
        rvTeachers.setLayoutManager(new LinearLayoutManager(this));
        rvTeachers.setAdapter(teacherAdapter);

        // Tải danh sách giảng viên từ Firestore
        loadTeachers();

        // Xử lý nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý sự kiện khi nhấn nút Thêm Giảng Viên
        fabAddTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageTeacherActivity.this, AddTeacherActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                teacherAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Tải danh sách giảng viên từ Firestore
    private void loadTeachers() {
        Log.d("ManageTeacherActivity", "Bắt đầu tải danh sách giảng viên...");
        db.collection("teachers")
                .whereEqualTo("role", EUserRole.TEACHER.toString())
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("ManageTeacherActivity", "Lỗi khi lắng nghe dữ liệu: " + e.getMessage());
                        Toast.makeText(ManageTeacherActivity.this, "Lỗi khi tải danh sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        tvTeacherCount.setText("(0)");
                        return;
                    }

                    if (snapshot != null) {
                        // Tạo danh sách tạm thời
                        List<Teacher> tempList = new ArrayList<>();
                        Log.d("ManageTeacherActivity", "Tải dữ liệu thành công, số lượng tài liệu: " + snapshot.size());
                        for (QueryDocumentSnapshot document : snapshot) {
                            Map<String, Object> data = document.getData();
                            Log.d("ManageTeacherActivity", "Dữ liệu giảng viên: " + data.toString());

                            // Chuyển đổi Timestamp thành Date
                            Date birthDate = data.get("birthDate") instanceof Timestamp ?
                                    ((Timestamp) data.get("birthDate")).toDate() : null;
                            Date createdAt = data.get("createdAt") instanceof Timestamp ?
                                    ((Timestamp) data.get("createdAt")).toDate() : null;
                            Date updatedAt = data.get("updatedAt") instanceof Timestamp ?
                                    ((Timestamp) data.get("updatedAt")).toDate() : null;

                            try {
                                Teacher teacher = new Teacher(
                                        (String) data.get("userID"),
                                        (String) data.get("fullName"),
                                        (String) data.get("email"),
                                        (String) data.get("password"),
                                        EUserRole.valueOf((String) data.get("role")),
                                        (String) data.get("imageUrl"),
                                        birthDate,
                                        (Boolean) data.get("isActive"),
                                        createdAt,
                                        updatedAt,
                                        (String) data.get("degree"),
                                        (List<Subject>) data.get("teachingClasses"),
                                        (String) data.get("major")
                                );
                                tempList.add(teacher);
                                Log.d("ManageTeacherActivity", "Đã thêm giảng viên vào danh sách tạm: " + teacher.getFullName());
                            } catch (Exception ex) {
                                Log.e("ManageTeacherActivity", "Lỗi khi ánh xạ giảng viên: " + ex.getMessage());
                            }
                        }

                        // Log danh sách tạm để kiểm tra
                        Log.d("ManageTeacherActivity", "Danh sách tạm có " + tempList.size() + " giảng viên");
                        for (Teacher teacher : tempList) {
                            Log.d("ManageTeacherActivity", "Giảng viên trong danh sách tạm: " + teacher.getFullName());
                        }

                        // Cập nhật teacherList và adapter
                        teacherList = new ArrayList<>(tempList); // Tạo bản sao mới
                        teacherAdapter.updateList(teacherList);
                        tvTeacherCount.setText("(" + teacherList.size() + ")");
                        Log.d("ManageTeacherActivity", "Cập nhật danh sách hoàn tất, số giảng viên: " + teacherList.size());
                    } else {
                        Log.e("ManageTeacherActivity", "Snapshot null");
                        tvTeacherCount.setText("(0)");
                    }
                });
    }
}
