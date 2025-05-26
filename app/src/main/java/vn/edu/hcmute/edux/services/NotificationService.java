package vn.edu.hcmute.edux.services;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import vn.edu.hcmute.edux.model.Notification;

public class NotificationService {
    private final FirebaseFirestore db;

    public NotificationService() {
        db = FirebaseFirestore.getInstance();
    }

    public void createNotification(String title, String content, String classId, Consumer<Void> onSuccess, Consumer<String> onFailure) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("content", content);
        data.put("classId", classId);
        data.put("timestamp", new Date());

        db.collection("notifications")
                .add(data)
                .addOnSuccessListener(documentReference -> onSuccess.accept(null))
                .addOnFailureListener(e -> onFailure.accept("Lỗi tạo thông báo: " + e.getMessage()));
    }

    public void loadNotificationsByClassId(String classId, Consumer<List<Notification>> onSuccess, Consumer<String> onFailure) {
        db.collection("notifications")
                .whereEqualTo("classId", classId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Notification> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Notification notification = doc.toObject(Notification.class);
                        notification = new Notification(
                                doc.getId(),
                                notification.getTitle(),
                                notification.getContent(),
                                notification.getClassId(),
                                notification.getTimestamp()
                        );
                        list.add(notification);
                    }
                    onSuccess.accept(list);
                })
                .addOnFailureListener(e -> onFailure.accept("Lỗi tải thông báo: " + e.getMessage()));
    }

    public void getAllNotifications(Consumer<List<Notification>> onSuccess, Consumer<String> onFailure) {
        db.collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Notification> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Notification notification = doc.toObject(Notification.class);
                        notification = new Notification(
                                doc.getId(),
                                notification.getTitle(),
                                notification.getContent(),
                                notification.getClassId(),
                                notification.getTimestamp()
                        );
                        list.add(notification);
                    }
                    onSuccess.accept(list);
                })
                .addOnFailureListener(e -> onFailure.accept(e.getMessage()));
    }

}
