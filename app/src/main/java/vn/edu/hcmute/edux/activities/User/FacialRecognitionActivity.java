
package vn.edu.hcmute.edux.activities.User;

import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.vision.common.InputImage;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.controllers.user.FacialRecognitionController;
import vn.edu.hcmute.edux.utils.FaceOverlayView;
import vn.edu.hcmute.edux.views.iFacialRecognitionView;

public class FacialRecognitionActivity extends AppCompatActivity implements iFacialRecognitionView {

    private PreviewView previewView;
    private FaceOverlayView faceOverlay;
    private ExecutorService cameraExecutor;
    private boolean isProcessing = false;

    private FacialRecognitionController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_recognition);

        previewView = findViewById(R.id.previewView);
        faceOverlay = findViewById(R.id.faceOverlay);
        cameraExecutor = Executors.newSingleThreadExecutor();

        try {
            controller = new FacialRecognitionController(this);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi load FaceNet", e);
        }

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        controller.loadRegisteredEmbedding(userId, this::startCamera);
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                // Lấy camera provider
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Cấu hình preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Cấu hình image analysis
                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                analysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    if (isProcessing || controller.isMatched() || imageProxy.getImage() == null) {
                        imageProxy.close();
                        return;
                    }

                    isProcessing = true;

                    /*
                        Chuyển đổi ImageProxy sang Bitmap và InputImage
                        ImageProxy là một lớp đại diện cho một khung hình (frame) nhận được từ camera trong CameraX.
                        Nó là dạng ảnh thô (raw image) thường có định dạng như YUV_420_888.
                     */
                    Bitmap bitmap = imageProxyToBitmap(imageProxy);
                    InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

                    // Gọi hàm nhận diện khuôn mặt và xác minh
                    // Tham số 3 sẽ nhận diện khuôn mặt và hiển thị overlay
                    controller.detectFaceAndVerify(image, bitmap, (rect, sim, matched, w, h) -> {
                        runOnUiThread(() -> showFaceOverlay(List.of(rect), sim, matched, w, h));
                    }, () -> {
                        imageProxy.close();
                        isProcessing = false;
                    });
                });

                // Ràng buộc lifecycle với camera
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, preview, analysis);

            } catch (Exception e) {
                Log.e("CAMERA", "Camera error", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFaceOverlay(List<Rect> faceBounds, double similarity, boolean matched, int w, int h) {
        faceOverlay.setFaceBounds(faceBounds, similarity, matched, w, h);
    }

    @Override
    public void navigateToQuiz() {
        if (isFinishing() || isDestroyed()) return;

        runOnUiThread(() -> {
            showToast("Đúng người!");
            previewView.postDelayed(() -> {
                startActivity(new Intent(this, QuizActivity.class));
                finish();
            }, 600);
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(this).get();
                cameraProvider.unbindAll();
            } catch (Exception e) {
                Log.e("CAMERA", "Lỗi unbind", e);
            }
        }, ContextCompat.getMainExecutor(this));

        if (cameraExecutor != null) cameraExecutor.shutdown();
    }

    // Chuyển đổi ảnh từ ImageProxy (YUV) sang Bitmap JPEG, xoay đúng hướng
    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        @SuppressWarnings("UnsafeOptInUsageError")
        Image image = imageProxy.getImage();
        if (image == null) return null;

        int width = image.getWidth();
        int height = image.getHeight();

        /*
            Image ở định dạng YUV_420_888 có 3 "plane" dữ liệu:
                Y: độ sáng (luminance)
                U và V: màu sắc (chrominance)
        */

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

        // Tạo một YuvImage từ mảng byte NV21 — đây là lớp hỗ trợ chuyển đổi thành JPEG (vì BitmapFactory không hiểu YUV).
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        byte[] imageBytes = out.toByteArray(); // Lấy mảng byte JPEG đã nén (chuẩn RGB).

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        // Xoay bitmap theo độ xoay của ImageProxy
        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
