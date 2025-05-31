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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.adapters.StudentAdapter;
import vn.edu.hcmute.edux.controllers.admin.ManageStudentController;
import vn.edu.hcmute.edux.model.Student;

public class ManageStudentActivity extends AppCompatActivity {

    private ImageView btnBack, fabAddStudent;
    private RecyclerView rvStudents;
    private TextView tvTeacherCount, tvStudentListHeading;
    private EditText etSearch;
    private StudentAdapter studentAdapter;
    private List<Student> studentList;
    private ManageStudentController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_student);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khởi tạo Controller
        controller = new ManageStudentController(this);

        // Ánh xạ các view
        btnBack = findViewById(R.id.btn_Back);
        fabAddStudent = findViewById(R.id.fabAddStudent);
        rvStudents = findViewById(R.id.rvStudents);
        tvTeacherCount = findViewById(R.id.tvTeacherCount);
        tvStudentListHeading = findViewById(R.id.tvStudentListHeading);
        etSearch = findViewById(R.id.etSearch);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Khởi tạo Adapter
        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentList);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(studentAdapter);

        // Xử lý sự kiện nhấn vào item để xem chi tiết
        studentAdapter.setOnItemClickListener(student -> {
            Intent intent = new Intent(ManageStudentActivity.this, StudentDetailActivity.class);
            intent.putExtra("STUDENT_ID", student.getUserID());
            startActivity(intent);
        });

        // Xử lý sự kiện nhấn nút xóa
        studentAdapter.setOnDeleteClickListener(student -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa sinh viên " + student.getFullName() + "?")
                    .setPositiveButton("Xóa", (dialog, which) -> controller.deleteStudent(student))
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        // Tải danh sách sinh viên
        controller.loadStudents();

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Thêm Sinh Viên
        fabAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(ManageStudentActivity.this, AddStudentActivity.class);
            startActivity(intent);
        });

        // Xử lý tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                studentAdapter.filter(s.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.loadStudents();
    }

    // Phương thức để Controller cập nhật giao diện
    public void updateStudentList(List<Student> students) {
        studentList.clear();
        studentList.addAll(students);
        studentAdapter.updateList(studentList);
        tvTeacherCount.setText("(" + studentList.size() + ")");
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}