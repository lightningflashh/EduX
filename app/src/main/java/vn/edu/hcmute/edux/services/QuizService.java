package vn.edu.hcmute.edux.services;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import vn.edu.hcmute.edux.model.Quiz;
import vn.edu.hcmute.edux.model.Submission;

public class QuizService {

    private final FirebaseFirestore firestore;

    public QuizService() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void createQuiz(Context context, Quiz quiz, Runnable onSuccess, Consumer<Exception> onFailure) {
        firestore.collection("quizzes").document(quiz.getQuizID())
                .set(quiz)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Tạo quiz thành công!", Toast.LENGTH_SHORT).show();
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (onFailure != null) onFailure.accept(e);
                });
    }

    public void getQuizzesByClassId(@NonNull String classId, @NonNull Consumer<List<Quiz>> onSuccess, @NonNull Consumer<Exception> onFailure) {
        firestore.collection("quizzes")
                .whereEqualTo("classID", classId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Quiz> quizzes = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Quiz quiz = doc.toObject(Quiz.class);
                        quizzes.add(quiz);
                    }
                    onSuccess.accept(quizzes);
                })
                .addOnFailureListener(onFailure::accept);
    }

    public void getQuizById(String quizId, Consumer<Quiz> onSuccess, Consumer<Exception> onFailure) {
        firestore.collection("quizzes").document(quizId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Quiz quiz = document.toObject(Quiz.class);
                        if (quiz != null) {
                            onSuccess.accept(quiz);
                        } else {
                            onFailure.accept(new Exception("Dữ liệu quiz không hợp lệ"));
                        }
                    } else {
                        onFailure.accept(new Exception("Quiz không tồn tại"));
                    }
                })
                .addOnFailureListener(onFailure::accept);
    }

    public void submitQuiz(Submission submission, Runnable onSuccess, Consumer<Exception> onFailure) {
        String submissionId = UUID.randomUUID().toString();
        firestore.collection("submissions").document(submissionId)
                .set(submission)
                .addOnSuccessListener(unused -> {
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(onFailure::accept);
    }
}