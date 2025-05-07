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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.edu.hcmute.edux.Adapter.SubjectAdapter;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.services.SubjectService;


public class ManageSubjectActivity extends AppCompatActivity {

    private ImageView fabAddSubject, btnBack;
    private RecyclerView rvSubjects;
    private TextView tvSubjectCount;
    private EditText etSearch;
    private SubjectAdapter subjectAdapter;
    private List<Subject> subjectList;
    private SubjectService subjectService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subject);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        subjectService = new SubjectService();

        fabAddSubject = findViewById(R.id.fabAddSubject);
        btnBack = findViewById(R.id.btn_Back);
        rvSubjects = findViewById(R.id.rvSubjects);
        tvSubjectCount = findViewById(R.id.tvSubjectCount);
        etSearch = findViewById(R.id.etSearch);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        subjectList = new ArrayList<>();
        subjectAdapter = new SubjectAdapter(subjectList);
        rvSubjects.setLayoutManager(new LinearLayoutManager(this));
        rvSubjects.setAdapter(subjectAdapter);

        // Xử lý sự kiện nhấn vào item để xem chi tiết
        subjectAdapter.setOnItemClickListener(subject -> {
            Intent intent = new Intent(ManageSubjectActivity.this, SubjectDetailActivity.class);
            intent.putExtra("SUBJECT_ID", subject.getClassId());
            startActivity(intent);
        });

        // Xử lý sự kiện nhấn nút xóa
        subjectAdapter.setOnDeleteClickListener(subject -> {
            subjectService.deleteSubject(
                    subject.getClassId(),
                    success -> {
                        subjectList.remove(subject);
                        subjectAdapter.updateList(subjectList);
                        tvSubjectCount.setText(subjectList.size());
                        Toast.makeText(ManageSubjectActivity.this, "Xóa môn học thành công!", Toast.LENGTH_SHORT).show();
                    },
                    error -> Toast.makeText(ManageSubjectActivity.this, error, Toast.LENGTH_LONG).show()
            );
        });

        loadSubjects();

        btnBack.setOnClickListener(v -> finish());

        fabAddSubject.setOnClickListener(v -> {
            Intent intent = new Intent(ManageSubjectActivity.this, AddSubjectActivity.class);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                subjectAdapter.filter(s.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubjects();
    }

    private void loadSubjects() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("subjects")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        subjectList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            Subject subject = new Subject(
                                    (String) data.get("classId"),
                                    (String) data.get("className"),
                                    (String) data.get("description"),
                                    null
                            );
                            subjectList.add(subject);
                        }
                        subjectAdapter.updateList(subjectList);
                        tvSubjectCount.setText("(" + subjectList.size() + ")");
                        Log.d("ManageSubjectActivity", "Tải thành công " + subjectList.size() + " môn học");
                    } else {
                        Toast.makeText(ManageSubjectActivity.this, "Lỗi khi tải danh sách môn học: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("ManageSubjectActivity", "Lỗi khi tải môn học: " + task.getException().getMessage());
                    }
                });
    }
}
