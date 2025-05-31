package vn.edu.hcmute.edux.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.activities.User.CreateQuizActivity;
import vn.edu.hcmute.edux.adapters.QuizAdapter;
import vn.edu.hcmute.edux.controllers.user.QuizFragmentController;
import vn.edu.hcmute.edux.model.Quiz;

public class QuizFragment extends Fragment {

    private Button btnCreateQuiz;
    private RecyclerView recyclerView;
    private QuizAdapter adapter;
    private List<Quiz> quizList = new ArrayList<>();
    private String classId;
    private QuizFragmentController controller;

    public QuizFragment() {
        super(R.layout.fragment_quiz);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classId = getArguments() != null ? getArguments().getString("class_id") : null;
        if (classId == null) {
            showToast("Không tìm thấy lớp học!");
            return;
        }

        // Khởi tạo Controller
        controller = new QuizFragmentController(this);

        init(view);

        // Kiểm tra vai trò người dùng và điều chỉnh hiển thị nút "Tạo Quiz"
        controller.checkUserRole();

        btnCreateQuiz.setOnClickListener(v -> controller.handleCreateQuizButtonClick(classId));

        controller.loadQuizListByClass(classId);
    }

    private void init(View view) {
        btnCreateQuiz = view.findViewById(R.id.btnCreateQuiz);
        recyclerView = view.findViewById(R.id.item_quiz);

        adapter = new QuizAdapter(quizList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    // Phương thức để Controller cập nhật giao diện
    public void updateQuizList(List<Quiz> quizzes) {
        quizList.clear();
        quizList.addAll(quizzes);
        adapter.notifyDataSetChanged();
    }

    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void navigateToCreateQuizActivity(String classId) {
        Intent intent = new Intent(getActivity(), CreateQuizActivity.class);
        intent.putExtra("class_id", classId);
        startActivity(intent);
    }

    public void setCreateQuizButtonVisibility(boolean isVisible) {
        btnCreateQuiz.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (classId != null && adapter != null) {
            controller.loadQuizListByClass(classId);
        }
    }
}