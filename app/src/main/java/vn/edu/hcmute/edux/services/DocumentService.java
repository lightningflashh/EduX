package vn.edu.hcmute.edux.services;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;


import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.model.EDocumentType;
import vn.edu.hcmute.edux.model.StudyMaterial;
import vn.edu.hcmute.edux.utils.FirebaseHelper;
import vn.edu.hcmute.edux.utils.UploadHelper;

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
        FirebaseHelper.fetchMaterialsForClass(classId, new FirebaseHelper.OnMaterialsLoadedListener() {
            @Override
            public void onLoaded(List<StudyMaterial> materials) {
                listener.onSuccess(materials);
            }

            @Override
            public void onError(Exception e) {
                listener.onError(e);
            }
        });
    }

    public void loadMaterialsForTeacher(String classId, MaterialListener listener) {
        FirebaseHelper.fetchMaterialsForClass(classId, new FirebaseHelper.OnMaterialsLoadedListener() {
            @Override
            public void onLoaded(List<StudyMaterial> materials) {
                listener.onSuccess(materials);
            }

            @Override
            public void onError(Exception e) {
                listener.onError(e);
            }
        });
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
            File file = createTempFileFromUri(context, uri);

            UploadHelper.uploadToImageKitAndSaveMetadataToFirestore(
                    context,
                    file,
                    classId,
                    teacherId,
                    type,
                    title,
                    new UploadHelper.OnUploadCompleteListener() {
                        @Override
                        public void onSuccess() {
                            listener.onSuccess();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            listener.onError(e);
                        }
                    }
            );
        } catch (Exception e) {
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

    public List<StudyMaterial> filter(List<StudyMaterial> all, EDocumentType type) {
        if (type == null) return all;
        List<StudyMaterial> result = new ArrayList<>();
        for (StudyMaterial m : all) {
            if (m.getType() == type) {
                result.add(m);
            }
        }
        return result;
    }

    private File createTempFileFromUri(Context context, Uri uri) throws Exception {
        String fileName = getFileName(context, uri);
        File tempFile = File.createTempFile("upload_", "_" + fileName, context.getCacheDir());

        try (
                InputStream in = context.getContentResolver().openInputStream(uri);
                OutputStream out = new FileOutputStream(tempFile)
        ) {
            if (in != null && out != null) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
        }
        return tempFile;
    }

    private String getFileName(Context context, Uri uri) {
        String result = "file";
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
            }
        }
        return result;
    }
}
