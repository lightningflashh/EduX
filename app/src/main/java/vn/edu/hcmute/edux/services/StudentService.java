package vn.edu.hcmute.edux.services;

import androidx.annotation.NonNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import vn.edu.hcmute.edux.model.EUserRole;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.model.StudyMaterial;
import vn.edu.hcmute.edux.model.Subject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class StudentService {

    private final FirebaseFirestore db;

    public StudentService() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void loadStudents(@NonNull Consumer<List<Student>> onSuccess, @NonNull Consumer<String> onFailure) {
        List<Student> studentList = new ArrayList<>();
        db.collection("students")
                .whereEqualTo("role", EUserRole.STUDENT.toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            Date birthDate = data.containsKey("birthDate") && data.get("birthDate") instanceof com.google.firebase.Timestamp ?
                                    ((com.google.firebase.Timestamp) data.get("birthDate")).toDate() : null;
                            Date createdAt = data.containsKey("createdAt") && data.get("createdAt") instanceof com.google.firebase.Timestamp ?
                                    ((com.google.firebase.Timestamp) data.get("createdAt")).toDate() : null;
                            Date updatedAt = data.containsKey("updatedAt") && data.get("updatedAt") instanceof com.google.firebase.Timestamp ?
                                    ((com.google.firebase.Timestamp) data.get("updatedAt")).toDate() : null;
                            Date enrollmentDate = data.containsKey("enrollmentDate") && data.get("enrollmentDate") instanceof com.google.firebase.Timestamp ?
                                    ((com.google.firebase.Timestamp) data.get("enrollmentDate")).toDate() : null;

                            Object joinedClassesObj = data.get("joinedClasses");
                            List<Subject> joinedClasses = new ArrayList<>();
                            if (joinedClassesObj instanceof Map) {
                                Map<String, Object> joinedClassesRaw = (Map<String, Object>) joinedClassesObj;
                                for (Map.Entry<String, Object> entry : joinedClassesRaw.entrySet()) {
                                    if (entry.getValue() instanceof Map) {
                                        Map<String, Object> classData = (Map<String, Object>) entry.getValue();
                                        Subject subject = new Subject(
                                                entry.getKey(),
                                                (String) classData.get("className"),
                                                (String) classData.get("description"),
                                                (List<StudyMaterial>) classData.get("studyMaterials")
                                        );
                                        joinedClasses.add(subject);
                                    }
                                }
                            }

                            Student student = new Student(
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
                                    (String) data.get("studentCode"),
                                    (String) data.get("major"),
                                    (String) data.get("studyTime"),
                                    joinedClasses
                            );
                            studentList.add(student);
                        }
                        onSuccess.accept(studentList);
                    } else {
                        onFailure.accept("Lỗi khi tải danh sách sinh viên: " + task.getException().getMessage());
                    }
                });
    }

    public void loadStudentById(String userId, @NonNull Consumer<Student> onSuccess, @NonNull Consumer<String> onFailure) {
        db.collection("students")
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
                            Date enrollmentDate = data.containsKey("enrollmentDate") && data.get("enrollmentDate") instanceof com.google.firebase.Timestamp ?
                                    ((com.google.firebase.Timestamp) data.get("enrollmentDate")).toDate() : null;

                            Object joinedClassesObj = data.get("joinedClasses");
                            List<Subject> joinedClasses = new ArrayList<>();
                            if (joinedClassesObj instanceof Map) {
                                Map<String, Object> joinedClassesRaw = (Map<String, Object>) joinedClassesObj;
                                for (Map.Entry<String, Object> entry : joinedClassesRaw.entrySet()) {
                                    if (entry.getValue() instanceof Map) {
                                        Map<String, Object> classData = (Map<String, Object>) entry.getValue();
                                        Subject subject = new Subject(
                                                entry.getKey(),
                                                (String) classData.get("className"),
                                                (String) classData.get("description"),
                                                (List<StudyMaterial>) classData.get("studyMaterials")
                                        );
                                        joinedClasses.add(subject);
                                    }
                                }
                            } else if (joinedClassesObj instanceof List) {
                                List<Map<String, Object>> joinedClassesList = (List<Map<String, Object>>) joinedClassesObj;
                                for (Map<String, Object> classData : joinedClassesList) {
                                    Subject subject = new Subject(
                                            (String) classData.get("classId"),
                                            (String) classData.get("className"),
                                            (String) classData.get("description"),
                                            (List<StudyMaterial>) classData.get("studyMaterials")
                                    );
                                    joinedClasses.add(subject);
                                }
                            }

                            Student student = new Student(
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
                                    (String) data.get("studentCode"),
                                    (String) data.get("major"),
                                    (String) data.get("studyTime"),
                                    joinedClasses
                            );
                            onSuccess.accept(student);
                        } else {
                            onFailure.accept("Sinh viên không tồn tại");
                        }
                    } else {
                        onFailure.accept("Lỗi khi tải thông tin sinh viên: " + task.getException().getMessage());
                    }
                });
    }

    public void deleteStudent(String studentId, @NonNull Consumer<Void> onSuccess, @NonNull Consumer<String> onFailure) {
        db.collection("students").document(studentId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Student student = document.toObject(Student.class);
                        if (student != null && student.getJoinedClasses() != null) {
                            for (Subject subject : student.getJoinedClasses()) {
                                db.collection("subjects").document(subject.getClassId())
                                        .collection("students")
                                        .document(studentId)
                                        .delete();
                            }
                        }
                        db.collection("students").document(studentId)
                                .delete()
                                .addOnSuccessListener(aVoid -> onSuccess.accept(null))
                                .addOnFailureListener(e -> onFailure.accept("Xóa sinh viên thất bại: " + e.getMessage()));
                    } else {
                        onFailure.accept("Sinh viên không tồn tại");
                    }
                })
                .addOnFailureListener(e -> onFailure.accept("Lỗi khi kiểm tra sinh viên: " + e.getMessage()));
    }

    public void loadSubjects(String userId, String classNameFilter, Consumer<List<Subject>> onSuccess, Consumer<Exception> onError) {
        db.collection("students").whereEqualTo("userID", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            onError.accept(new Exception("Không tìm thấy sinh viên với userID: " + userId));
                            return;
                        }
                        DocumentSnapshot studentDoc = task.getResult().getDocuments().get(0);
                        Object joinedClassesObj = studentDoc.get("joinedClasses");
                        List<Subject> subjects = new ArrayList<>();

                        if (joinedClassesObj instanceof Map) {
                            Map<String, Object> joinedClassesRaw = (Map<String, Object>) joinedClassesObj;
                            if (joinedClassesRaw != null && !joinedClassesRaw.isEmpty()) {
                                for (Map.Entry<String, Object> entry : joinedClassesRaw.entrySet()) {
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
                        } else if (joinedClassesObj instanceof List) {
                            List<Map<String, Object>> joinedClassesList = (List<Map<String, Object>>) joinedClassesObj;
                            for (Map<String, Object> classData : joinedClassesList) {
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

    public void updateStudentEmbedding(String uid, List<Double> embedding,
                                       Runnable onSuccess, java.util.function.Consumer<String> onFailure) {
        db.collection("students")
                .document(uid)
                .update("embedding", embedding)
                .addOnSuccessListener(unused -> {
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    if (onFailure != null) onFailure.accept(e.getMessage());
                });
    }
}