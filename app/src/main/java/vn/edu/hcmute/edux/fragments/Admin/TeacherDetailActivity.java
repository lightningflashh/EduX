package vn.edu.hcmute.edux.fragments.Admin;

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
import vn.edu.hcmute.edux.model.Teacher;
import vn.edu.hcmute.edux.services.TeacherService;


public class TeacherDetailActivity extends AppCompatActivity {

    private TextInputEditText etTeacherId, etFullName, etEmail, etDegree, etMajor;
    private Button btnSave;
    private ImageView btnBack, btnEdit;
    private TeacherService teacherService;
    private Teacher teacher;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        teacherService = new TeacherService();

        etTeacherId = findViewById(R.id.etTeacherId);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etDegree = findViewById(R.id.etDegree);
        etMajor = findViewById(R.id.etMajor);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btn_Back);
        btnEdit = findViewById(R.id.btnEdit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Nhận teacherId từ Intent và tải dữ liệu
        String teacherId = getIntent().getStringExtra("TEACHER_ID");
        if (teacherId == null) {
            Toast.makeText(this, "Không tìm thấy giảng viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tải thông tin giảng viên từ Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("teachers").document(teacherId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        teacher = document.toObject(Teacher.class);
                        if (teacher != null) {
                            etTeacherId.setText(teacher.getUserID());
                            etFullName.setText(teacher.getFullName());
                            etEmail.setText(teacher.getEmail());
                            etDegree.setText(teacher.getDegree());
                            etMajor.setText(teacher.getMajor());
                        } else {
                            Toast.makeText(this, "Dữ liệu giảng viên không hợp lệ", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Giảng viên không tồn tại", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải giảng viên: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        etDegree.setEnabled(enable);
        etMajor.setEnabled(enable);
        btnSave.setVisibility(enable ? View.VISIBLE : View.GONE);
        btnEdit.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    private void saveChanges() {
        String teacherId = etTeacherId.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String degree = etDegree.getText().toString().trim();
        String major = etMajor.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        teacher.setFullName(fullName);
        teacher.setEmail(email);
        teacher.setDegree(degree);
        teacher.setMajor(major);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("teachers").document(teacherId)
                .set(teacher)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật giảng viên thành công!", Toast.LENGTH_SHORT).show();
                    toggleEditMode(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
