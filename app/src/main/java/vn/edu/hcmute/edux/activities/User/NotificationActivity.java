package vn.edu.hcmute.edux.activities.User;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.hcmute.edux.R;


public class NotificationActivity extends AppCompatActivity {

    TextView titleTextView, contentTextView;

    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        titleTextView = findViewById(R.id.text_detail_title);
        contentTextView = findViewById(R.id.text_detail_content);
        btnBack = findViewById(R.id.btn_Back);
        btnBack.setOnClickListener(v -> finish());

        // Nhận dữ liệu truyền từ MainActivity
        int notificationId = getIntent().getIntExtra("notification_id", -1);
        String title = getIntent().getStringExtra("notification_title");
        String content = getIntent().getStringExtra("notification_content");

        if (title != null && content != null) {
            titleTextView.setText(title);
            contentTextView.setText(content);
        } else {
            titleTextView.setText("Không tìm thấy thông báo");
            contentTextView.setText("ID: " + notificationId);
        }
    }
}
