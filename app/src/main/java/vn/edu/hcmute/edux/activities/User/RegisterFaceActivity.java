package vn.edu.hcmute.edux.activities.User;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.services.StudentService;
import vn.edu.hcmute.edux.utils.FaceNetHelper;

public class RegisterFaceActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int CAMERA_PERMISSION_CODE = 1001;

    private Button btnCapture, btnSave;
    private ImageView btnBack;
    private ImageView imgPreview;
    private Bitmap faceBitmap;
    private FaceNetHelper faceNetHelper;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_face);

        btnCapture = findViewById(R.id.btnCapture);
        btnSave = findViewById(R.id.btnSave);
        imgPreview = findViewById(R.id.imgFacePreview);
        btnBack = findViewById(R.id.btn_back);

        try {
            faceNetHelper = new FaceNetHelper(this);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi load FaceNet", Toast.LENGTH_SHORT).show();
            finish();
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnCapture.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());
        btnSave.setOnClickListener(v -> saveEmbeddingToFirestore());
        btnBack.setOnClickListener(v -> finish());
    }

    // Check camera permission and open camera
    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền camera để sử dụng chức năng này", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap rawBitmap = (Bitmap) data.getExtras().get("data");
            if (rawBitmap != null) {
                detectFace(rawBitmap);
            }
        }
    }

    private void detectFace(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build();
        FaceDetector detector = FaceDetection.getClient(options);

        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty()) {
                        Rect rawBounds = faces.get(0).getBoundingBox(); // Lấy bounding box của khuôn mặt đầu tiên
                        Rect bounds = adjustBoundsToBitmap(rawBounds, bitmap); // Điều chỉnh kích thước bounding box

                        try {
                            faceBitmap = Bitmap.createBitmap(bitmap,
                                    bounds.left, bounds.top,
                                    bounds.width(), bounds.height());
                            faceBitmap = flipBitmapHorizontal(faceBitmap); // Lật ngang ảnh khuôn mặt do camera chụp ngược
                            imgPreview.setImageBitmap(faceBitmap);
                            Toast.makeText(this, "Đã cắt khuôn mặt", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            faceBitmap = null;
                            Toast.makeText(this, "Lỗi khi crop ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy khuôn mặt!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi phát hiện khuôn mặt", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveEmbeddingToFirestore() {
        if (faceBitmap == null) {
            Toast.makeText(this, "Vui lòng chụp ảnh khuôn mặt", Toast.LENGTH_SHORT).show();
            return;
        }

        float[] embedding = faceNetHelper.getFaceEmbedding(faceBitmap);
        if (embedding == null || embedding.length != 512) {
            Toast.makeText(this, "Lỗi trích xuất embedding", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Double> embList = new ArrayList<>();
        for (float val : embedding) embList.add((double) val);

        StudentService studentService = new StudentService();
        studentService.updateStudentEmbedding(
                currentUser.getUid(),
                embList,
                () -> {
                    Toast.makeText(this, "Đã lưu embedding!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                errorMessage -> {
                    Toast.makeText(this, "Lỗi lưu Firestore: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
        );
    }

    // Điều chỉnh kích thước của vùng mặt để đảm bảo không vượt quá kích thước của bitmap
    private Rect adjustBoundsToBitmap(Rect bounds, Bitmap bitmap) {
        int left = Math.max(0, bounds.left);
        int top = Math.max(0, bounds.top);
        int right = Math.min(bitmap.getWidth(), bounds.right);
        int bottom = Math.min(bitmap.getHeight(), bounds.bottom);

        int width = right - left;
        int height = bottom - top;

        if (width < 10 || height < 10) {
            return new Rect(0, 0, 0, 0); // Tránh crash
        }

        return new Rect(left, top, right, bottom);
    }

    private Bitmap flipBitmapHorizontal(Bitmap src) {
        Matrix matrix = new Matrix();
        // Lật ngang ảnh (-1), giữ nguyên trục Y (1), điểm gốc là giữa ảnh
        matrix.postScale(-1, 1, src.getWidth() / 2f, src.getHeight() / 2f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}

