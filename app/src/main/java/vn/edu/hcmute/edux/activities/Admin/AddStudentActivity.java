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


public class AddStudentActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etStudentCode, etFullName, etUsername;
    private Button btnAddStudent;
    private ImageView btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khởi tạo Firebase Auth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý sự kiện khi nhấn nút Thêm Sinh Viên
        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String studentCode = etStudentCode.getText().toString().trim();
                String fullName = etFullName.getText().toString().trim();

                // Kiểm tra đầu vào
                if (email.isEmpty() || password.isEmpty() || studentCode.isEmpty() || fullName.isEmpty()) {
                    Toast.makeText(AddStudentActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo tài khoản trên Firebase Authentication
                createStudentAccount(email, password, studentCode, fullName);
            }
        });
    }

    // Hàm tạo tài khoản sinh viên trên Firebase Authentication và lưu thông tin vào Firestore
    private void createStudentAccount(String email, String password, String studentCode, String fullName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Tạo tài khoản thành công, lấy userID
                            String userID = mAuth.getCurrentUser().getUid();

                            // Tạo đối tượng Student
                            Map<String, Object> student = new HashMap<>();
                            student.put("userID", userID);
                            student.put("email", email);
                            student.put("studentCode", studentCode);
                            student.put("fullName", fullName);
                            student.put("password", password); // Lưu ý: Không nên lưu mật khẩu, chỉ để minh họa
                            student.put("role", "STUDENT");
                            student.put("imageUrl", "");
                            student.put("birthDate", null);
                            student.put("isActive", true);
                            student.put("createdAt", new Date());
                            student.put("updatedAt", new Date());
                            student.put("studyTime", null);
                            student.put("major", null);

                            // Lưu thông tin sinh viên vào Firestore
                            db.collection("students").document(userID)
                                    .set(student)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(AddStudentActivity.this, "Thêm sinh viên thành công!", Toast.LENGTH_SHORT).show();
                                                finish(); // Quay lại màn hình trước (ManageStudentActivity)
                                            } else {
                                                Toast.makeText(AddStudentActivity.this, "Lưu thông tin thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            // Tạo tài khoản thất bại
                            Toast.makeText(AddStudentActivity.this, "Tạo tài khoản thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}