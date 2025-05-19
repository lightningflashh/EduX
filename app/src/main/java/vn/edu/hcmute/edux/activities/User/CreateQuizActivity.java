package vn.edu.hcmute.edux.activities.User;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.Answer;
import vn.edu.hcmute.edux.model.EQuizStatus;
import vn.edu.hcmute.edux.model.Question;
import vn.edu.hcmute.edux.model.Quiz;
import vn.edu.hcmute.edux.services.QuizService;
import vn.edu.hcmute.edux.utils.UserSessionManager;

public class CreateQuizActivity extends AppCompatActivity {

    private LinearLayout questionContainer;
    private Button btnAddQuestion, btnSubmit;
    private ImageView btnBack;
    private EditText edtQuizName, edtQuizDescription;
    private TextView txtStartTime, txtEndTime, txtDuration;

    private final Calendar calendarStart = Calendar.getInstance();
    private final Calendar calendarEnd = Calendar.getInstance();
    private String userId, classId;

    QuizService quizService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        init();
        addQuestionView(); // Thêm câu hỏi mặc định

        btnAddQuestion.setOnClickListener(v -> addQuestionView());
        btnSubmit.setOnClickListener(v -> handleCreateQuiz());
        btnBack.setOnClickListener(v -> finish());

        txtStartTime.setOnClickListener(v -> showDateTimePicker(calendarStart, txtStartTime));
        txtEndTime.setOnClickListener(v -> showDateTimePicker(calendarEnd, txtEndTime));
    }

    private void init() {
        questionContainer = findViewById(R.id.questionContainer);
        btnAddQuestion = findViewById(R.id.btn_add_question);
        btnSubmit = findViewById(R.id.btn_submit);
        btnBack = findViewById(R.id.btn_back);
        edtQuizName = findViewById(R.id.edt_quiz_name);
        edtQuizDescription = findViewById(R.id.edt_quiz_description);
        txtStartTime = findViewById(R.id.txt_start_time);
        txtEndTime = findViewById(R.id.txt_end_time);
        txtDuration = findViewById(R.id.txt_duration);
        userId = UserSessionManager.getUserId(this);
        classId = getIntent().getStringExtra("class_id");
    }

    private void addQuestionView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View questionView = inflater.inflate(R.layout.question_item, questionContainer, false);

        ImageView btnRemove = questionView.findViewById(R.id.btn_remove_question);
        btnRemove.setOnClickListener(v -> questionContainer.removeView(questionView));

        questionContainer.addView(questionView);
    }

    private void showDateTimePicker(Calendar calendar, TextView targetView) {
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    TimePickerDialog timePicker = new TimePickerDialog(this,
                            (tpView, hour, minute) -> {
                                calendar.set(year, month, day, hour, minute);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                targetView.setText(sdf.format(calendar.getTime()));

                                // Tính duration nếu đủ cả 2
                                if (!txtStartTime.getText().toString().contains("Chọn") &&
                                        !txtEndTime.getText().toString().contains("Chọn")) {
                                    long duration = TimeUnit.MILLISECONDS.toMinutes(calendarEnd.getTimeInMillis() - calendarStart.getTimeInMillis());
                                    txtDuration.setText("Thời lượng: " + duration + " phút");
                                }
                            }, 8, 0, true);
                    timePicker.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    private void handleCreateQuiz() {
        String quizTitle = edtQuizName.getText().toString().trim();
        String quizDesc = edtQuizDescription.getText().toString().trim();

        if (quizTitle.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên quiz", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Question> questionList = new ArrayList<>();

        for (int i = 0; i < questionContainer.getChildCount(); i++) {
            View questionView = questionContainer.getChildAt(i);

            EditText etQuestion = questionView.findViewById(R.id.et_question);
            EditText etA = questionView.findViewById(R.id.et_option_a);
            EditText etB = questionView.findViewById(R.id.et_option_b);
            EditText etC = questionView.findViewById(R.id.et_option_c);
            EditText etD = questionView.findViewById(R.id.et_option_d);
            RadioGroup rgAnswer = questionView.findViewById(R.id.rg_correct_answer);

            String questionText = etQuestion.getText().toString().trim();
            String a = etA.getText().toString().trim();
            String b = etB.getText().toString().trim();
            String c = etC.getText().toString().trim();
            String d = etD.getText().toString().trim();

            int selectedId = rgAnswer.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Chưa chọn đáp án đúng ở câu " + (i + 1), Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadio = questionView.findViewById(selectedId);
            String correctKey = selectedRadio.getText().toString().trim(); // A, B, C, D

            List<Answer> answers = new ArrayList<>();
            answers.add(new Answer("A", a, correctKey.equals("A")));
            answers.add(new Answer("B", b, correctKey.equals("B")));
            answers.add(new Answer("C", c, correctKey.equals("C")));
            answers.add(new Answer("D", d, correctKey.equals("D")));

            String questionId = UUID.randomUUID().toString();
            questionList.add(new Question(questionId, questionText, answers));
        }

        // Tạo quiz object
        String quizId = UUID.randomUUID().toString();
        Date startTime = calendarStart.getTime();
        Date endTime = calendarEnd.getTime();
        long duration = TimeUnit.MILLISECONDS.toMinutes(endTime.getTime() - startTime.getTime());

        Quiz quiz = new Quiz(quizId, quizTitle, quizDesc, userId, classId, startTime, endTime, duration, EQuizStatus.UPCOMING, questionList);

        quizService = new QuizService();
        quizService.createQuiz(
                this,
                quiz,
                () -> finish(),  // onSuccess: đóng activity
                null             // onFailure: không làm gì thêm
        );
    }
}
