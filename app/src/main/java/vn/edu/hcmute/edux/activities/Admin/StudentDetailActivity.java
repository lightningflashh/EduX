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
import vn.edu.hcmute.edux.controllers.admin.StudentDetailController;
import vn.edu.hcmute.edux.model.Student;

public class StudentDetailActivity extends AppCompatActivity {

    private TextInputEditText etStudentId, etFullName, etEmail, etStudentCode, etMajor;
    private Button btnSave;
    private ImageView btnBack, btnEdit;
    private StudentDetailController controller;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khởi tạo Controller
        controller = new StudentDetailController(this);

        // Ánh xạ các view từ layout
        etStudentId = findViewById(R.id.etStudentId);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etStudentCode = findViewById(R.id.etStudentCode);
        etMajor = findViewById(R.id.etMajor);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btn_Back);
        btnEdit = findViewById(R.id.btnEdit);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Nhận studentId từ Intent và tải dữ liệu
        String studentId = getIntent().getStringExtra("STUDENT_ID");
        if (studentId == null) {
            showToast("Không tìm thấy sinh viên");
            finish();
            return;
        }

        // Tải thông tin sinh viên từ Controller
        controller.loadStudent(studentId);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Edit
        btnEdit.setOnClickListener(v -> toggleEditMode(true));

        // Xử lý nút Save
        btnSave.setOnClickListener(v -> {
            String studentIdText = etStudentId.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String studentCode = etStudentCode.getText().toString().trim();
            String major = etMajor.getText().toString().trim();
            controller.saveChanges(studentIdText, fullName, email, studentCode, major);
        });
    }

    private void toggleEditMode(boolean enable) {
        isEditMode = enable;
        etFullName.setEnabled(enable);
        etEmail.setEnabled(enable);
        etStudentCode.setEnabled(enable);
        etMajor.setEnabled(enable);
        btnSave.setVisibility(enable ? View.VISIBLE : View.GONE);
        btnEdit.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    // Phương thức để Controller cập nhật giao diện
    public void displayStudent(Student student) {
        etStudentId.setText(student.getUserID());
        etFullName.setText(student.getFullName());
        etEmail.setText(student.getEmail());
        etStudentCode.setText(student.getStudentCode());
        etMajor.setText(student.getMajor());
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