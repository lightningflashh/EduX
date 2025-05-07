package vn.edu.hcmute.edux.fragments.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import vn.edu.hcmute.edux.Adapter.StudentAdapter;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.services.StudentService;

public class ManageStudentActivity extends AppCompatActivity {

    private ImageView btnBack, fabAddStudent;
    private RecyclerView rvStudents;
    private TextView tvTeacherCount, tvStudentListHeading;
    private EditText etSearch;
    private StudentAdapter studentAdapter;
    private List<Student> studentList;
    private StudentService studentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_student);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        studentService = new StudentService();

        btnBack = findViewById(R.id.btn_Back);
        fabAddStudent = findViewById(R.id.fabAddStudent);
        rvStudents = findViewById(R.id.rvStudents); // ID trong layout là rvTeachers, nên đổi thành rvStudents nếu cần
        tvTeacherCount = findViewById(R.id.tvTeacherCount);
        tvStudentListHeading = findViewById(R.id.tvStudentListHeading);
        etSearch = findViewById(R.id.etSearch);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        studentService.deleteStudent(
                                student.getUserID(),
                                success -> {
                                    studentList.remove(student);
                                    studentAdapter.updateList(studentList);
                                    tvTeacherCount.setText("(" + studentList.size() + ")");
                                    Toast.makeText(ManageStudentActivity.this, "Xóa sinh viên thành công!", Toast.LENGTH_SHORT).show();
                                },
                                error -> Toast.makeText(ManageStudentActivity.this, error, Toast.LENGTH_LONG).show()
                        );
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        loadStudents();

        btnBack.setOnClickListener(v -> finish());

        fabAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(ManageStudentActivity.this, AddStudentActivity.class);
            startActivity(intent);
        });

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
        loadStudents();
    }

    private void loadStudents() {
        studentService.loadStudents(
                students -> {
                    studentList.clear();
                    studentList.addAll(students);
                    studentAdapter.updateList(studentList);
                    tvTeacherCount.setText("(" + studentList.size() + ")");
                    Log.d("ManageStudentActivity", "Tải thành công " + studentList.size() + " sinh viên");
                },
                error -> {
                    Toast.makeText(ManageStudentActivity.this, error, Toast.LENGTH_LONG).show();
                    Log.e("ManageStudentActivity", error);
                }
        );
    }
}
