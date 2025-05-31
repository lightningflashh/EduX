package vn.edu.hcmute.edux.controllers.user;


import vn.edu.hcmute.edux.activities.User.ChangePasswordActivity;
import vn.edu.hcmute.edux.services.AuthService;

public class ChangePasswordController {

    private ChangePasswordActivity activity;
    private AuthService authService;

    public ChangePasswordController(ChangePasswordActivity activity) {
        this.activity = activity;
        this.authService = new AuthService();
    }

    public void changePassword(String currentPassword, String newPassword, String confirmPassword) {
        // Kiểm tra đầu vào
        if (currentPassword.isEmpty()) {
            activity.showToast("Vui lòng nhập mật khẩu hiện tại!");
            return;
        }

        if (newPassword.isEmpty()) {
            activity.showToast("Vui lòng nhập mật khẩu mới!");
            return;
        }

        if (confirmPassword.isEmpty()) {
            activity.showToast("Vui lòng xác nhận mật khẩu mới!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            activity.showToast("Mật khẩu mới và xác nhận mật khẩu không khớp!");
            return;
        }

        if (newPassword.length() < 6) {
            activity.showToast("Mật khẩu mới phải có ít nhất 6 ký tự!");
            return;
        }

        // Gọi Service để thay đổi mật khẩu
        authService.changePassword(currentPassword, newPassword,
                () -> {
                    activity.showToast("Thay đổi mật khẩu thành công!");
                    activity.finishActivity();
                },
                error -> activity.showToast("Lỗi: " + error.getMessage())
        );
    }
}