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
import vn.edu.hcmute.edux.model.Quiz;
import vn.edu.hcmute.edux.services.QuizService;

public class QuizFragment extends Fragment {

    private Button btnCreateQuiz;
    private RecyclerView recyclerView;
    private QuizAdapter adapter;
    private List<Quiz> quizList = new ArrayList<>();
    private String classId;

    public QuizFragment() {
        super(R.layout.fragment_quiz);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classId = getArguments() != null ? getArguments().getString("class_id") : null;
        if (classId == null) {
            Toast.makeText(getContext(), "Không tìm thấy lớp học!", Toast.LENGTH_SHORT).show();
            return;
        }

        init(view);

        btnCreateQuiz.setOnClickListener(v -> handleCreateQuizButtonClick(classId));

        loadQuizListByClass(classId);
    }

    private void init(View view) {
        btnCreateQuiz = view.findViewById(R.id.btnCreateQuiz);
        recyclerView = view.findViewById(R.id.item_quiz);

        adapter = new QuizAdapter(quizList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void handleCreateQuizButtonClick(String classId) {
        Intent intent = new Intent(getActivity(), CreateQuizActivity.class);
        intent.putExtra("class_id", classId);
        startActivity(intent);
    }

    private void loadQuizListByClass(String classId) {
        QuizService quizService = new QuizService();
        quizService.getQuizzesByClassId(
                classId,
                quizzes -> {
                    quizList.clear();
                    quizList.addAll(quizzes);
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Toast.makeText(getContext(), "Lỗi tải quiz: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (classId != null && adapter != null) {
            loadQuizListByClass(classId);
        }
    }

}

