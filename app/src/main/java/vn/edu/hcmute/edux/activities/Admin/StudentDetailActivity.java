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
import com.google.firebase.firestore.FirebaseFirestore;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.services.StudentService;


public class StudentDetailActivity extends AppCompatActivity {

    private TextInputEditText etStudentId, etFullName, etEmail, etStudentCode, etMajor;
    private Button btnSave;
    private ImageView btnBack, btnEdit;
    private StudentService studentService;
    private Student student;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        studentService = new StudentService();
        etStudentId = findViewById(R.id.etStudentId);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etStudentCode = findViewById(R.id.etStudentCode);
        etMajor = findViewById(R.id.etMajor);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btn_Back);
        btnEdit = findViewById(R.id.btnEdit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Nhận studentId từ Intent và tải dữ liệu
        String studentId = getIntent().getStringExtra("STUDENT_ID");
        if (studentId == null) {
            Toast.makeText(this, "Không tìm thấy sinh viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tải thông tin sinh viên từ Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("students").document(studentId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        student = document.toObject(Student.class);
                        if (student != null) {
                            etStudentId.setText(student.getUserID());
                            etFullName.setText(student.getFullName());
                            etEmail.setText(student.getEmail());
                            etStudentCode.setText(student.getStudentCode());
                            etMajor.setText(student.getMajor());
                        } else {
                            Toast.makeText(this, "Dữ liệu sinh viên không hợp lệ", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Sinh viên không tồn tại", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải sinh viên: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });

        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> toggleEditMode(true));

        btnSave.setOnClickListener(v -> saveChanges());
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

    private void saveChanges() {
        String studentId = etStudentId.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String studentCode = etStudentCode.getText().toString().trim();
        String major = etMajor.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || studentCode.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        student.setFullName(fullName);
        student.setEmail(email);
        student.setStudentCode(studentCode);
        student.setMajor(major);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("students").document(studentId)
                .set(student)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật sinh viên thành công!", Toast.LENGTH_SHORT).show();
                    toggleEditMode(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
