package vn.edu.hcmute.edux.activities.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.adapters.SubjectAdapter;
import vn.edu.hcmute.edux.controllers.admin.ManageSubjectController;
import vn.edu.hcmute.edux.model.Subject;

public class ManageSubjectActivity extends AppCompatActivity {

    private ImageView fabAddSubject, btnBack;
    private RecyclerView rvSubjects;
    private TextView tvSubjectCount;
    private EditText etSearch;
    private SubjectAdapter subjectAdapter;
    private List<Subject> subjectList;
    private ManageSubjectController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subject);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Khởi tạo Controller
        controller = new ManageSubjectController(this);

        // Ánh xạ các view
        fabAddSubject = findViewById(R.id.fabAddSubject);
        btnBack = findViewById(R.id.btn_Back);
        rvSubjects = findViewById(R.id.rvSubjects);
        tvSubjectCount = findViewById(R.id.tvSubjectCount);
        etSearch = findViewById(R.id.etSearch);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Khởi tạo Adapter
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
        subjectAdapter.setOnDeleteClickListener(controller::deleteSubject);

        // Tải danh sách môn học
        controller.loadSubjects();

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Thêm Môn Học
        fabAddSubject.setOnClickListener(v -> {
            Intent intent = new Intent(ManageSubjectActivity.this, AddSubjectActivity.class);
            startActivity(intent);
        });

        // Xử lý tìm kiếm
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
        controller.loadSubjects();
    }

    // Phương thức để Controller cập nhật giao diện
    public void updateSubjectList(List<Subject> subjects) {
        subjectList.clear();
        subjectList.addAll(subjects);
        subjectAdapter.updateList(subjectList);
        tvSubjectCount.setText("(" + subjectList.size() + ")");
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}