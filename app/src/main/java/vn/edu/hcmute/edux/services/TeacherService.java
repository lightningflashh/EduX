package vn.edu.hcmute.edux.services;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import vn.edu.hcmute.edux.model.EUserRole;
import vn.edu.hcmute.edux.model.StudyMaterial;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.model.Teacher;


public class TeacherService {

    private final FirebaseFirestore db;

    public TeacherService() {
        this.db = FirebaseFirestore.getInstance();
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
}
