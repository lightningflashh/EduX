package vn.edu.hcmute.edux.utils;


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


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class UploadHelper {
    private static final String TAG = "UploadHelper";


    public interface OnUploadCompleteListener {
        void onSuccess();

        void onFailure(Exception e);
    }

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

        // Gọi server để lấy token upload ImageKit
        Request tokenRequest = new Request.Builder()
                .url("http://10.0.2.2:8080/auth") // Thay bằng API thật
                .build();

        client.newCall(tokenRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "ImageKit upload failed: " + response.code());
                    listener.onFailure(new IOException("Lỗi lấy token từ server"));
                    return;
                }

                try {
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
                                String uploadResp = response.body().string();
                                Log.d(TAG, "Upload response: " + uploadResp);

                                JSONObject uploadJson = new JSONObject(uploadResp);
                                String fileUrl = uploadJson.getString("url");

                                Log.d(TAG, "Upload successful. File URL: " + fileUrl);

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
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save metadata to Firestore", e);
                    listener.onFailure(e);
                });
    }
}

