package vn.edu.hcmute.edux.services;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminService {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public AdminService() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public String getCurrentUserEmail() {
        String email = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "null";
        Log.d("AdminService", "Current user: " + email);
        return email;
    }

    public void getStudentCount(CountCallback callback) {
        db.collection("students")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        callback.onComplete(snapshot.size(), null);
                    } else {
                        callback.onComplete(0, task.getException().getMessage());
                    }
                });
    }

    public void getTeacherCount(CountCallback callback) {
        db.collection("teachers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        callback.onComplete(snapshot.size(), null);
                    } else {
                        callback.onComplete(0, task.getException().getMessage());
                    }
                });
    }

    public void getSubjectCount(CountCallback callback) {
        db.collection("subjects")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        callback.onComplete(snapshot.size(), null);
                    } else {
                        callback.onComplete(0, task.getException().getMessage());
                    }
                });
    }

    public void logout() {
        auth.signOut();
    }

    public void createStudentAccount(String email, String password, String studentCode, String fullName, StudentCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userID = auth.getCurrentUser().getUid();

                        // Tạo đối tượng Student
                        Map<String, Object> student = new HashMap<>();
                        student.put("userID", userID);
                        student.put("email", email);
                        student.put("studentCode", studentCode);
                        student.put("fullName", fullName);
                        student.put("password", password); // Lưu ý: Không nên lưu mật khẩu, chỉ để minh họa
                        student.put("role", "STUDENT");
                        student.put("imageUrl", "");
                        student.put("birthDate", null);
                        student.put("isActive", true);
                        student.put("createdAt", new Date());
                        student.put("updatedAt", new Date());
                        student.put("studyTime", null);
                        student.put("major", null);

                        // Lưu thông tin sinh viên vào Firestore
                        db.collection("students").document(userID)
                                .set(student)
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        callback.onComplete(true, null);
                                    } else {
                                        callback.onComplete(false, "Lưu thông tin thất bại: " + task2.getException().getMessage());
                                    }
                                });
                    } else {
                        callback.onComplete(false, "Tạo tài khoản thất bại: " + task.getException().getMessage());
                    }
                });
    }

    // Callback interface cho việc tạo tài khoản
    public interface StudentCallback {
        void onComplete(boolean success, String message);
    }

    public interface CountCallback {
        void onComplete(int count, String error);
    }
}