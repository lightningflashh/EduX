package vn.edu.hcmute.edux.activities.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.activities.LoginActivity;


public class ForgotPasswordActivity extends AppCompatActivity {

    ConstraintLayout btnResetPassword,txBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        txBack = findViewById(R.id.tx_back_forgot);

        txBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}