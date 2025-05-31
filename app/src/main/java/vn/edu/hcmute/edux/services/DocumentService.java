package vn.edu.hcmute.edux.services;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.model.EDocumentType;
import vn.edu.hcmute.edux.model.StudyMaterial;
import vn.edu.hcmute.edux.services.DocumentUploadService;
import vn.edu.hcmute.edux.utils.FileUtils;

public class DocumentService {

    public interface MaterialListener {
        void onSuccess(List<StudyMaterial> materials);
        void onError(Exception e);
    }

    public interface SimpleListener {
        void onSuccess();
        void onError(Exception e);
    }


    public void loadMaterialsForStudent(String classId, MaterialListener listener) {
        fetchMaterialsFromFirestore(classId, listener);
    }

    public void loadMaterialsForTeacher(String classId, MaterialListener listener) {
        fetchMaterialsFromFirestore(classId, listener);
    }


    private void fetchMaterialsFromFirestore(String classId, MaterialListener listener) {
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
                        } catch (Exception ignored) {
                        }
                    }
                    listener.onSuccess(list);
                })
                .addOnFailureListener(listener::onError);
    }

    public void uploadMaterial(
            Context context,
            Uri uri,
            String title,
            String classId,
            String teacherId,
            EDocumentType type,
            SimpleListener listener
    ) {
        try {
            // Bước 1: Chuyển Uri thành File thực (tạo file tạm trong cache)
            File file = FileUtils.createTempFileFromUri(context, uri);

            // Bước 2: Gọi DocumentUploadService để upload file lên ImageKit + lưu Firestore
            DocumentUploadService.uploadToImageKitAndSaveMetadataToFirestore(
                    context,
                    file,
                    classId,
                    teacherId,
                    type,
                    title,
                    // Bước 3: Gửi callback báo kết quả upload về controller
                    new DocumentUploadService.OnUploadCompleteListener() {
                        @Override
                        public void onSuccess() {
                            listener.onSuccess(); // → Controller xử lý: view.showMessage(), reload list,..
                        }

                        @Override
                        public void onFailure(Exception e) {
                            listener.onError(e);
                        }
                    }
            );
        } catch (Exception e) {
            // Trường hợp lỗi khi tạo file tạm (ví dụ Uri không hợp lệ)
            listener.onError(e);
        }
    }

    public void updateTitle(String docId, String newTitle, SimpleListener listener) {
        FirebaseFirestore.getInstance()
                .collection("documents")
                .document(docId)
                .update("title", newTitle)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(listener::onError);
    }

    public void deleteMaterial(String docId, SimpleListener listener) {
        FirebaseFirestore.getInstance()
                .collection("documents")
                .document(docId)
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(listener::onError);
    }


    public List<StudyMaterial> filter(List<StudyMaterial> all, EDocumentType type, String keyword) {
        if ((type == null || type.name().isEmpty()) && (keyword == null || keyword.trim().isEmpty())) {
            return all;
        }

        List<StudyMaterial> result = new ArrayList<>();
        for (StudyMaterial m : all) {
            boolean matchType = (type == null || m.getType() == type);
            boolean matchKeyword = (keyword == null || keyword.trim().isEmpty()
                    || m.getTitle().toLowerCase().contains(keyword.toLowerCase()));

            if (matchType && matchKeyword) {
                result.add(m);
            }
        }
        return result;
    }

}
