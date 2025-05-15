package vn.edu.hcmute.edux.activities.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.hcmute.edux.R;


public class SettingActivity extends AppCompatActivity {

    ImageView btnBack;

    LinearLayout btnStatic,btnFile,btnLogout,btnChangPassword,btnSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        btnBack = findViewById(R.id.btn_Back);
        btnStatic = findViewById(R.id.btn_static);
        btnFile = findViewById(R.id.btn_file);
        btnLogout = findViewById(R.id.btn_logout);
        btnChangPassword = findViewById(R.id.btn_changePassword);
        btnSupport = findViewById(R.id.btn_support);
        btnBack.setOnClickListener(v -> finish());

        btnChangPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });





    }
}