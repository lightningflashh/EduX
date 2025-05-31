package vn.edu.hcmute.edux.views;


import java.util.List;
import vn.edu.hcmute.edux.model.Notification;

public interface INotificationView {
    void showNotifications(List<Notification> notifications);
    void showError(String message);
    void showMessage(String message);
}

