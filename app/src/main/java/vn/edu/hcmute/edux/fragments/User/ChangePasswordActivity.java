package vn.edu.hcmute.edux.fragments.User;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import vn.edu.hcmute.edux.R;


public class ChangePasswordActivity extends AppCompatActivity {

    ImageView btnBack;
    EditText edNewPassword, edConfirmPassword;
    MaterialButton btnConfirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_password);

        btnBack = findViewById(R.id.btn_back_change_password);
        edNewPassword = findViewById(R.id.ed_new_password);
        edConfirmPassword = findViewById(R.id.ed_confirm_password);
        btnConfirmPassword = findViewById(R.id.btn_confirm_password);


        btnBack.setOnClickListener(v -> finish());



    }
}