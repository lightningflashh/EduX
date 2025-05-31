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
import vn.edu.hcmute.edux.controllers.admin.AddStudentController;

public class AddStudentActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etStudentCode, etFullName;
    private Button btnAddStudent;
    private ImageView btnBack;
    private AddStudentController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khởi tạo Controller
        controller = new AddStudentController(this);

        // Ánh xạ các view từ layout
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etStudentCode = findViewById(R.id.etStudentCode);
        etFullName = findViewById(R.id.etFullName);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnBack = findViewById(R.id.btn_Back);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện khi nhấn nút Thêm Sinh Viên
        btnAddStudent.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String studentCode = etStudentCode.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            controller.addStudent(email, password, studentCode, fullName);
        });
    }

    // Phương thức để Controller gọi để hiển thị thông báo và điều hướng
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void navigateBack() {
        finish();
    }
}