package vn.edu.hcmute.edux.controllers.admin;

import vn.edu.hcmute.edux.activities.Admin.AddStudentActivity;
import vn.edu.hcmute.edux.services.AdminService;

public class AddStudentController {

    private AddStudentActivity activity;
    private AdminService service;

    public AddStudentController(AddStudentActivity activity) {
        this.activity = activity;
        this.service = new AdminService();
    }

    public void addStudent(String email, String password, String studentCode, String fullName) {
        // Kiểm tra đầu vào
        if (email.isEmpty() || password.isEmpty() || studentCode.isEmpty() || fullName.isEmpty()) {
            activity.showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        // Gọi Service để tạo tài khoản và lưu thông tin sinh viên
        service.createStudentAccount(email, password, studentCode, fullName, (success, message) -> {
            if (success) {
                activity.showToast("Thêm sinh viên thành công!");
                activity.navigateBack();
            } else {
                activity.showToast(message);
            }
        });
    }
}