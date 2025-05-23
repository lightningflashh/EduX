package vn.edu.hcmute.edux.controllers.user;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import vn.edu.hcmute.edux.model.Answer;
import vn.edu.hcmute.edux.model.EQuizStatus;
import vn.edu.hcmute.edux.model.Question;
import vn.edu.hcmute.edux.model.Quiz;
import vn.edu.hcmute.edux.services.QuizService;
import vn.edu.hcmute.edux.views.iCreateQuizView;

public class CreateQuizController {
    private final iCreateQuizView view;
    private final QuizService quizService;

    public CreateQuizController(iCreateQuizView view) {
        this.view = view;
        this.quizService = new QuizService();
    }

    public void handleCreateQuiz(String userId, String classId, List<View> questionViews) {
        String title = view.getQuizTitle();
        String desc = view.getQuizDescription();

        if (title.isEmpty()) {
            view.showToast("Vui lòng nhập tên quiz");
            return;
        }

        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < questionViews.size(); i++) {
            Question q = buildQuestionFromView(questionViews.get(i), i);
            if (q == null) return; // Nếu lỗi ở 1 câu thì dừng luôn
            questions.add(q);
        }

        long duration = TimeUnit.MILLISECONDS.toMinutes(
                view.getEndTime().getTime() - view.getStartTime().getTime()
        );

        Quiz quiz = new Quiz(
                UUID.randomUUID().toString(),
                title,
                desc,
                userId,
                classId,
                view.getStartTime(),
                view.getEndTime(),
                duration,
                EQuizStatus.UPCOMING,
                questions
        );

        quizService.createQuiz(
                view.getContext(),
                quiz,
                view::finishActivity,
                e -> {
                    Log.e("CreateQuizController", "Create quiz failed", e); // e là Exception ở đây
                    String msg = (e.getMessage() != null) ? e.getMessage() : "Đã có lỗi xảy ra";
                    view.showToast("Tạo quiz thất bại: " + msg);
                }
        );
    }

    private Question buildQuestionFromView(View qView, int index) {
        EditText etQ = qView.findViewById(vn.edu.hcmute.edux.R.id.et_question);
        EditText etA = qView.findViewById(vn.edu.hcmute.edux.R.id.et_option_a);
        EditText etB = qView.findViewById(vn.edu.hcmute.edux.R.id.et_option_b);
        EditText etC = qView.findViewById(vn.edu.hcmute.edux.R.id.et_option_c);
        EditText etD = qView.findViewById(vn.edu.hcmute.edux.R.id.et_option_d);
        RadioGroup rg = qView.findViewById(vn.edu.hcmute.edux.R.id.rg_correct_answer);

        String question = etQ.getText().toString().trim();
        String a = etA.getText().toString().trim();
        String b = etB.getText().toString().trim();
        String c = etC.getText().toString().trim();
        String d = etD.getText().toString().trim();

        if (question.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
            view.showToast("Vui lòng điền đầy đủ thông tin ở câu " + (index + 1));
            return null;
        }

        int checkedId = rg.getCheckedRadioButtonId();
        if (checkedId == -1) {
            view.showToast("Chưa chọn đáp án đúng ở câu " + (index + 1));
            return null;
        }

        RadioButton selected = qView.findViewById(checkedId);
        String correct = selected.getText().toString().trim();

        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer("A", a, correct.equals("A")));
        answers.add(new Answer("B", b, correct.equals("B")));
        answers.add(new Answer("C", c, correct.equals("C")));
        answers.add(new Answer("D", d, correct.equals("D")));

        return new Question(UUID.randomUUID().toString(), question, answers);
    }
}
