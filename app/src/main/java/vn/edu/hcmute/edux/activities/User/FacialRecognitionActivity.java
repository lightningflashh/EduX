package vn.edu.hcmute.edux.activities.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.services.StudentService;
import vn.edu.hcmute.edux.utils.FaceNetHelper;
import vn.edu.hcmute.edux.utils.FaceOverlayView;

public class FacialRecognitionActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private FaceNetHelper faceNetHelper;
    private FaceOverlayView faceOverlay;
    private float[] registeredEmbedding;
    private boolean matched = false;

    private StudentService studentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_recognition);
        init();
        try {
            faceNetHelper = new FaceNetHelper(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadRegisteredEmbedding();
    }

    private void init() {
        previewView = findViewById(R.id.previewView);
        cameraExecutor = Executors.newSingleThreadExecutor();
        faceOverlay = findViewById(R.id.faceOverlay);
        studentService = new StudentService();
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    if (matched || imageProxy.getImage() == null) {
                        imageProxy.close();
                        return;
                    }
                    InputImage image = InputImage.fromMediaImage(
                            imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
                    detectFace(image, imageProxy);
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                Log.e("CAMERA", "Camera error", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // Load the registered embedding from the database
    private void loadRegisteredEmbedding() {
        String userId = Objects
                .requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        studentService.loadEmbedding(
                userId,
                embeddingList -> {
                    registeredEmbedding = new float[embeddingList.size()];
                    for (int i = 0; i < embeddingList.size(); i++) {
                        registeredEmbedding[i] = embeddingList.get(i).floatValue();
                    }
                    startCamera();
                },
                error -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        );
    }

    private void detectFace(InputImage inputImage, ImageProxy imageProxy) {
        // Set up the face detection options
        // Set the performance mode to PERFORMANCE_MODE_FAST for real-time detection
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build();

        // Google ML Kit Face Detection API for detecting faces
        FaceDetection.getClient(options).process(inputImage)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty()) {
                        Face face = faces.get(0);
                        Bitmap bitmap = imageProxyToBitmap(imageProxy);
                        if (bitmap == null) {
                            imageProxy.close();
                            return;
                        }

                        // Get the bounding box of the detected face and crop the face
                        Rect bounds = face.getBoundingBox();
                        int left = Math.max(bounds.left, 0);
                        int top = Math.max(bounds.top, 0);
                        int width = Math.min(bounds.width(), bitmap.getWidth() - left);
                        int height = Math.min(bounds.height(), bitmap.getHeight() - top);

                        try {
                            Bitmap cropped = Bitmap.createBitmap(bitmap, left, top, width, height);
                            // Get current face embedding
                            float[] currentEmbedding = faceNetHelper.getFaceEmbedding(cropped);
                            double similarity = cosineSimilarity(registeredEmbedding, currentEmbedding);
                            Log.d("VERIFY", "Similarity = " + similarity);

                            matched = similarity >= 0.8; // Threshold for matching

                            // Draw the bounding box around the detected face
                            runOnUiThread(() -> {
                                List<Rect> rects = new ArrayList<>();
                                rects.add(bounds);
                                faceOverlay.setFaceBounds(rects, similarity, matched, bitmap.getWidth(), bitmap.getHeight());
                            });

                            if (matched) {
                                Toast.makeText(this, "Đúng người!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, QuizActivity.class));
                                finish();
                            }
                        } catch (Exception e) {
                            Log.e("VERIFY", "Lỗi crop mặt", e);
                        }
                    }
                    imageProxy.close(); // Close the image proxy after processing
                })
                .addOnFailureListener(e -> {
                    Log.e("VERIFY", "Lỗi detect face: " + e.getMessage());
                    imageProxy.close();
                });
    }
    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        @SuppressWarnings("UnsafeOptInUsageError")
        Image image = imageProxy.getImage();
        if (image == null) return null;

        int width = image.getWidth();
        int height = image.getHeight();

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        byte[] imageBytes = out.toByteArray();

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    // Calculate cosine similarity between two vectors
    private double cosineSimilarity(float[] vec1, float[] vec2) {
        // cos(θ)=(A⋅B)/(∣∣A∣∣∗∣∣B∣∣)
        // . A⋅B = Σ Ai*Bi
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            dot += vec1[i] * vec2[i];
            normA += vec1[i] * vec1[i];
            normB += vec2[i] * vec2[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) cameraExecutor.shutdown();
    }

}