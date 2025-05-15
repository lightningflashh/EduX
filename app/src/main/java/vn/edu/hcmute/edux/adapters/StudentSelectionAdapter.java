package vn.edu.hcmute.edux.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.Student;


public class StudentSelectionAdapter extends RecyclerView.Adapter<StudentSelectionAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private List<Student> selectedStudents;

    public StudentSelectionAdapter(List<Student> studentList) {
        this.studentList = studentList;
        this.selectedStudents = new ArrayList<>();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_selection_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.tvStudentName.setText(student.getFullName());
        holder.tvStudentCode.setText("Mã SV: " + student.getStudentCode());
        holder.cbStudent.setChecked(selectedStudents.contains(student));

        holder.cbStudent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedStudents.contains(student)) {
                    selectedStudents.add(student);
                }
            } else {
                selectedStudents.remove(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public List<Student> getSelectedStudents() {
        return new ArrayList<>(selectedStudents);
    }

    public void setSelected(Student student, boolean isSelected) {
        if (isSelected) {
            if (!selectedStudents.contains(student)) {
                selectedStudents.add(student);
            }
        } else {
            selectedStudents.remove(student);
        }
        notifyDataSetChanged();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvStudentCode;
        CheckBox cbStudent;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentCode = itemView.findViewById(R.id.tvStudentCode);
            cbStudent = itemView.findViewById(R.id.cbStudent);
        }
    }
}
