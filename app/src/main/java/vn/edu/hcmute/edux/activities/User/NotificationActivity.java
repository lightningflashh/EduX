package vn.edu.hcmute.edux.activities.User;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;


import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.adapters.NotificationAdapter;
import vn.edu.hcmute.edux.controllers.user.NotificationController;
import vn.edu.hcmute.edux.model.Notification;

import vn.edu.hcmute.edux.utils.UserSessionManager;
import vn.edu.hcmute.edux.views.INotificationView;

public class NotificationActivity extends AppCompatActivity implements INotificationView {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private TextView titleDetail, contentDetail, classDetail, timeDetail;
    private View detailCard;
    private Button btnBackToList;
    private ImageView btnBackToolbar, btnAddNotification;

    private Map<String, String> classIdNameMap = new HashMap<>();

    private NotificationController controller;
    private List<Notification> allNotifications = new ArrayList<>();

    private String classId;
    private String role;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        controller = new NotificationController(this);

        // Gán view
        recyclerView = findViewById(R.id.recycler_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        detailCard = findViewById(R.id.card_detail);
        titleDetail = findViewById(R.id.text_detail_title);
        contentDetail = findViewById(R.id.text_detail_content);
        classDetail = findViewById(R.id.text_detail_class);
        timeDetail = findViewById(R.id.text_detail_time);

        btnBackToolbar = findViewById(R.id.btn_Back);
        btnBackToList = findViewById(R.id.btn_hide_detail);
        btnAddNotification = findViewById(R.id.fab_add_notification);

        classId = getIntent().getStringExtra("class_id");
        userId = UserSessionManager.getUserId(this);
        role = UserSessionManager.getRole(this);

        btnBackToolbar.setOnClickListener(v -> finish());
        btnBackToList.setOnClickListener(v -> detailCard.setVisibility(View.GONE));

        if ("TEACHER".equals(role)) {
            btnAddNotification.setVisibility(View.VISIBLE);
            btnAddNotification.setOnClickListener(v -> {
                startActivity(new Intent(this, CreateNotificationActivity.class));
            });
        }

        loadNotificationsByRole();
    }

    private void loadNotificationsByRole() {
        if ("TEACHER".equals(role)) {
            if ("all".equals(classId)) controller.loadNotificationsForTeacher(userId);
            else controller.loadNotificationsByClassId(classId);
        } else {
            controller.loadNotificationsForStudent(userId);
        }
    }

    @Override
    public void showNotifications(List<Notification> notifications) {
        allNotifications = notifications;
        adapter = new NotificationAdapter(allNotifications, classIdNameMap, this::showDetail);
        recyclerView.setAdapter(adapter);
    }

    private void showDetail(Notification noti) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        titleDetail.setText(noti.getTitle());
        contentDetail.setText(noti.getContent());
        classDetail.setText("Lớp: " + noti.getClassId());
        timeDetail.setText("Thời gian: " + (noti.getTimestamp() != null ? sdf.format(noti.getTimestamp()) : "Không rõ"));

        detailCard.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotificationsByRole(); // reload
    }

}