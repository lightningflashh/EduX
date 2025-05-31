package vn.edu.hcmute.edux.services;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import vn.edu.hcmute.edux.model.EUserRole;
import vn.edu.hcmute.edux.model.StudyMaterial;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.model.Teacher;

public class TeacherService {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public TeacherService() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void loadTeachers(@NonNull Consumer<List<Teacher>> onSuccess, @NonNull Consumer<String> onFailure) {
        List<Teacher> teacherList = new ArrayList<>();
        db.collection("teachers")
                .whereEqualTo("role", EUserRole.TEACHER.toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            Date birthDate = data.get("birthDate") instanceof com.google.firebase.Timestamp ?
                                    ((com.google.firebase.Timestamp) data.get("birthDate")).toDate() : null;
                            Date createdAt = data.get("createdAt") instanceof com.google.firebase.Timestamp ?
                                    ((com.google.firebase.Timestamp) data.get("createdAt")).toDate() : null;
                            Date updatedAt = data.get("updatedAt") instanceof com.google.firebase.Timestamp ?
                                    ((com.google.firebase.Timestamp) data.get("updatedAt")).toDate() : null;

                            // Chuyển đổi teachingClasses từ List<Map> sang List<Subject>
                            List<Map<String, Object>> teachingClassesRaw = (List<Map<String, Object>>) data.get("teachingClasses");
                            List<Subject> teachingClasses = new ArrayList<>();
                            if (teachingClassesRaw != null) {
                                for (Map<String, Object> classData : teachingClassesRaw) {
                                    Subject subject = new Subject(
                                            (String) classData.get("classId"),
                                            (String) classData.get("className"),
                                            (String) classData.get("description"),
                                            (List<StudyMaterial>) classData.get("materials")
                                    );
                                    teachingClasses.add(subject);
                                }
                            }

                            Teacher teacher = new Teacher(
                                    (String) data.get("userID"),
                                    (String) data.get("fullName"),
                                    (String) data.get("email"),
                                    (String) data.get("password"),
                                    EUserRole.valueOf((String) data.get("role")),
                                    (String) data.get("imageUrl"),
                                    birthDate,
                                    (Boolean) data.get("isActive"),
                                    createdAt,
                                    updatedAt,
                                    (String) data.get("degree"),
                                    teachingClasses,
                                    (String) data.get("major")
                            );
                            teacherList.add(teacher);
                        }
                        onSuccess.accept(teacherList);
                    } else {
                        onFailure.accept("Lỗi khi tải danh sách giảng viên: " + task.getException().getMessage());
                    }
                });
    }

    public void loadTeacherById(String userId, @NonNull Consumer<Teacher> onSuccess, @NonNull Consumer<String> onFailure) {
        db.collection("teachers")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();

                            Date birthDate = data.containsKey("birthDate") && data.get("birthDate") instanceof com.google.firebase.Timestamp ?
                                    ((com.google.firebase.Timestamp) data.get("birthDate")).toDate() : null;
                            Date createdAt = data.containsKey("createdAt") && data.get("createdAt") instanceof com.google.firebase.Timestamp ?
                                    ((com.google.firebase.Timestamp) data.get("createdAt")).toDate() : null;
                            Date updatedAt = data.containsKey("updatedAt") && data.get("updatedAt") instanceof com.google.firebase.Timestamp ?
                                    ((com.google.firebase.Timestamp) data.get("updatedAt")).toDate() : null;

                            Object teachingClassesObj = data.get("teachingClasses");
                            List<Subject> teachingClasses = new ArrayList<>();
                            if (teachingClassesObj instanceof Map) {
                                Map<String, Object> classesRaw = (Map<String, Object>) teachingClassesObj;
                                for (Map.Entry<String, Object> entry : classesRaw.entrySet()) {
                                    if (entry.getValue() instanceof Map) {
                                        Map<String, Object> classData = (Map<String, Object>) entry.getValue();
                                        Subject subject = new Subject(
                                                entry.getKey(),
                                                (String) classData.get("className"),
                                                (String) classData.get("description"),
                                                (List<StudyMaterial>) classData.get("studyMaterials")
                                        );
                                        teachingClasses.add(subject);
                                    }
                                }
                            } else if (teachingClassesObj instanceof List) {
                                List<Map<String, Object>> classesList = (List<Map<String, Object>>) teachingClassesObj;
                                for (Map<String, Object> classData : classesList) {
                                    Subject subject = new Subject(
                                            (String) classData.get("classId"),
                                            (String) classData.get("className"),
                                            (String) classData.get("description"),
                                            (List<StudyMaterial>) classData.get("studyMaterials")
                                    );
                                    teachingClasses.add(subject);
                                }
                            }

                            Teacher teacher = new Teacher(
                                    (String) data.get("userID"),
                                    (String) data.get("fullName"),
                                    (String) data.get("email"),
                                    (String) data.get("password"),
                                    EUserRole.valueOf((String) data.get("role")),
                                    (String) data.get("imageUrl"),
                                    birthDate,
                                    (Boolean) data.get("isActive"),
                                    createdAt,
                                    updatedAt,
                                    (String) data.get("degree"),
                                    teachingClasses,
                                    (String) data.get("major")
                            );
                            onSuccess.accept(teacher);
                        } else {
                            onFailure.accept("Giảng viên không tồn tại");
                        }
                    } else {
                        onFailure.accept("Lỗi khi tải thông tin giảng viên: " + task.getException().getMessage());
                    }
                });
    }

    public void deleteTeacher(String teacherId, @NonNull Consumer<Void> onSuccess, @NonNull Consumer<String> onFailure) {
        db.collection("teachers").document(teacherId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Teacher teacher = document.toObject(Teacher.class);
                        if (teacher != null && teacher.getTeachingClasses() != null) {
                            // Xóa tham chiếu đến giảng viên trong các môn học
                            for (Subject subject : teacher.getTeachingClasses()) {
                                db.collection("subjects").document(subject.getClassId())
                                        .update("teacherId", null); // Giả sử môn học có trường teacherId
                            }
                        }
                        // Xóa giảng viên
                        db.collection("teachers").document(teacherId)
                                .delete()
                                .addOnSuccessListener(aVoid -> onSuccess.accept(null))
                                .addOnFailureListener(e -> onFailure.accept("Xóa giảng viên thất bại: " + e.getMessage()));
                    } else {
                        onFailure.accept("Giảng viên không tồn tại");
                    }
                })
                .addOnFailureListener(e -> onFailure.accept("Lỗi khi kiểm tra giảng viên: " + e.getMessage()));
    }

    public void loadSubjects(String userId, String classNameFilter, Consumer<List<Subject>> onSuccess, Consumer<Exception> onError) {
        db.collection("teachers").whereEqualTo("userID", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            onError.accept(new Exception("Không tìm thấy sinh viên với userID: " + userId));
                            return;
                        }
                        DocumentSnapshot studentDoc = task.getResult().getDocuments().get(0);
                        Object teachingClassesObj = studentDoc.get("teachingClasses");
                        List<Subject> subjects = new ArrayList<>();

                        if (teachingClassesObj instanceof Map) {
                            Map<String, Object> teachingClassesRaw = (Map<String, Object>) teachingClassesObj;
                            if (teachingClassesRaw != null && !teachingClassesRaw.isEmpty()) {
                                for (Map.Entry<String, Object> entry : teachingClassesRaw.entrySet()) {
                                    if (entry.getValue() instanceof Map) {
                                        Map<String, Object> classData = (Map<String, Object>) entry.getValue();
                                        String className = (String) classData.get("className");
                                        if (className == null) {
                                            continue;
                                        }

                                        if (classNameFilter == null || classNameFilter.isEmpty() ||
                                                className.toLowerCase().contains(classNameFilter.toLowerCase())) {
                                            Subject subject = new Subject(
                                                    entry.getKey(),
                                                    className,
                                                    (String) classData.get("description"),
                                                    (List<StudyMaterial>) classData.get("studyMaterials")
                                            );
                                            subjects.add(subject);
                                        }
                                    }
                                }
                            }
                        } else if (teachingClassesObj instanceof List) {
                            List<Map<String, Object>> teachingClassesList = (List<Map<String, Object>>) teachingClassesObj;
                            for (Map<String, Object> classData : teachingClassesList) {
                                String className = (String) classData.get("className");
                                if (className == null) {
                                    continue;
                                }

                                if (classNameFilter == null || classNameFilter.isEmpty() ||
                                        className.toLowerCase().contains(classNameFilter.toLowerCase())) {
                                    Subject subject = new Subject(
                                            (String) classData.get("classId"),
                                            className,
                                            (String) classData.get("description"),
                                            (List<StudyMaterial>) classData.get("studyMaterials")
                                    );
                                    subjects.add(subject);
                                }
                            }
                        }

                        onSuccess.accept(subjects);
                    } else {
                        onError.accept(task.getException() != null ? task.getException() : new Exception("Lỗi không xác định khi truy vấn Firestore"));
                    }
                });
    }

    public void createTeacherAccount(String email, String password, String fullName, String major, TeacherCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userID = auth.getCurrentUser().getUid();

                        // Tạo đối tượng Teacher
                        Map<String, Object> teacher = new HashMap<>();
                        teacher.put("userID", userID);
                        teacher.put("email", email);
                        teacher.put("fullName", fullName);
                        teacher.put("password", password); // Lưu ý: Không nên lưu mật khẩu, chỉ để minh họa
                        teacher.put("role", "TEACHER");
                        teacher.put("imageUrl", "");
                        teacher.put("birthDate", null);
                        teacher.put("isActive", true);
                        teacher.put("createdAt", new Date());
                        teacher.put("updatedAt", new Date());
                        teacher.put("degree", null);
                        teacher.put("teachingClasses", null);
                        teacher.put("major", major.isEmpty() ? null : major);

                        // Lưu thông tin giảng viên vào Firestore
                        db.collection("teachers").document(userID)
                                .set(teacher)
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
    public void updateTeacher(String teacherId, Teacher teacher, Consumer<Void> onSuccess, Consumer<String> onFailure) {
        db.collection("teachers").document(teacherId)
                .set(teacher)
                .addOnSuccessListener(aVoid -> onSuccess.accept(null))
                .addOnFailureListener(e -> onFailure.accept(e.getMessage()));
    }
    public interface TeacherCallback {
        void onComplete(boolean success, String message);
    }
}