package vn.edu.hcmute.edux.activities.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.adapters.TeacherAdapter;
import vn.edu.hcmute.edux.controllers.admin.ManageTeacherController;
import vn.edu.hcmute.edux.model.Teacher;

public class ManageTeacherActivity extends AppCompatActivity {

    private ImageView fabAddTeacher, btnBack;
    private RecyclerView rvTeachers;
    private TextView tvTeacherCount;
    private EditText etSearch;
    private TeacherAdapter teacherAdapter;
    private List<Teacher> teacherList;
    private ManageTeacherController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_teacher);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khởi tạo Controller
        controller = new ManageTeacherController(this);

        // Ánh xạ các view từ layout
        fabAddTeacher = findViewById(R.id.fabAddTeacher);
        btnBack = findViewById(R.id.btn_Back);
        rvTeachers = findViewById(R.id.rvTeachers);
        tvTeacherCount = findViewById(R.id.tvTeacherCount);
        etSearch = findViewById(R.id.etSearch);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Khởi tạo danh sách giảng viên và adapter
        teacherList = new ArrayList<>();
        teacherAdapter = new TeacherAdapter(teacherList);
        rvTeachers.setLayoutManager(new LinearLayoutManager(this));
        rvTeachers.setAdapter(teacherAdapter);

        // Xử lý sự kiện nhấn vào item để xem chi tiết
        teacherAdapter.setOnItemClickListener(teacher -> {
            Intent intent = new Intent(ManageTeacherActivity.this, TeacherDetailActivity.class);
            intent.putExtra("TEACHER_ID", teacher.getUserID());
            startActivity(intent);
        });

        // Tải danh sách giảng viên từ Controller
        controller.loadTeachers();

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện khi nhấn nút Thêm Giảng Viên
        fabAddTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(ManageTeacherActivity.this, AddTeacherActivity.class);
            startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        controller.loadTeachers();
    }

    // Phương thức để Controller cập nhật giao diện
    public void updateTeacherList(List<Teacher> teachers) {
        teacherList.clear();
        teacherList.addAll(teachers);
        teacherAdapter.updateList(teacherList);
        tvTeacherCount.setText("(" + teacherList.size() + ")");
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}