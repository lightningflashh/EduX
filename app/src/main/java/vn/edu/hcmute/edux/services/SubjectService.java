package vn.edu.hcmute.edux.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import vn.edu.hcmute.edux.model.EUserRole;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.model.StudyMaterial;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.model.Teacher;


public class SubjectService {

    private final FirebaseFirestore db;

    public SubjectService() {
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

    public void loadStudents(@NonNull Consumer<List<Student>> onSuccess, @NonNull Consumer<String> onFailure) {
        List<Student> studentList = new ArrayList<>();
        db.collection("students")
                .whereEqualTo("role", EUserRole.STUDENT.toString())
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


                            // Chuyển đổi joinedClasses từ List<Map> sang List<Subject>
                            List<Map<String, Object>> joinedClassesRaw = (List<Map<String, Object>>) data.get("joinedClasses");
                            List<Subject> joinedClasses = new ArrayList<>();
                            if (joinedClassesRaw != null) {
                                for (Map<String, Object> classData : joinedClassesRaw) {
                                    Subject subject = new Subject(
                                            (String) classData.get("classId"),
                                            (String) classData.get("className"),
                                            (String) classData.get("description"),
                                            (List<StudyMaterial>) classData.get("materials")
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
                            studentList.add(student);
                        }
                        onSuccess.accept(studentList);
                    } else {
                        onFailure.accept("Lỗi khi tải danh sách sinh viên: " + task.getException().getMessage());
                    }
                });
    }

