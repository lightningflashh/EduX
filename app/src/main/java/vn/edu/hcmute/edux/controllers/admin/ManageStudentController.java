package vn.edu.hcmute.edux.controllers.admin;

import android.util.Log;

import vn.edu.hcmute.edux.activities.Admin.ManageStudentActivity;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.services.StudentService;

public class ManageStudentController {

    private ManageStudentActivity activity;
    private StudentService studentService;

    public ManageStudentController(ManageStudentActivity activity) {
        this.activity = activity;
        this.studentService = new StudentService();
    }

    public void loadStudents() {
        studentService.loadStudents(
                students -> {
                    activity.updateStudentList(students);
                    Log.d("ManageStudentController", "Tải thành công " + students.size() + " sinh viên");
                },
                error -> {
                    activity.showToast(error);
                    Log.e("ManageStudentController", error);
                }
        );
    }

    public void deleteStudent(Student student) {
        studentService.deleteStudent(
                student.getUserID(),
                success -> {
                    loadStudents(); // Tải lại danh sách sau khi xóa
                    activity.showToast("Xóa sinh viên thành công!");
                },
                error -> activity.showToast(error)
        );
    }
}