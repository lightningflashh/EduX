package vn.edu.hcmute.edux.activities.Admin;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.adapters.StudentSelectionAdapter;
import vn.edu.hcmute.edux.controllers.admin.AddSubjectController;
import vn.edu.hcmute.edux.model.Student;

public class AddSubjectActivity extends AppCompatActivity {

    private TextInputEditText etSubjectId, etSubjectName, etDescription;
    private Spinner spinnerTeacher;
    private RecyclerView rvStudents;
    private Button btnAddSubject;
    private ImageView btnBack;
    private AddSubjectController controller;
    private StudentSelectionAdapter studentSelectionAdapter;
    private ArrayAdapter<String> teacherAdapter;
    private List<String> teacherNames;
    private List<Student> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khởi tạo Controller
        controller = new AddSubjectController(this);

        // Ánh xạ các view từ layout
        etSubjectId = findViewById(R.id.etSubjectId);
        etSubjectName = findViewById(R.id.etSubjectName);
        etDescription = findViewById(R.id.etDescription);
        spinnerTeacher = findViewById(R.id.spinnerTeacher);
        rvStudents = findViewById(R.id.rvStudents);
        btnAddSubject = findViewById(R.id.btnAddSubject);
        btnBack = findViewById(R.id.btn_Back);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Khởi tạo Adapter
        teacherNames = new ArrayList<>();
        teacherNames.add("Chọn giảng viên...");
        teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherNames);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(teacherAdapter);

        studentList = new ArrayList<>();
        studentSelectionAdapter = new StudentSelectionAdapter(studentList);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(studentSelectionAdapter);

        // Tải dữ liệu từ Controller
        controller.loadTeachers();
        controller.loadStudents();

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện khi nhấn nút Thêm Môn Học
        btnAddSubject.setOnClickListener(v -> {
            String subjectId = etSubjectId.getText().toString().trim();
            String subjectName = etSubjectName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            int selectedTeacherPosition = spinnerTeacher.getSelectedItemPosition();
            List<Student> selectedStudents = studentSelectionAdapter.getSelectedStudents();
            controller.createSubject(subjectId, subjectName, description, selectedTeacherPosition, selectedStudents);
        });
    }

    // Phương thức để Controller cập nhật giao diện
    public void updateTeacherList(List<String> updatedTeacherNames) {
        teacherNames.clear();
        teacherNames.addAll(updatedTeacherNames);
        teacherAdapter.notifyDataSetChanged();
    }

    public void updateStudentList(List<Student> students) {
        studentList.clear();
        studentList.addAll(students);
        studentSelectionAdapter.notifyDataSetChanged();
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void navigateBack() {
        finish();
    }
}