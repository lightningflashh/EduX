package vn.edu.hcmute.edux.activities.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.activities.LoginActivity;
import vn.edu.hcmute.edux.controllers.admin.AdminController;

public class AdminActivity extends AppCompatActivity {

    private TextView tvStudentCount, tvTeacherCount, tvSubjectCount, tvUserName;
    private ConstraintLayout btnStudent, btnTeacher, btnClass;
    private CardView btnLogout;
    private AdminController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khởi tạo Controller
        controller = new AdminController(this);

        // Ánh xạ các view
        tvStudentCount = findViewById(R.id.tvStudentCount);
        tvTeacherCount = findViewById(R.id.tvTeacherCount);
        tvSubjectCount = findViewById(R.id.tvSubjectCount);
        tvUserName = findViewById(R.id.textView5);
        btnStudent = findViewById(R.id.btn_student);
        btnTeacher = findViewById(R.id.btn_teacher);
        btnClass = findViewById(R.id.btn_class);
        btnLogout = findViewById(R.id.btnLogout);

        // Gọi controller để tải dữ liệu
        controller.loadCounts();

        // Xử lý sự kiện nhấn vào các nút
        btnStudent.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, ManageStudentActivity.class)));
        btnTeacher.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, ManageTeacherActivity.class)));
        btnClass.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, ManageSubjectActivity.class)));
        btnLogout.setOnClickListener(v -> controller.logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.loadCounts();
    }

    // Các phương thức để Controller cập nhật giao diện
    public void updateStudentCount(String count) {
        tvStudentCount.setText(count);
    }

    public void updateTeacherCount(String count) {
        tvTeacherCount.setText(count);
    }

    public void updateSubjectCount(String count) {
        tvSubjectCount.setText(count);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}