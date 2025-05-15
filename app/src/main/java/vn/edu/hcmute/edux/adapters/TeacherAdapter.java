package vn.edu.hcmute.edux.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.Teacher;


public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private List<Teacher> teacherList;
    private List<Teacher> teacherListFull;
    private OnItemClickListener onItemClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(Teacher teacher);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Teacher teacher);
    }

    public TeacherAdapter(List<Teacher> teacherList) {
        this.teacherList = new ArrayList<>(teacherList);
        this.teacherListFull = new ArrayList<>(teacherList);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_item, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = teacherList.get(position);
        holder.tvTeacherName.setText(teacher.getFullName() != null ? teacher.getFullName() : "Không có tên");
        holder.tvTeacherEmail.setText("Email: " + (teacher.getEmail() != null ? teacher.getEmail() : "Không có email"));

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(teacher);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(teacher);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public void updateList(List<Teacher> newList) {
        this.teacherList = new ArrayList<>(newList);
        this.teacherListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        teacherList.clear();
        if (query.isEmpty()) {
            teacherList.addAll(teacherListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            teacherList.addAll(teacherListFull.stream()
                    .filter(teacher -> (teacher.getFullName() != null && teacher.getFullName().toLowerCase().contains(lowerCaseQuery)) ||
                            (teacher.getEmail() != null && teacher.getEmail().toLowerCase().contains(lowerCaseQuery)))
                    .collect(Collectors.toList()));
        }
        notifyDataSetChanged();
    }

    static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView tvTeacherName, tvTeacherEmail;
        ImageView btnDelete;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTeacherName = itemView.findViewById(R.id.tvTeacherName);
            tvTeacherEmail = itemView.findViewById(R.id.tvTeacherEmail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
