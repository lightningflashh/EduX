package vn.edu.hcmute.edux.fragments.Admin;

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

import vn.edu.hcmute.edux.Adapter.StudentSelectionAdapter;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.model.Teacher;
import vn.edu.hcmute.edux.services.SubjectService;


public class AddSubjectActivity extends AppCompatActivity {

    private TextInputEditText etSubjectId, etSubjectName, etDescription;
    private Spinner spinnerTeacher;
    private RecyclerView rvStudents;
    private Button btnAddSubject;
    private ImageView btnBack;
    private SubjectService subjectService;
    private List<Teacher> teacherList;
    private List<Student> studentList;
    private StudentSelectionAdapter studentSelectionAdapter;
    private ArrayAdapter<String> teacherAdapter;
    private List<String> teacherNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        subjectService = new SubjectService();

        etSubjectId = findViewById(R.id.etSubjectId);
        etSubjectName = findViewById(R.id.etSubjectName);
        etDescription = findViewById(R.id.etDescription);
        spinnerTeacher = findViewById(R.id.spinnerTeacher);
        rvStudents = findViewById(R.id.rvStudents);
        btnAddSubject = findViewById(R.id.btnAddSubject);
        btnBack = findViewById(R.id.btn_Back);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        teacherList = new ArrayList<>();
        studentList = new ArrayList<>();
        teacherNames = new ArrayList<>();
        teacherNames.add("Chọn giảng viên...");
        teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherNames);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(teacherAdapter);

        studentSelectionAdapter = new StudentSelectionAdapter(studentList);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(studentSelectionAdapter);

        loadTeachers();
        loadStudents();

        btnBack.setOnClickListener(v -> finish());

        btnAddSubject.setOnClickListener(v -> {
            String subjectId = etSubjectId.getText().toString().trim();
            String subjectName = etSubjectName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            int selectedTeacherPosition = spinnerTeacher.getSelectedItemPosition();
            List<Student> selectedStudents = studentSelectionAdapter.getSelectedStudents();

            if (subjectId.isEmpty() || subjectName.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền mã và tên môn học", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedTeacherPosition == 0) {
                Toast.makeText(this, "Vui lòng chọn giảng viên", Toast.LENGTH_SHORT).show();
                return;
            }

            Teacher selectedTeacher = teacherList.get(selectedTeacherPosition - 1);
            createSubject(subjectId, subjectName, description, selectedTeacher, selectedStudents);
        });
    }

    private void loadTeachers() {
        subjectService.loadTeachers(
                teachers -> {
                    teacherList.clear();
                    teacherList.addAll(teachers);
                    teacherNames.clear();
                    teacherNames.add("Chọn giảng viên...");
                    for (Teacher teacher : teacherList) {
                        teacherNames.add(teacher.getFullName());
                    }
                    teacherAdapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        );
    }

    private void loadStudents() {
        subjectService.loadStudents(
                students -> {
                    studentList.clear();
                    studentList.addAll(students);
                    studentSelectionAdapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        );
    }

    private void createSubject(String subjectId, String subjectName, String description,
                               Teacher selectedTeacher, List<Student> selectedStudents) {
        subjectService.createSubject(
                subjectId, subjectName, description, selectedTeacher, selectedStudents,
                success -> {
                    Toast.makeText(this, "Thêm môn học thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        );
    }
}
