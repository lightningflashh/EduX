package vn.edu.hcmute.edux.controllers.user;

import com.google.firebase.firestore.FirebaseFirestore;

import vn.edu.hcmute.edux.services.StudentService;
import vn.edu.hcmute.edux.services.TeacherService;
import vn.edu.hcmute.edux.utils.UserSessionManager;
import vn.edu.hcmute.edux.views.iMainView;

public class MainController {
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final iMainView view;

    public MainController(iMainView view) {
        this.view = view;
        this.studentService = new StudentService();
        this.teacherService = new TeacherService();
    }

    public void loadUserInfo(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("teachers").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && "TEACHER".equals(doc.getString("role"))) {
                        UserSessionManager.saveSession(view.getContext(), uid, "TEACHER");
                        loadTeacher(uid);
                    } else {
                        db.collection("students").document(uid).get()
                                .addOnSuccessListener(stuDoc -> {
                                    if (stuDoc.exists() && "STUDENT".equals(stuDoc.getString("role"))) {
                                        UserSessionManager.saveSession(view.getContext(), uid, "STUDENT");
                                        loadStudent(uid);
                                    } else {
                                        view.showToast("Không tìm thấy người dùng");
                                    }
                                });
                    }
                }).addOnFailureListener(e -> view.showToast("Lỗi khi kiểm tra quyền: " + e.getMessage()));
    }

    public void loadStudent(String uid) {
        studentService.loadStudentById(uid, view::displayStudent, err -> view.showToast("Lỗi: " + err));
        studentService.loadSubjects(uid, "", view::updateSubjectList, err -> view.showToast("Lỗi: " + err.getMessage()));
    }

    public void loadTeacher(String uid) {
        teacherService.loadTeacherById(uid, view::displayTeacher, err -> view.showToast("Lỗi: " + err));
        teacherService.loadSubjects(uid, "", view::updateSubjectList, err -> view.showToast("Lỗi: " + err.getMessage()));
    }
}