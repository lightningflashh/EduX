package vn.edu.hcmute.edux.activities.Admin;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.controllers.admin.TeacherDetailController;
import vn.edu.hcmute.edux.model.Teacher;

public class TeacherDetailActivity extends AppCompatActivity {

    private TextInputEditText etTeacherId, etFullName, etEmail, etDegree, etMajor;
    private Button btnSave;
    private ImageView btnBack, btnEdit;
    private TeacherDetailController controller;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khởi tạo Controller
        controller = new TeacherDetailController(this);

        // Ánh xạ các view từ layout
        etTeacherId = findViewById(R.id.etTeacherId);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etDegree = findViewById(R.id.etDegree);
        etMajor = findViewById(R.id.etMajor);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btn_Back);
        btnEdit = findViewById(R.id.btnEdit);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Nhận teacherId từ Intent và tải dữ liệu
        String teacherId = getIntent().getStringExtra("TEACHER_ID");
        if (teacherId == null) {
            showToast("Không tìm thấy giảng viên");
            finish();
            return;
        }

        // Tải thông tin giảng viên từ Controller
        controller.loadTeacher(teacherId);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Edit
        btnEdit.setOnClickListener(v -> toggleEditMode(true));

        // Xử lý nút Save
        btnSave.setOnClickListener(v -> {
            String teacherIdText = etTeacherId.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String degree = etDegree.getText().toString().trim();
            String major = etMajor.getText().toString().trim();
            controller.saveChanges(teacherIdText, fullName, email, degree, major);
        });
    }

    private void toggleEditMode(boolean enable) {
        isEditMode = enable;
        etFullName.setEnabled(enable);
        etEmail.setEnabled(enable);
        etDegree.setEnabled(enable);
        etMajor.setEnabled(enable);
        btnSave.setVisibility(enable ? View.VISIBLE : View.GONE);
        btnEdit.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    // Phương thức để Controller cập nhật giao diện
    public void displayTeacher(Teacher teacher) {
        etTeacherId.setText(teacher.getUserID());
        etFullName.setText(teacher.getFullName());
        etEmail.setText(teacher.getEmail());
        etDegree.setText(teacher.getDegree());
        etMajor.setText(teacher.getMajor());
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void finishActivity() {
        finish();
    }

    public void disableEditMode() {
        toggleEditMode(false);
    }
}