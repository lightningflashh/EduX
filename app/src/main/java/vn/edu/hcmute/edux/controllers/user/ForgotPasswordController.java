package vn.edu.hcmute.edux.controllers.user;

import android.util.Patterns;

import vn.edu.hcmute.edux.activities.User.ForgotPasswordActivity;
import vn.edu.hcmute.edux.services.AuthService;

public class ForgotPasswordController {

    private ForgotPasswordActivity activity;
    private AuthService authService;

    public ForgotPasswordController(ForgotPasswordActivity activity) {
        this.activity = activity;
        this.authService = new AuthService();
    }

    public void handleResetPassword(String email) {
        // Kiểm tra tính hợp lệ của email
        if (email.isEmpty()) {
            activity.showToast("Vui lòng nhập email!");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            activity.showToast("Email không hợp lệ!");
            return;
        }

        // Gọi Service để gửi email đặt lại mật khẩu
        authService.sendPasswordResetEmail(email,
                () -> {
                    activity.showToast("Email đặt lại mật khẩu đã được gửi! Vui lòng kiểm tra hộp thư của bạn.");
                    activity.navigateToLoginActivity();
                },
                error -> activity.showToast("Lỗi: " + error.getMessage())
        );
    }
}