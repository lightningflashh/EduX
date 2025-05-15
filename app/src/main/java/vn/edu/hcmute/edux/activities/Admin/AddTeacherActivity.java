package vn.edu.hcmute.edux.activities.Admin;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import vn.edu.hcmute.edux.R;


public class AddTeacherActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etFullName, etMajor;
    private Button btnAddTeacher;
    private ImageView btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khởi tạo Firebase Auth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý sự kiện khi nhấn nút Thêm Giảng Viên
        btnAddTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String fullName = etFullName.getText().toString().trim();
                String major = etMajor.getText().toString().trim();

                // Kiểm tra đầu vào
                if (email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                    Toast.makeText(AddTeacherActivity.this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(AddTeacherActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo tài khoản trên Firebase Authentication
                createTeacherAccount(email, password, fullName, major);
            }
        });
    }

    // Hàm tạo tài khoản giảng viên trên Firebase Authentication và lưu thông tin vào Firestore
    private void createTeacherAccount(String email, String password, String fullName, String major) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Tạo tài khoản thành công, lấy userID
                            String userID = mAuth.getCurrentUser().getUid();

                            // Tạo đối tượng Teacher
                            Map<String, Object> teacher = new HashMap<>();
                            teacher.put("userID", userID);
                            teacher.put("email", email);
                            teacher.put("fullName", fullName);
                            teacher.put("password", password); // Lưu ý: Không nên lưu mật khẩu, chỉ để minh họa
                            teacher.put("role", "TEACHER");
                            teacher.put("imageUrl", "");
                            teacher.put("birthDate", null);
                            teacher.put("isActive", true);
                            teacher.put("createdAt", new Date());
                            teacher.put("updatedAt", new Date());
                            teacher.put("degree", null);
                            teacher.put("teachingClasses", null);
                            teacher.put("major", major.isEmpty() ? null : major);

                            // Lưu thông tin giảng viên vào Firestore
                            db.collection("teachers").document(userID)
                                    .set(teacher)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(AddTeacherActivity.this, "Thêm giảng viên thành công!", Toast.LENGTH_SHORT).show();
                                                finish(); // Quay lại màn hình trước
                                            } else {
                                                Toast.makeText(AddTeacherActivity.this, "Lưu thông tin thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            // Tạo tài khoản thất bại
                            Toast.makeText(AddTeacherActivity.this, "Tạo tài khoản thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
