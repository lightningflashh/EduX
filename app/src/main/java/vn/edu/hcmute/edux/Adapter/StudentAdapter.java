package vn.edu.hcmute.edux.Adapter;

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
import vn.edu.hcmute.edux.model.Student;


public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private List<Student> studentListFull;
    private OnItemClickListener onItemClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(Student student);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Student student);
    }

    public StudentAdapter(List<Student> studentList) {
        this.studentList = new ArrayList<>(studentList);
        this.studentListFull = new ArrayList<>(studentList);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.tvStudentName.setText(student.getFullName() != null ? student.getFullName() : "Không có tên");
        holder.tvStudentCode.setText("Mã SV: " + (student.getStudentCode() != null ? student.getStudentCode() : "Không có mã"));

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(student);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void updateList(List<Student> newList) {
        this.studentList = new ArrayList<>(newList);
        this.studentListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        studentList.clear();
        if (query.isEmpty()) {
            studentList.addAll(studentListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            studentList.addAll(studentListFull.stream()
                    .filter(student -> (student.getFullName() != null && student.getFullName().toLowerCase().contains(lowerCaseQuery)) ||
                            (student.getStudentCode() != null && student.getStudentCode().toLowerCase().contains(lowerCaseQuery)))
                    .collect(Collectors.toList()));
        }
        notifyDataSetChanged();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvStudentCode;
        ImageView btnDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentCode = itemView.findViewById(R.id.tvStudentCode);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
