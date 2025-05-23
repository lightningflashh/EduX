package vn.edu.hcmute.edux.controllers.user;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;

import vn.edu.hcmute.edux.services.StudentService;
import vn.edu.hcmute.edux.utils.FaceNetHelper;
import vn.edu.hcmute.edux.views.iFacialRecognitionView;

public class FacialRecognitionController {

    private final iFacialRecognitionView view;
    private final StudentService studentService;
    private final FaceNetHelper faceNetHelper;

    private float[] registeredEmbedding;
    private boolean matched = false;

    public FacialRecognitionController(iFacialRecognitionView view) throws Exception {
        this.view = view;
        this.studentService = new StudentService();
        this.faceNetHelper = new FaceNetHelper(view.getContext());
    }

    public boolean isMatched() {
        return matched;
    }

    public void loadRegisteredEmbedding(String userId, Runnable onSuccess) {
        studentService.loadEmbedding(
                userId,
                embeddingList -> {
                    registeredEmbedding = new float[embeddingList.size()];
                    for (int i = 0; i < embeddingList.size(); i++) {
                        registeredEmbedding[i] = embeddingList.get(i).floatValue();
                    }
                    onSuccess.run();
                },
                error -> view.showToast("Lỗi tải embedding: " + error)
        );
    }

    public void detectFaceAndVerify(InputImage image, Bitmap fullBitmap, FaceOverlayCallback callback, Runnable onComplete) {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build();

        FaceDetection.getClient(options).process(image)
                .addOnSuccessListener(faces -> {
                    try {
                        if (!faces.isEmpty()) {
                            Face face = faces.get(0);
                            Rect bounds = face.getBoundingBox();

                            Rect safeBounds = adjustBounds(bounds, fullBitmap);
                            Bitmap faceBitmap = Bitmap.createBitmap(
                                    fullBitmap,
                                    safeBounds.left,
                                    safeBounds.top,
                                    safeBounds.width(),
                                    safeBounds.height()
                            );

                            float[] currentEmbedding = faceNetHelper.getFaceEmbedding(faceBitmap);
                            double similarity = cosineSimilarity(registeredEmbedding, currentEmbedding);
                            matched = similarity >= 0.8;

                            callback.onFaceRecognized(bounds, similarity, matched, fullBitmap.getWidth(), fullBitmap.getHeight());

                            if (matched) {
                                view.navigateToQuiz();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("VERIFY", "Lỗi xử lý khuôn mặt", e);
                        view.showToast("Lỗi xử lý khuôn mặt");
                    } finally {
                        onComplete.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("VERIFY", "Lỗi detect face: " + e.getMessage());
                    onComplete.run();
                });
    }

    public interface FaceOverlayCallback {
        void onFaceRecognized(Rect face, double similarity, boolean matched, int width, int height);
    }

    private Rect adjustBounds(Rect bounds, Bitmap bitmap) {
        int left = Math.max(bounds.left, 0);
        int top = Math.max(bounds.top, 0);
        int right = Math.min(bounds.right, bitmap.getWidth());
        int bottom = Math.min(bounds.bottom, bitmap.getHeight());
        return new Rect(left, top, right, bottom);
    }

    private double cosineSimilarity(float[] vec1, float[] vec2) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < vec1.length; i++) {
            dot += vec1[i] * vec2[i];
            normA += vec1[i] * vec1[i];
            normB += vec2[i] * vec2[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

