package vn.edu.hcmute.edux.views;

import android.content.Context;

import java.util.List;

import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.model.Teacher;

public interface iMainView {
    void displayStudent(Student student);
    void displayTeacher(Teacher teacher);
    void updateSubjectList(List<Subject> subjects);
    void showToast(String message);
    Context getContext();
}
