package vn.edu.hcmute.edux.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.Subject;
import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    private List<Subject> subjectList = new ArrayList<>();
    private List<Subject> subjectListFull = new ArrayList<>(); // Danh sách đầy đủ để lọc
    private OnItemClickListener onItemClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(Subject subject);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Subject subject);
    }

    public SubjectAdapter(List<Subject> subjectList) {
        this.subjectList = new ArrayList<>(subjectList);
        this.subjectListFull = new ArrayList<>(subjectList);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_item, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.tvSubjectName.setText(subject.getClassName() != null ? subject.getClassName() : "Chưa xác định");
        holder.tvSubjectId.setText(subject.getClassId() != null ? "Mã môn: " + subject.getClassId() : "Mã môn: Chưa xác định");
        holder.tvSubjectDescription.setText(subject.getDescription() != null ? "Mô tả: " + subject.getDescription() : "Mô tả: Chưa xác định");

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(subject);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(subject);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public void updateList(List<Subject> newList) {
        this.subjectList = new ArrayList<>(newList);
        this.subjectListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        subjectList.clear();
        if (text.isEmpty()) {
            subjectList.addAll(subjectListFull);
        } else {
            text = text.toLowerCase();
            for (Subject subject : subjectListFull) {
                if ((subject.getClassName() != null && subject.getClassName().toLowerCase().contains(text)) ||
                        (subject.getClassId() != null && subject.getClassId().toLowerCase().contains(text)) ||
                        (subject.getDescription() != null && subject.getDescription().toLowerCase().contains(text))) {
                    subjectList.add(subject);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvSubjectId, tvSubjectDescription;
        ImageView btnDelete;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvSubjectId = itemView.findViewById(R.id.tvSubjectId);
            tvSubjectDescription = itemView.findViewById(R.id.tvSubjectDescription);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}