    public void createSubject(String subjectId, String subjectName, String description,
                              Teacher selectedTeacher, List<Student> selectedStudents,
                              @NonNull Consumer<Void> onSuccess, @NonNull Consumer<String> onFailure) {
        Subject subject = new Subject(subjectId, subjectName, description, null);
        db.collection("subjects").document(subjectId)
                .set(subject)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Subject> teachingClasses = selectedTeacher.getTeachingClasses() != null ?
                                new ArrayList<>(selectedTeacher.getTeachingClasses()) : new ArrayList<>();
                        teachingClasses.add(subject);
                        selectedTeacher.setTeachingClasses(teachingClasses);
                        db.collection("teachers").document(selectedTeacher.getUserID())
                                .update("teachingClasses", teachingClasses)
                                .addOnCompleteListener(updateTask -> {
                                    if (!updateTask.isSuccessful()) {
                                        Log.e("SubjectService", "Lỗi khi cập nhật giảng viên: " + updateTask.getException().getMessage());
                                    }
                                });

                        for (Student student : selectedStudents) {
                            List<Subject> joinedClasses = student.getJoinedClasses() != null ?
                                    new ArrayList<>(student.getJoinedClasses()) : new ArrayList<>();
                            joinedClasses.add(subject);
                            student.setJoinedClasses(joinedClasses);
                            db.collection("students").document(student.getUserID())
                                    .update("joinedClasses", joinedClasses)
                                    .addOnCompleteListener(updateTask -> {
                                        if (!updateTask.isSuccessful()) {
                                            Log.e("SubjectService", "Lỗi khi cập nhật sinh viên: " + updateTask.getException().getMessage());
                                        }
                                    });
                        }
                        onSuccess.accept(null);
                    } else {
                        onFailure.accept("Thêm môn học thất bại: " + task.getException().getMessage());
                    }
                });
    }

    public void updateSubject(String subjectId, String subjectName, String description,
                              Teacher oldTeacher, Teacher newTeacher, List<Student> oldStudents, List<Student> newStudents,
                              @NonNull Consumer<Void> onSuccess, @NonNull Consumer<String> onFailure) {
        Subject updatedSubject = new Subject(subjectId, subjectName, description, null);
        db.collection("subjects").document(subjectId)
                .set(updatedSubject)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (oldTeacher != null && !oldTeacher.getUserID().equals(newTeacher.getUserID())) {
                            List<Subject> oldTeacherClasses = oldTeacher.getTeachingClasses() != null ?
                                    new ArrayList<>(oldTeacher.getTeachingClasses()) : new ArrayList<>();
                            oldTeacherClasses.removeIf(s -> s.getClassId().equals(subjectId));
                            oldTeacher.setTeachingClasses(oldTeacherClasses);
                            db.collection("teachers").document(oldTeacher.getUserID())
                                    .update("teachingClasses", oldTeacherClasses)
                                    .addOnCompleteListener(updateTask -> {
                                        if (!updateTask.isSuccessful()) {
                                            Log.e("SubjectService", "Lỗi khi cập nhật giảng viên cũ: " + updateTask.getException().getMessage());
                                        }
                                    });
                        }

                        List<Subject> newTeacherClasses = newTeacher.getTeachingClasses() != null ?
                                new ArrayList<>(newTeacher.getTeachingClasses()) : new ArrayList<>();
                        if (!newTeacherClasses.contains(updatedSubject)) {
                            newTeacherClasses.add(updatedSubject);
                            newTeacher.setTeachingClasses(newTeacherClasses);
                            db.collection("teachers").document(newTeacher.getUserID())
                                    .update("teachingClasses", newTeacherClasses)
                                    .addOnCompleteListener(updateTask -> {
                                        if (!updateTask.isSuccessful()) {
                                            Log.e("SubjectService", "Lỗi khi cập nhật giảng viên mới: " + updateTask.getException().getMessage());
                                        }
                                    });
                        }

                        for (Student student : oldStudents) {
                            if (!newStudents.contains(student)) {
                                List<Subject> joinedClasses = student.getJoinedClasses() != null ?
                                        new ArrayList<>(student.getJoinedClasses()) : new ArrayList<>();
                                joinedClasses.removeIf(s -> s.getClassId().equals(subjectId));
                                student.setJoinedClasses(joinedClasses);
                                db.collection("students").document(student.getUserID())
                                        .update("joinedClasses", joinedClasses)
                                        .addOnCompleteListener(updateTask -> {
                                            if (!updateTask.isSuccessful()) {
                                                Log.e("SubjectService", "Lỗi khi cập nhật sinh viên: " + updateTask.getException().getMessage());
                                            }
                                        });
                            }
                        }

                        for (Student student : newStudents) {
                            List<Subject> joinedClasses = student.getJoinedClasses() != null ?
                                    new ArrayList<>(student.getJoinedClasses()) : new ArrayList<>();
                            if (!joinedClasses.contains(updatedSubject)) {
                                joinedClasses.add(updatedSubject);
                                student.setJoinedClasses(joinedClasses);
                                db.collection("students").document(student.getUserID())
                                        .update("joinedClasses", joinedClasses)
                                        .addOnCompleteListener(updateTask -> {
                                            if (!updateTask.isSuccessful()) {
                                                Log.e("SubjectService", "Lỗi khi cập nhật sinh viên: " + updateTask.getException().getMessage());
                                            }
                                        });
                            }
                        }

                        onSuccess.accept(null);
                    } else {
                        onFailure.accept("Cập nhật môn học thất bại: " + task.getException().getMessage());
                    }
                });
    }

    public void deleteSubject(String subjectId, @NonNull Consumer<Void> onSuccess, @NonNull Consumer<String> onFailure) {
        db.collection("subjects").document(subjectId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection("teachers")
                                .whereArrayContains("teachingClasses", subjectId)
                                .get()
                                .addOnCompleteListener(teacherTask -> {
                                    if (teacherTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : teacherTask.getResult()) {
                                            Teacher teacher = document.toObject(Teacher.class);
                                            List<Subject> teachingClasses = teacher.getTeachingClasses() != null ?
                                                    new ArrayList<>(teacher.getTeachingClasses()) : new ArrayList<>();
                                            teachingClasses.removeIf(s -> s.getClassId().equals(subjectId));
                                            teacher.setTeachingClasses(teachingClasses);
                                            db.collection("teachers").document(teacher.getUserID())
                                                    .update("teachingClasses", teachingClasses);
                                        }
                                    }
                                });

                        db.collection("students")
                                .whereArrayContains("joinedClasses", subjectId)
                                .get()
                                .addOnCompleteListener(studentTask -> {
                                    if (studentTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : studentTask.getResult()) {
                                            Student student = document.toObject(Student.class);
                                            List<Subject> joinedClasses = student.getJoinedClasses() != null ?
                                                    new ArrayList<>(student.getJoinedClasses()) : new ArrayList<>();
                                            joinedClasses.removeIf(s -> s.getClassId().equals(subjectId));
                                            student.setJoinedClasses(joinedClasses);
                                            db.collection("students").document(student.getUserID())
                                                    .update("joinedClasses", joinedClasses);
                                        }
                                    }
                                });

                        onSuccess.accept(null);
                    } else {
                        onFailure.accept("Xóa môn học thất bại: " + task.getException().getMessage());
                    }
                });
    }
}
