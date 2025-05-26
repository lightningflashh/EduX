package vn.edu.hcmute.edux.views;



import java.util.List;

import vn.edu.hcmute.edux.model.Subject;

public interface INotificationCreateView {
    void showSubjects(List<Subject> subjects);
    void showError(String message);
    void showMessage(String message);
    void finishActivity();
}
