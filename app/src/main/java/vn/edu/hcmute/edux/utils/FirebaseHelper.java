package vn.edu.hcmute.edux.utils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.model.StudyMaterial;
import vn.edu.hcmute.edux.model.EDocumentType;
import vn.edu.hcmute.edux.model.Subject;

public class FirebaseHelper {

    public interface OnMaterialsLoadedListener {
        void onLoaded(List<StudyMaterial> materials);
        void onError(Exception e);
    }

    public static void fetchAllMaterialsFromFirestore(OnMaterialsLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("documents")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<StudyMaterial> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            StudyMaterial material = new StudyMaterial(
                                    doc.getString("materialID"),
                                    doc.getString("title"),
                                    doc.getString("fileURL"),
                                    doc.getString("classId"),
                                    doc.getString("teacherId"),
                                    EDocumentType.valueOf(doc.getString("type")),
                                    doc.getLong("uploadedAt")
                            );
                            list.add(material);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    listener.onLoaded(list);
                })
                .addOnFailureListener(listener::onError);
    }

    public static void getStudentJoinedClasses(String studentId, OnClassesLoadedListener listener) {
        FirebaseFirestore.getInstance()
                .collection("users") // hoặc "students"
                .document(studentId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        List<Subject> joined = (List<Subject>) doc.toObject(Student.class).getJoinedClasses();
                        listener.onLoaded(joined);
                    } else {
                        listener.onLoaded(new ArrayList<>());
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    public static void fetchMaterialsForClass(String classId, OnMaterialsLoadedListener listener) {
        FirebaseFirestore.getInstance()
                .collection("documents")
                .whereEqualTo("classId", classId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<StudyMaterial> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        try {
                            StudyMaterial material = new StudyMaterial(
                                    doc.getString("materialID"),
                                    doc.getString("title"),
                                    doc.getString("fileURL"),
                                    doc.getString("classId"),
                                    doc.getString("teacherId"),
                                    EDocumentType.valueOf(doc.getString("type")),
                                    doc.getLong("uploadedAt")
                            );
                            list.add(material);
                        } catch (Exception ignored) {}
                    }
                    listener.onLoaded(list);
                })
                .addOnFailureListener(listener::onError);
    }

    public static void getClassesForTeacher(String teacherId, OnClassesLoadedListener listener) {
        FirebaseFirestore.getInstance()
                .collection("classes") // or "subjects" tùy cấu trúc của bạn
                .whereEqualTo("teacherId", teacherId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Subject> result = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Subject subject = doc.toObject(Subject.class);
                        result.add(subject);
                    }
                    listener.onLoaded(result);
                })
                .addOnFailureListener(e -> listener.onError(e));
    }

    public interface OnClassesLoadedListener {
        void onLoaded(List<Subject> classList);
        void onError(Exception e);
    }


}
