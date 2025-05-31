package vn.edu.hcmute.edux.controllers.admin;


import vn.edu.hcmute.edux.activities.Admin.StudentDetailActivity;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.services.StudentService;

public class StudentDetailController {

    private StudentDetailActivity activity;
    private StudentService studentService;
    private Student student;

    public StudentDetailController(StudentDetailActivity activity) {
        this.activity = activity;
        this.studentService = new StudentService();
    }

    public void loadStudent(String studentId) {
        studentService.loadStudentById(
                studentId,
                student -> {
                    this.student = student;
                    activity.displayStudent(student);
                },
                error -> {
                    activity.showToast(error);
                    activity.finishActivity();
                }
        );
    }

    public void saveChanges(String studentId, String fullName, String email, String studentCode, String major) {
        // Kiểm tra đầu vào
        if (fullName.isEmpty() || email.isEmpty() || studentCode.isEmpty()) {
            activity.showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        // Cập nhật thông tin sinh viên
        student.setFullName(fullName);
        student.setEmail(email);
        student.setStudentCode(studentCode);
        student.setMajor(major);

        // Gọi Service để lưu thay đổi
        studentService.updateStudent(
                studentId,
                student,
                success -> {
                    activity.showToast("Cập nhật sinh viên thành công!");
                    activity.disableEditMode();
                },
                error -> activity.showToast("Cập nhật thất bại: " + error)
        );
    }
}