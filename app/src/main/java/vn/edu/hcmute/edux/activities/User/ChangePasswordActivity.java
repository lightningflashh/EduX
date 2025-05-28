package vn.edu.hcmute.edux.activities.User;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.controllers.user.ChangePasswordController;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText edCurrentPassword, edNewPassword, edConfirmPassword;
    private MaterialButton btnConfirmPassword;
    private ChangePasswordController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_password);

        // Khởi tạo Controller
        controller = new ChangePasswordController(this);

        // Ánh xạ các view từ layout
        btnBack = findViewById(R.id.btn_back_change_password);
        edCurrentPassword = findViewById(R.id.ed_current_password);
        edNewPassword = findViewById(R.id.ed_new_password);
        edConfirmPassword = findViewById(R.id.ed_confirm_password);
        btnConfirmPassword = findViewById(R.id.btn_confirm_password);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Confirm
        btnConfirmPassword.setOnClickListener(v -> {
            String currentPassword = edCurrentPassword.getText().toString().trim();
            String newPassword = edNewPassword.getText().toString().trim();
            String confirmPassword = edConfirmPassword.getText().toString().trim();
            controller.changePassword(currentPassword, newPassword, confirmPassword);
        });
    }

    // Phương thức để Controller hiển thị thông báo
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Phương thức để Controller kết thúc Activity
    public void finishActivity() {
        finish();
    }
}