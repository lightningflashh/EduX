package vn.edu.hcmute.edux.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.Subject;


public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private List<Subject> subjectList;
    private List<Subject> subjectListFull;
    private OnItemClickListener onItemClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    // Interface để xử lý sự kiện nhấn vào item
    public interface OnItemClickListener {
        void onItemClick(Subject subject);
    }

    // Interface để xử lý sự kiện nhấn nút xóa
    public interface OnDeleteClickListener {
        void onDeleteClick(Subject subject);
    }

    public SubjectAdapter(List<Subject> subjectList) {
        this.subjectList = new ArrayList<>(subjectList);
        this.subjectListFull = new ArrayList<>(subjectList);
        Log.d("SubjectAdapter", "Khởi tạo adapter với " + this.subjectList.size() + " môn học");
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
        holder.tvSubjectName.setText(subject.getClassName());
        holder.tvSubjectId.setText("Mã môn: " + (subject.getClassId() != null ? subject.getClassId() : "Chưa xác định"));
        holder.tvSubjectDescription.setText("Mô tả: " + (subject.getDescription() != null ? subject.getDescription() : "Chưa xác định"));
        Log.d("SubjectAdapter", "Hiển thị môn học: " + subject.getClassName() + " tại vị trí " + position);

        // Xử lý sự kiện nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(subject);
            }
        });

        // Xử lý sự kiện nhấn nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(subject);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("SubjectAdapter", "Số lượng môn học hiển thị: " + subjectList.size());
        return subjectList.size();
    }

    public void filter(String text) {
        subjectList.clear();
        if (text.isEmpty()) {
            subjectList.addAll(subjectListFull);
        } else {
            text = text.toLowerCase();
            for (Subject subject : subjectListFull) {
                if (subject.getClassName().toLowerCase().contains(text) ||
                        (subject.getClassId() != null && subject.getClassId().toLowerCase().contains(text)) ||
                        (subject.getDescription() != null && subject.getDescription().toLowerCase().contains(text))) {
                    subjectList.add(subject);
                }
            }
        }
        notifyDataSetChanged();
        Log.d("SubjectAdapter", "Lọc danh sách, số lượng sau lọc: " + subjectList.size());
    }

    public void updateList(List<Subject> newList) {
        subjectList = new ArrayList<>(newList);
        subjectListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
        Log.d("SubjectAdapter", "Cập nhật danh sách, số lượng: " + subjectList.size());
        for (Subject subject : subjectList) {
            Log.d("SubjectAdapter", "Môn học trong danh sách: " + subject.getClassName());
        }
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
