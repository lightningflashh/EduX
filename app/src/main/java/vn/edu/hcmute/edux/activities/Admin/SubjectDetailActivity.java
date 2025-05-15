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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.adapters.StudentSelectionAdapter;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.model.Teacher;
import vn.edu.hcmute.edux.services.SubjectService;


public class SubjectDetailActivity extends AppCompatActivity {

    private TextInputEditText etSubjectId, etSubjectName, etDescription;
    private Spinner spinnerTeacher;
    private RecyclerView rvStudents;
    private Button btnSave;
    private ImageView btnBack, btnEdit;
    private SubjectService subjectService;
    private Subject subject;
    private List<Teacher> teacherList;
    private List<Student> studentList;
    private List<Student> selectedStudents;
    private StudentSelectionAdapter studentSelectionAdapter;
    private ArrayAdapter<String> teacherAdapter;
    private List<String> teacherNames;
    private boolean isEditMode = false;
    private Teacher originalTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        subjectService = new SubjectService();

        etSubjectId = findViewById(R.id.etSubjectId);
        etSubjectName = findViewById(R.id.etSubjectName);
        etDescription = findViewById(R.id.etDescription);
        spinnerTeacher = findViewById(R.id.spinnerTeacher);
        rvStudents = findViewById(R.id.rvStudents);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btn_Back);
        btnEdit = findViewById(R.id.btnEdit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        teacherList = new ArrayList<>();
        studentList = new ArrayList<>();
        selectedStudents = new ArrayList<>();
        teacherNames = new ArrayList<>();
        teacherNames.add("Chọn giảng viên...");
        teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherNames);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(teacherAdapter);

        studentSelectionAdapter = new StudentSelectionAdapter(studentList);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(studentSelectionAdapter);

        // Nhận classId từ Intent và tải dữ liệu
        String classId = getIntent().getStringExtra("SUBJECT_ID");
        if (classId == null) {
            Toast.makeText(this, "Không tìm thấy môn học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tải thông tin môn học từ Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("subjects").document(classId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        subject = document.toObject(Subject.class);
                        if (subject != null) {
                            etSubjectId.setText(subject.getClassId());
                            etSubjectName.setText(subject.getClassName());
                            etDescription.setText(subject.getDescription());
                            loadTeachers();
                            loadStudents();
                        } else {
                            Toast.makeText(this, "Dữ liệu môn học không hợp lệ", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Môn học không tồn tại", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải môn học: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });

        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> toggleEditMode(true));

        btnSave.setOnClickListener(v -> saveChanges());
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
                        // Kiểm tra nếu teachingClasses không null và có môn học hiện tại
                        if (teacher.getTeachingClasses() != null) {
                            boolean hasSubject = false;
                            for (Subject s : teacher.getTeachingClasses()) {
                                if (s != null && subject.getClassId().equals(s.getClassId())) {
                                    hasSubject = true;
                                    break;
                                }
                            }
                            if (hasSubject) {
                                originalTeacher = teacher;
                                spinnerTeacher.setSelection(teacherNames.indexOf(teacher.getFullName()));
                            }
                        }
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
                    selectedStudents.clear();
                    for (Student student : studentList) {
                        if (student.getJoinedClasses() != null) {
                            boolean hasSubject = false;
                            for (Subject s : student.getJoinedClasses()) {
                                if (s != null && subject.getClassId().equals(s.getClassId())) {
                                    hasSubject = true;
                                    break;
                                }
                            }
                            if (hasSubject) {
                                selectedStudents.add(student);
                                studentSelectionAdapter.setSelected(student, true);
                            }
                        }
                    }
                    studentSelectionAdapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        );
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

    private void saveChanges() {
        String subjectId = etSubjectId.getText().toString().trim();
        String subjectName = etSubjectName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        int selectedTeacherPosition = spinnerTeacher.getSelectedItemPosition();
        List<Student> newSelectedStudents = studentSelectionAdapter.getSelectedStudents();

        if (subjectName.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền tên môn học", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTeacherPosition == 0) {
            Toast.makeText(this, "Vui lòng chọn giảng viên", Toast.LENGTH_SHORT).show();
            return;
        }

        Teacher newTeacher = teacherList.get(selectedTeacherPosition - 1);

        subjectService.updateSubject(
                subjectId, subjectName, description,
                originalTeacher, newTeacher,
                selectedStudents, newSelectedStudents,
                success -> {
                    Toast.makeText(this, "Cập nhật môn học thành công!", Toast.LENGTH_SHORT).show();
                    toggleEditMode(false);
                    subject = new Subject(subjectId, subjectName, description, null);
                    originalTeacher = newTeacher;
                    selectedStudents = new ArrayList<>(newSelectedStudents);
                },
                error -> Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        );
    }
}
