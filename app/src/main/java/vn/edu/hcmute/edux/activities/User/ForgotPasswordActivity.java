package vn.edu.hcmute.edux.activities.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.activities.LoginActivity;
import vn.edu.hcmute.edux.controllers.user.ForgotPasswordController;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ConstraintLayout btnResetPassword, txBack;
    private EditText editEmail;
    private ForgotPasswordController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Khởi tạo Controller
        controller = new ForgotPasswordController(this);

        // Ánh xạ các view từ layout
        btnResetPassword = findViewById(R.id.btn_reset_password);
        txBack = findViewById(R.id.tx_back_forgot);
        editEmail = findViewById(R.id.edit_email);

        // Xử lý nút Back
        txBack.setOnClickListener(v -> navigateToLoginActivity());

        // Xử lý nút Reset Password
        btnResetPassword.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            controller.handleResetPassword(email);
        });
    }

    // Phương thức để Controller điều hướng về LoginActivity
    public void navigateToLoginActivity() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Phương thức để Controller hiển thị thông báo
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}