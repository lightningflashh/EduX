package vn.edu.hcmute.edux.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.activities.User.FacialRecognitionActivity;
import vn.edu.hcmute.edux.model.Quiz;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private final List<Quiz> quizList;

    public QuizAdapter(List<Quiz> quizList) {
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        holder.quizTitle.setText(quiz.getTitle());
        holder.quizDuration.setText("Thời lượng: " + quiz.getDuration() + " phút");

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, FacialRecognitionActivity.class);
            intent.putExtra("quizTitle", quiz.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitle, quizDuration;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.tvQuizTitle);
            quizDuration = itemView.findViewById(R.id.tvQuizTime);
        }
    }
}
