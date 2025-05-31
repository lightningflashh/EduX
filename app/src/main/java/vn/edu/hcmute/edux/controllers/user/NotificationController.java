package vn.edu.hcmute.edux.controllers.user;



import android.util.Log;

import java.util.*;
import java.util.function.Consumer;

import vn.edu.hcmute.edux.model.Notification;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.services.NotificationService;
import vn.edu.hcmute.edux.services.StudentService;
import vn.edu.hcmute.edux.services.TeacherService;
import vn.edu.hcmute.edux.views.INotificationView;

public class NotificationController {

    private final INotificationView view;
    private final NotificationService notificationService = new NotificationService();
    private final StudentService studentService = new StudentService();
    private final TeacherService teacherService = new TeacherService();

    public NotificationController(INotificationView view) {
        this.view = view;
    }

    public void loadNotificationsForTeacher(String teacherId) {
        teacherService.loadSubjects(teacherId, null, subjects -> {
            List<Notification> all = new ArrayList<>();
            Set<String> loadedIds = new HashSet<>();

            for (Subject subject : subjects) {
                notificationService.loadNotificationsByClassId(subject.getClassId(), notifications -> {
                    for (Notification noti : notifications) {
                        if (loadedIds.add(noti.getId())) {
                            all.add(noti);
                        }
                    }
                    view.showNotifications(all);
                }, err -> view.showError("Lỗi: " + err));
            }
        }, err -> view.showError("Lỗi: " + err.getMessage()));
    }

    public void loadNotificationsForStudent(String studentId) {
        studentService.loadSubjects(studentId, null, subjects -> {
            List<Notification> all = new ArrayList<>();
            Set<String> loadedIds = new HashSet<>();

            for (Subject subject : subjects) {
                notificationService.loadNotificationsByClassId(subject.getClassId(), notifications -> {
                    for (Notification noti : notifications) {
                        if (loadedIds.add(noti.getId())) {
                            all.add(noti);
                        }
                    }
                    view.showNotifications(all);
                }, err -> view.showError("Lỗi: " + err));
            }
        }, err -> view.showError("Lỗi: " + err.getMessage()));
    }

    public void loadNotificationsByClassId(String classId) {
        notificationService.loadNotificationsByClassId(classId, view::showNotifications,
                err -> view.showError("Lỗi: " + err));
    }

    // THÊM phần này vào NotificationController đã có của bạn

    public void loadSubjectsForCreation(String teacherId, Consumer<List<Subject>> onSuccess, Consumer<String> onError) {
        teacherService.loadSubjects(teacherId, null, onSuccess,
                error -> onError.accept("Không thể tải danh sách lớp: " + error.getMessage()));
    }

    public void createNotification(String title, String content, Subject subject,
                                   Runnable onSuccess, Consumer<String> onError) {
        if (title == null || title.trim().isEmpty() ||
                content == null || content.trim().isEmpty() ||
                subject == null) {
            onError.accept("Vui lòng nhập đủ thông tin");
            return;
        }

        notificationService.createNotification(title, content, subject.getClassId(),
                unused -> onSuccess.run(),
                onError);
    }

}
