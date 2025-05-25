package vn.edu.hcmute.edux.activities.User;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.controllers.user.NotificationController;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.utils.UserSessionManager;
import vn.edu.hcmute.edux.views.INotificationCreateView;

public class CreateNotificationActivity extends AppCompatActivity implements INotificationCreateView {

    private EditText edtTitle, edtContent;
    private Spinner spinnerSubjects;
    private Button btnSubmit;

    private NotificationController controller;

    private List<Subject> subjectList = new ArrayList<>();
    private Subject selectedSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        edtTitle = findViewById(R.id.edt_title);
        edtContent = findViewById(R.id.edt_content);
        spinnerSubjects = findViewById(R.id.spinner_subjects);
        btnSubmit = findViewById(R.id.btn_submit);

        controller = new NotificationController(null); // Controller dùng chung, không dùng view list
        String userId = UserSessionManager.getUserId(this);
        String role = UserSessionManager.getRole(this);

        if (!"TEACHER".equals(role)) {
            showError("Chỉ giáo viên mới có quyền tạo thông báo");
            finish();
            return;
        }

        controller.loadSubjectsForCreation(userId,
                this::showSubjects,
                this::showError);

        btnSubmit.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String content = edtContent.getText().toString().trim();

            controller.createNotification(title, content, selectedSubject,
                    () -> {
                        showMessage("Đã tạo thông báo!");
                        finishActivity();
                    },
                    this::showError
            );
        });
    }

    @Override
    public void showSubjects(List<Subject> subjects) {
        subjectList = subjects;
        List<String> names = new ArrayList<>();
        for (Subject s : subjects) {
            names.add(s.getClassName() + " (" + s.getClassId() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjects.setAdapter(adapter);

        spinnerSubjects.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedSubject = subjectList.get(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedSubject = null;
            }
        });
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishActivity() {
        finish();
    }
}
