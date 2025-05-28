package vn.edu.hcmute.edux.controllers.user;

import android.os.CountDownTimer;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vn.edu.hcmute.edux.activities.User.QuizActivity;
import vn.edu.hcmute.edux.model.Answer;
import vn.edu.hcmute.edux.model.Question;
import vn.edu.hcmute.edux.model.Quiz;
import vn.edu.hcmute.edux.model.Submission;
import vn.edu.hcmute.edux.services.QuizService;

public class QuizActivityController {

    private QuizActivity activity;
    private QuizService quizService;
    private Quiz quiz;
    private List<Question> questions;
    private List<String> userAnswers; // Lưu answerID (A, B, C, D)
    private int currentQuestionIndex;
    private CountDownTimer timer;
    private long timeRemainingInMillis;
    private static final long QUIZ_DURATION_MILLIS = 15 * 60 * 1000; // 15 phút (cố định)

    public QuizActivityController(QuizActivity activity) {
        this.activity = activity;
        this.quizService = new QuizService();
        this.userAnswers = new ArrayList<>();
        this.currentQuestionIndex = 0;
    }

    public void loadQuiz(String quizId) {
        quizService.getQuizById(quizId,
                loadedQuiz -> {
                    this.quiz = loadedQuiz;
                    this.questions = quiz.getQuestions();

                    // Kiểm tra thời gian hợp lệ (timeStart và timeEnd)
                    Date currentTime = new Date(); // Thời gian hiện tại: 06:16 PM +07, 28/05/2025
                    if (currentTime.before(quiz.getTimeStart())) {
                        activity.showToast("Quiz chưa bắt đầu!");
                        activity.finishActivity();
                        return;
                    }
                    if (currentTime.after(quiz.getTimeEnd())) {
                        activity.showToast("Quiz đã kết thúc!");
                        activity.finishActivity();
                        return;
                    }

                    for (int i = 0; i < questions.size(); i++) {
                        userAnswers.add(null); // Khởi tạo danh sách câu trả lời
                    }
                    startTimer();
                    displayCurrentQuestion();
                },
                error -> {
                    activity.showToast("Lỗi tải quiz: " + error.getMessage());
                    activity.finishActivity();
                }
        );
    }

    private void startTimer() {
        timeRemainingInMillis = QUIZ_DURATION_MILLIS; // Cố định 15 phút

        timer = new CountDownTimer(timeRemainingInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemainingInMillis = millisUntilFinished;
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String timeFormatted = String.format("%02d:%02d", minutes, seconds);
                activity.updateTimer(timeFormatted);

                // Kiểm tra nếu thời gian hiện tại vượt quá timeEnd
                Date currentTime = new Date();
                if (currentTime.after(quiz.getTimeEnd())) {
                    cancel(); // Hủy bộ đếm thời gian
                    onFinish(); // Tự động nộp bài
                }
            }

            @Override
            public void onFinish() {
                activity.showToast("Hết thời gian! Đang nộp bài...");
                submitQuiz(); // Tự động nộp bài khi hết 15 phút
            }
        }.start();
    }

    private void displayCurrentQuestion() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        String selectedAnswerId = userAnswers.get(currentQuestionIndex);
        activity.displayQuestion(currentQuestion, currentQuestionIndex, questions.size(), selectedAnswerId);
    }

    public void saveCurrentAnswer(String selectedAnswerId) {
        // Lưu đáp án hiện tại
        userAnswers.set(currentQuestionIndex, selectedAnswerId);
    }

    public void nextQuestion() {
        // Chuyển sang câu hỏi tiếp theo
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            displayCurrentQuestion();
        } else {
            activity.showToast("Đây là câu hỏi cuối cùng!");
        }
    }

    public void prevQuestion() {
        // Chuyển sang câu hỏi trước
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayCurrentQuestion();
        } else {
            activity.showToast("Đây là câu hỏi đầu tiên!");
        }
    }

    public void submitQuiz() {
        // Hủy bộ đếm thời gian
        if (timer != null) {
            timer.cancel();
        }

        // Kiểm tra lại timeEnd trước khi nộp bài
        Date currentTime = new Date();
        if (currentTime.after(quiz.getTimeEnd())) {
            activity.showToast("Quiz đã kết thúc! Không thể nộp bài.");
            activity.finishActivity();
            return;
        }

        // Tính điểm
        int score = calculateScore();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        long timeTaken = QUIZ_DURATION_MILLIS - timeRemainingInMillis; // Thời gian đã sử dụng (milliseconds)

        // Tạo Submission
        Submission submission = new Submission(
                userId,
                quiz.getQuizID(),
                userAnswers,
                score,
                timeTaken
        );

        // Lưu kết quả vào Firestore
        quizService.submitQuiz(submission,
                () -> {
                    activity.showToast("Nộp bài thành công! Điểm: " + score);
                    activity.finishActivity();
                },
                error -> {
                    activity.showToast("Lỗi nộp bài: " + error.getMessage());
                    activity.finishActivity();
                }
        );
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            String userAnswerId = userAnswers.get(i);
            if (userAnswerId == null) continue;

            Question question = questions.get(i);
            for (Answer answer : question.getAnswers()) {
                if (answer.getAnswerID().equals(userAnswerId) && answer.getCorrectAnswer()) {
                    score++;
                    break;
                }
            }
        }
        return score;
    }
}