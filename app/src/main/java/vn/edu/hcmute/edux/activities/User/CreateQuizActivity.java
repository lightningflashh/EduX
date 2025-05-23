package vn.edu.hcmute.edux.activities.User;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.controllers.user.CreateQuizController;
import vn.edu.hcmute.edux.utils.UserSessionManager;
import vn.edu.hcmute.edux.views.iCreateQuizView;

public class CreateQuizActivity extends AppCompatActivity implements iCreateQuizView {

    private LinearLayout questionContainer;
    private EditText edtQuizName, edtQuizDescription;
    private TextView txtStartTime, txtEndTime, txtDuration;
    private Calendar calendarStart = Calendar.getInstance(), calendarEnd = Calendar.getInstance();

    private CreateQuizController controller;
    private String userId, classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        init();
        controller = new CreateQuizController(this);

        addQuestionView();

        findViewById(R.id.btn_add_question).setOnClickListener(v -> addQuestionView());
        findViewById(R.id.btn_submit).setOnClickListener(v -> {
            List<View> views = new ArrayList<>();
            for (int i = 0; i < questionContainer.getChildCount(); i++) {
                views.add(questionContainer.getChildAt(i));
            }
            controller.handleCreateQuiz(userId, classId, views);
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        txtStartTime.setOnClickListener(v -> showDateTimePicker(calendarStart, txtStartTime));
        txtEndTime.setOnClickListener(v -> showDateTimePicker(calendarEnd, txtEndTime));
    }

    private void init() {
        edtQuizName = findViewById(R.id.edt_quiz_name);
        edtQuizDescription = findViewById(R.id.edt_quiz_description);
        txtStartTime = findViewById(R.id.txt_start_time);
        txtEndTime = findViewById(R.id.txt_end_time);
        txtDuration = findViewById(R.id.txt_duration);
        questionContainer = findViewById(R.id.questionContainer);
        userId = UserSessionManager.getUserId(this);
        classId = getIntent().getStringExtra("class_id");
    }

    private void addQuestionView() {
        View qView = LayoutInflater.from(this).inflate(R.layout.question_item, questionContainer, false);
        qView.findViewById(R.id.btn_remove_question).setOnClickListener(v -> questionContainer.removeView(qView));
        questionContainer.addView(qView);
    }

    private void showDateTimePicker(Calendar cal, TextView target) {
        DatePickerDialog dp = new DatePickerDialog(this, (view, y, m, d) -> {
            TimePickerDialog tp = new TimePickerDialog(this, (tpView, h, min) -> {
                cal.set(y, m, d, h, min);
                target.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(cal.getTime()));

                if (!txtStartTime.getText().toString().contains("Chọn") &&
                        !txtEndTime.getText().toString().contains("Chọn")) {
                    long mins = TimeUnit.MILLISECONDS.toMinutes(calendarEnd.getTimeInMillis() - calendarStart.getTimeInMillis());
                    txtDuration.setText("Thời lượng: " + mins + " phút");
                }
            }, 8, 0, true);
            tp.show();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    // ==== ICreateQuizView implementation ====

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public String getQuizTitle() {
        return edtQuizName.getText().toString().trim();
    }

    @Override
    public String getQuizDescription() {
        return edtQuizDescription.getText().toString().trim();
    }

    @Override
    public Date getStartTime() {
        return calendarStart.getTime();
    }

    @Override
    public Date getEndTime() {
        return calendarEnd.getTime();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
