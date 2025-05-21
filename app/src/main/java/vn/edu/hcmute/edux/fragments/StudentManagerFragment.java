package vn.edu.hcmute.edux.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.activities.Admin.ManageStudentActivity;
import vn.edu.hcmute.edux.adapters.StudentAdapter;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.services.StudentService;

public class StudentManagerFragment extends Fragment {

    private EditText inputMSSV;
    private Button btnAddStudent, btnAddStudentList;
    private RecyclerView recyclerView;
    private StudentService studentService;
    private StudentAdapter adapter;
    private List<Student> studentList;
    private String classId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_manager, container, false);

        // Init UI
        inputMSSV = view.findViewById(R.id.inputMSSV);
        btnAddStudent = view.findViewById(R.id.btnAddStudent);
        btnAddStudentList = view.findViewById(R.id.btnAddStudentList);
        recyclerView = view.findViewById(R.id.recyclerStudents);

        // Get class ID
        classId = getArguments() != null ? getArguments().getString("class_id") : null;
        studentService = new StudentService();

        // Recycler setup
        studentList = new ArrayList<>();
        adapter = new StudentAdapter(studentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadStudents();

        btnAddStudent.setOnClickListener(v -> {
            String mssv = inputMSSV.getText().toString().trim();
            if (!mssv.isEmpty()) {
                studentService.addStudentToClassByMSSV(classId, mssv,
                        () -> {
                            Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                            inputMSSV.setText("");
                            loadStudents();
                        },
                        error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
                );
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập MSSV", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnDeleteClickListener(student -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa" + student.getFullName() + "?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        studentService.removeStudentFromClassByMSSV(classId, student.getStudentCode(),
                            () -> {
                                Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                inputMSSV.setText("");
                                loadStudents();
                            },
                            error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
                        );
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        return view;
    }

    private void loadStudents() {
        studentService.getStudentsInClass(classId, new StudentService.StudentListCallback() {
            @Override
            public void onSuccess(List<Student> students) {
                studentList.clear();
                studentList.addAll(students);
                adapter.updateList(studentList);;
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Lỗi khi tải danh sách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
