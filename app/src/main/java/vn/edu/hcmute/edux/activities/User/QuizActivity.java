package vn.edu.hcmute.edux.activities.User;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.controllers.user.QuizActivityController;
import vn.edu.hcmute.edux.model.Question;

public class QuizActivity extends AppCompatActivity {

    private TextView txTimeQuiz, txSttQuestion, tvQuestionNumber, tvQuestion;
    private CardView questionCardView;
    private RadioGroup radioGroup;
    private RadioButton option1, option2, option3, option4;
    private Button btnNextQuestion;
    private ConstraintLayout btnSubmitQuiz, btnPrevQuestion;
    private QuizActivityController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_quiz);

        // Khởi tạo Controller
        controller = new QuizActivityController(this);

        // Ánh xạ các view từ layout
        txTimeQuiz = findViewById(R.id.tx_time_quiz);
        txSttQuestion = findViewById(R.id.tx_stt_question);
        tvQuestionNumber = findViewById(R.id.tv_question_number);
        tvQuestion = findViewById(R.id.tv_question);
        questionCardView = findViewById(R.id.questionCardView);
        radioGroup = findViewById(R.id.radioGroup);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        btnNextQuestion = findViewById(R.id.btn_next_question);
        btnSubmitQuiz = findViewById(R.id.btn_sumit_quiz);
        btnPrevQuestion = findViewById(R.id.btn_prev_question);

        // Nhận quizId từ Intent
        String quizId = getIntent().getStringExtra("quiz_id");
        if (quizId == null) {
            // Nếu không có quizId, hiển thị thông báo lỗi và kết thúc Activity
            showToast("Không tìm thấy quiz! Vui lòng chọn quiz để làm bài.");
            finish();
            return;
        }

        // Tải quiz
        controller.loadQuiz(quizId);

        // Lắng nghe sự kiện thay đổi trên RadioGroup để cập nhật giao diện ngay lập tức
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                // Khi người dùng chọn một đáp án, cập nhật giao diện
                RadioButton selectedRadioButton = findViewById(checkedId);
                String selectedAnswerId = null;
                if (selectedRadioButton == option1) selectedAnswerId = "A";
                else if (selectedRadioButton == option2) selectedAnswerId = "B";
                else if (selectedRadioButton == option3) selectedAnswerId = "C";
                else if (selectedRadioButton == option4) selectedAnswerId = "D";

                // Lưu đáp án ngay lập tức vào controller
                controller.saveCurrentAnswer(selectedAnswerId);

                // Giao diện đã được cập nhật tự động bởi radio_selector và radio_button_selector
                // Có thể thêm hiệu ứng bổ sung nếu cần (ví dụ: thay đổi màu text)
                option1.setTextColor(getResources().getColor(selectedAnswerId.equals("A") ? R.color.green : R.color.black));
                option2.setTextColor(getResources().getColor(selectedAnswerId.equals("B") ? R.color.green : R.color.black));
                option3.setTextColor(getResources().getColor(selectedAnswerId.equals("C") ? R.color.green : R.color.black));
                option4.setTextColor(getResources().getColor(selectedAnswerId.equals("D") ? R.color.green : R.color.black));
            }
        });

        // Xử lý sự kiện nút Next
        btnNextQuestion.setOnClickListener(v -> {
            controller.nextQuestion();
        });

        // Xử lý sự kiện nút Previous
        btnPrevQuestion.setOnClickListener(v -> {
            controller.prevQuestion();
        });

        // Xử lý sự kiện nút Submit
        btnSubmitQuiz.setOnClickListener(v -> {
            controller.submitQuiz();
        });
    }

    // Phương thức để Controller cập nhật giao diện
    public void displayQuestion(Question question, int questionIndex, int totalQuestions, String selectedAnswerId) {
        txSttQuestion.setText(String.format("%d/%d", questionIndex + 1, totalQuestions));
        tvQuestionNumber.setText("Question " + (questionIndex + 1));
        tvQuestion.setText(question.getContent());
        option1.setText(question.getAnswers().get(0).getContent());
        option2.setText(question.getAnswers().get(1).getContent());
        option3.setText(question.getAnswers().get(2).getContent());
        option4.setText(question.getAnswers().get(3).getContent());

        // Hiển thị đáp án đã chọn (nếu có)
        radioGroup.clearCheck();
        if (selectedAnswerId != null) {
            switch (selectedAnswerId) {
                case "A":
                    option1.setChecked(true);
                    break;
                case "B":
                    option2.setChecked(true);
                    break;
                case "C":
                    option3.setChecked(true);
                    break;
                case "D":
                    option4.setChecked(true);
                    break;
            }
        }

        // Cập nhật màu text dựa trên đáp án đã chọn
        option1.setTextColor(getResources().getColor(selectedAnswerId != null && selectedAnswerId.equals("A") ? R.color.green : R.color.black));
        option2.setTextColor(getResources().getColor(selectedAnswerId != null && selectedAnswerId.equals("B") ? R.color.green : R.color.black));
        option3.setTextColor(getResources().getColor(selectedAnswerId != null && selectedAnswerId.equals("C") ? R.color.green : R.color.black));
        option4.setTextColor(getResources().getColor(selectedAnswerId != null && selectedAnswerId.equals("D") ? R.color.green : R.color.black));
    }

    public void updateTimer(String time) {
        txTimeQuiz.setText(time);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void finishActivity() {
        finish();
    }
}