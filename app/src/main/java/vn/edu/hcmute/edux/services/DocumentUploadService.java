package vn.edu.hcmute.edux.services;


import android.content.Context;

import androidx.annotation.NonNull;


import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.*;

import vn.edu.hcmute.edux.model.StudyMaterial;
import vn.edu.hcmute.edux.model.EDocumentType;


import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Class xử lý việc upload file lên ImageKit + lưu metadata vào Firebase Firestore
 */
public class DocumentUploadService {
    private static final String TAG = "UploadHelper";

    /**
     * Interface callback sau khi upload hoàn tất hoặc thất bại
     */
    public interface OnUploadCompleteListener {
        void onSuccess();

        void onFailure(Exception e);
    }

    /**
     * Hàm chính để upload file lên ImageKit và lưu thông tin file vào Firestore
     *
     * @param context   Context để dùng cho log hoặc truyền cho lib nếu cần
     * @param file      File thực đã chuẩn bị từ Uri
     * @param listener  Callback để xử lý sau upload (thành công/thất bại)
     */
    public static void uploadToImageKitAndSaveMetadataToFirestore(
            Context context,
            File file,
            String classId,
            String teacherId,
            EDocumentType type,
            String title,
            OnUploadCompleteListener listener
    ) {
        OkHttpClient client = new OkHttpClient();

        // Gửi request để lấy token upload từ server backend riêng (bắt buộc với ImageKit)
        Request tokenRequest = new Request.Builder()
                .url("http://10.0.2.2:8080/auth") // Localhost backend trả về token ImageKit; Thay bằng API thật
                .build();

        client.newCall(tokenRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onFailure(e); // Gọi callback khi lấy token thất bại
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "ImageKit upload failed: " + response.code());
                    listener.onFailure(new IOException("Lỗi lấy token từ server"));
                    return;
                }

                try {
                    // Parse JSON nhận được từ server (token + signature + publicKey + expire time)
                    JSONObject json = new JSONObject(response.body().string());
                    Log.d(TAG, "Token JSON: " + json);
                    String token = json.getString("token");
                    String signature = json.getString("signature");
                    long expire = json.getLong("expire");
                    String publicKey = json.getString("publicKey");
                    Log.d(TAG, "Uploading file to ImageKit...");

                    Log.d(TAG, "Uploading with token=" + token);
                    Log.d(TAG, "signature=" + signature);
                    Log.d(TAG, "expire=" + expire);
                    Log.d(TAG, "publicKey=" + publicKey);
                    Log.d(TAG, "fileName=" + file.getName().replaceAll("[^a-zA-Z0-9\\.\\-_]", "_"));
                    Log.d(TAG, "file length=" + file.length());


                    // Upload file lên ImageKit
                    // Tạo multipart body chứa thông tin upload
                    RequestBody uploadBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", file.getName(),
                                    RequestBody.create(file, MediaType.parse("application/octet-stream")))
                            .addFormDataPart("fileName", file.getName())
                            .addFormDataPart("token", token)
                            .addFormDataPart("expire", String.valueOf(expire))
                            .addFormDataPart("signature", signature)
                            .addFormDataPart("publicKey", publicKey)
                            .build();

                    // Gửi request upload đến ImageKit
                    Request uploadRequest = new Request.Builder()
                            .url("https://upload.imagekit.io/api/v1/files/upload")
                            .post(uploadBody)
                            .build();

                    client.newCall(uploadRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.e(TAG, "Upload to ImageKit failed", e);
                            listener.onFailure(e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                String errorBody = response.body() != null ? response.body().string() : "null";
                                Log.e(TAG, "Upload failed. Code: " + response.code() + ", body: " + errorBody);
                                listener.onFailure(new IOException("Lỗi upload ImageKit"));
                                return;
                            }

                            try {
                                // Parse response sau khi upload thành công
                                String uploadResp = response.body().string();
                                Log.d(TAG, "Upload response: " + uploadResp);

                                JSONObject uploadJson = new JSONObject(uploadResp);
                                String fileUrl = uploadJson.getString("url"); // Lấy URL của file đã upload

                                Log.d(TAG, "Upload successful. File URL: " + fileUrl);

                                // Tạo model StudyMaterial để lưu vào Firestore
                                String materialId = UUID.randomUUID().toString();
                                long timestamp = System.currentTimeMillis();

                                StudyMaterial material = new StudyMaterial(
                                        materialId,
                                        title,
                                        fileUrl,
                                        classId,
                                        teacherId,
                                        type,
                                        timestamp
                                );
                                // Gửi dữ liệu metadata lên Firebase Firestore
                                saveMetadataToFirestore(material, listener);

                            } catch (JSONException e) {
                                Log.e(TAG, "Failed to parse upload response", e);
                                listener.onFailure(e);
                            }
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse token JSON", e);
                    listener.onFailure(e);
                }
            }
        });
    }

    /**
     * Lưu metadata file vào Firestore sau khi upload thành công
     *
     * @param material  Đối tượng tài liệu đã upload xong
     * @param listener  Callback để báo kết quả
     */
    private static void saveMetadataToFirestore(StudyMaterial material, OnUploadCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Saving metadata to Firestore: " + material.getTitle());
        Map<String, Object> data = new HashMap<>();

        data.put("materialID", material.getId());
        data.put("title", material.getTitle());
        data.put("fileURL", material.getUrl());
        data.put("classId", material.getClassId());
        data.put("teacherId", material.getTeacherId());
        data.put("type", material.getType().name());
        data.put("uploadedAt", material.getUploadedAt());

        db.collection("documents")
                .document(material.getId())
                .set(data)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Metadata saved to Firestore successfully");
                    listener.onSuccess(); // báo thành công cho controller
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save metadata to Firestore", e);
                    listener.onFailure(e); // báo lỗi
                });
    }
}

