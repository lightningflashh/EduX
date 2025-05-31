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
import vn.edu.hcmute.edux.controllers.admin.AddTeacherController;

public class AddTeacherActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etFullName, etMajor;
    private Button btnAddTeacher;
    private ImageView btnBack;
    private AddTeacherController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khởi tạo Controller
        controller = new AddTeacherController(this);

        // Ánh xạ các view từ layout
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFullName = findViewById(R.id.etFullName);
        etMajor = findViewById(R.id.etMajor);
        btnAddTeacher = findViewById(R.id.btnAddStudent); // ID vẫn là btnAddStudent theo layout
        btnBack = findViewById(R.id.btn_Back);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện khi nhấn nút Thêm Giảng Viên
        btnAddTeacher.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            String major = etMajor.getText().toString().trim();
            controller.addTeacher(email, password, fullName, major);
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