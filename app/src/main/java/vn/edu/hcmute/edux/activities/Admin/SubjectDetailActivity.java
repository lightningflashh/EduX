package vn.edu.hcmute.edux.activities.Admin;

import android.os.Bundle;
import android.view.View;
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
import vn.edu.hcmute.edux.controllers.admin.SubjectDetailController;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.model.Subject;

public class SubjectDetailActivity extends AppCompatActivity {

    private TextInputEditText etSubjectId, etSubjectName, etDescription;
    private Spinner spinnerTeacher;
    private RecyclerView rvStudents;
    private Button btnSave;
    private ImageView btnBack, btnEdit;
    private StudentSelectionAdapter studentSelectionAdapter;
    private ArrayAdapter<String> teacherAdapter;
    private List<String> teacherNames;
    private List<Student> studentList;
    private SubjectDetailController controller;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khởi tạo Controller
        controller = new SubjectDetailController(this);

        // Ánh xạ các view từ layout
        etSubjectId = findViewById(R.id.etSubjectId);
        etSubjectName = findViewById(R.id.etSubjectName);
        etDescription = findViewById(R.id.etDescription);
        spinnerTeacher = findViewById(R.id.spinnerTeacher);
        rvStudents = findViewById(R.id.rvStudents);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btn_Back);
        btnEdit = findViewById(R.id.btnEdit);

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

        // Nhận classId từ Intent và tải dữ liệu
        String classId = getIntent().getStringExtra("SUBJECT_ID");
        if (classId == null) {
            showToast("Không tìm thấy môn học");
            finish();
            return;
        }

        // Tải thông tin môn học từ Controller
        controller.loadSubject(classId);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Edit
        btnEdit.setOnClickListener(v -> toggleEditMode(true));

        // Xử lý nút Save
        btnSave.setOnClickListener(v -> {
            String subjectId = etSubjectId.getText().toString().trim();
            String subjectName = etSubjectName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            int selectedTeacherPosition = spinnerTeacher.getSelectedItemPosition();
            List<Student> newSelectedStudents = studentSelectionAdapter.getSelectedStudents();
            controller.saveChanges(subjectId, subjectName, description, selectedTeacherPosition, newSelectedStudents);
        });
    }

    private void toggleEditMode(boolean enable) {
        isEditMode = enable;
        etSubjectName.setEnabled(enable);
        etDescription.setEnabled(enable);
        spinnerTeacher.setEnabled(enable);
        rvStudents.setEnabled(enable);
        for (int i = 0; i < rvStudents.getChildCount(); i++) {
            View child = rvStudents.getChildAt(i);
            child.findViewById(R.id.cbStudent).setEnabled(enable);
        }
        btnSave.setVisibility(enable ? View.VISIBLE : View.GONE);
        btnEdit.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    // Phương thức để Controller cập nhật giao diện
    public void displaySubject(Subject subject) {
        etSubjectId.setText(subject.getClassId());
        etSubjectName.setText(subject.getClassName());
        etDescription.setText(subject.getDescription());
    }

    public void updateTeacherList(List<String> updatedTeacherNames) {
        teacherNames.clear();
        teacherNames.addAll(updatedTeacherNames);
        teacherAdapter.notifyDataSetChanged();
    }

    public void setTeacherSelection(int position) {
        spinnerTeacher.setSelection(position);
    }

    public void updateStudentList(List<Student> students) {
        studentList.clear();
        studentList.addAll(students);
        studentSelectionAdapter.notifyDataSetChanged();
    }

    public void setStudentSelection(Student student, boolean isSelected) {
        studentSelectionAdapter.setSelected(student, isSelected);
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