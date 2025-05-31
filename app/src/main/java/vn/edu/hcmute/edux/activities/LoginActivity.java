package vn.edu.hcmute.edux.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.activities.Admin.AdminActivity;
import vn.edu.hcmute.edux.activities.User.ForgotPasswordActivity;
import vn.edu.hcmute.edux.activities.User.MainActivity;

public class LoginActivity extends AppCompatActivity {

    TextView tvTitle, tvSubtitle, txForgotPassword;
    ConstraintLayout btnSignIn;
    EditText etEmail, etPassword;
    LottieAnimationView video_intro;
    Animation topAnim, botAnim;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Load animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.anim_down);
        botAnim = AnimationUtils.loadAnimation(this, R.anim.anim_up);

        // Ánh xạ các view từ layout
        txForgotPassword = findViewById(R.id.tx_forgot_password);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvTitle = findViewById(R.id.tvTitle);
        video_intro = findViewById(R.id.video_intro);
        btnSignIn = findViewById(R.id.btn_sign_in);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // Áp dụng animation
        tvSubtitle.setAnimation(topAnim);
        tvTitle.setAnimation(topAnim);
        video_intro.setAnimation(botAnim);

        // Xử lý sự kiện khi nhấn nút Sign In
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Kiểm tra đầu vào
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                signInWithEmail(email, password);
            }
        });

        // Xử lý sự kiện khi nhấn "Forgot Password"
        txForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent;
                            if ("admin@gmail.com".equals(email)) {
                                intent = new Intent(LoginActivity.this, AdminActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}