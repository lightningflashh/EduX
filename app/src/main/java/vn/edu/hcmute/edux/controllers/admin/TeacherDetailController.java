package vn.edu.hcmute.edux.controllers.admin;


import vn.edu.hcmute.edux.activities.Admin.TeacherDetailActivity;
import vn.edu.hcmute.edux.model.Teacher;
import vn.edu.hcmute.edux.services.TeacherService;

public class TeacherDetailController {

    private TeacherDetailActivity activity;
    private TeacherService teacherService;
    private Teacher teacher;

    public TeacherDetailController(TeacherDetailActivity activity) {
        this.activity = activity;
        this.teacherService = new TeacherService();
    }

    public void loadTeacher(String teacherId) {
        teacherService.loadTeacherById(
                teacherId,
                loadedTeacher -> {
                    this.teacher = loadedTeacher;
                    activity.displayTeacher(teacher);
                },
                error -> {
                    activity.showToast(error);
                    activity.finishActivity();
                }
        );
    }

    public void saveChanges(String teacherId, String fullName, String email, String degree, String major) {
        // Kiểm tra đầu vào
        if (fullName.isEmpty() || email.isEmpty()) {
            activity.showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        // Cập nhật thông tin giảng viên
        teacher.setFullName(fullName);
        teacher.setEmail(email);
        teacher.setDegree(degree);
        teacher.setMajor(major);

        // Gọi Service để lưu thay đổi
        teacherService.updateTeacher(
                teacherId,
                teacher,
                success -> {
                    activity.showToast("Cập nhật giảng viên thành công!");
                    activity.disableEditMode();
                },
                error -> activity.showToast("Cập nhật thất bại: " + error)
        );
    }
}