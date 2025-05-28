package vn.edu.hcmute.edux.controllers.admin;

import vn.edu.hcmute.edux.activities.Admin.AddTeacherActivity;
import vn.edu.hcmute.edux.services.TeacherService;

public class AddTeacherController {

    private AddTeacherActivity activity;
    private TeacherService service;

    public AddTeacherController(AddTeacherActivity activity) {
        this.activity = activity;
        this.service = new TeacherService();
    }

    public void addTeacher(String email, String password, String fullName, String major) {
        // Kiểm tra đầu vào
        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            activity.showToast("Vui lòng điền đầy đủ thông tin bắt buộc");
            return;
        }

        if (password.length() < 6) {
            activity.showToast("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        // Gọi Service để tạo tài khoản và lưu thông tin giảng viên
        service.createTeacherAccount(email, password, fullName, major, (success, message) -> {
            if (success) {
                activity.showToast("Thêm giảng viên thành công!");
                activity.navigateBack();
            } else {
                activity.showToast(message);
            }
        });
    }
}