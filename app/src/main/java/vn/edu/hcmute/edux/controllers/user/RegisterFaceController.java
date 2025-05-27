package vn.edu.hcmute.edux.controllers.user;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.services.StudentService;
import vn.edu.hcmute.edux.utils.FaceNetHelper;
import vn.edu.hcmute.edux.views.iRegisterFaceView;

public class RegisterFaceController {

    private final iRegisterFaceView view;
    private final FaceNetHelper faceNetHelper;

    public RegisterFaceController(iRegisterFaceView view) throws Exception {
        this.view = view;
        this.faceNetHelper = new FaceNetHelper(view.getContext());
    }

    public void detectAndExtractFace(Bitmap rawBitmap, String userId) {
        InputImage image = InputImage.fromBitmap(rawBitmap, 0);
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build();

        FaceDetector detector = FaceDetection.getClient(options);

        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty()) {
                        Rect rawBounds = faces.get(0).getBoundingBox();
                        Rect safeBounds = adjustBoundsToBitmap(rawBounds, rawBitmap);

                        try {
                            Bitmap croppedFace = Bitmap.createBitmap(
                                    rawBitmap,
                                    safeBounds.left,
                                    safeBounds.top,
                                    safeBounds.width(),
                                    safeBounds.height()
                            );

                            Bitmap flippedFace = flipBitmapHorizontal(croppedFace);
                            view.showFacePreview(flippedFace);

                        } catch (Exception e) {
                            view.showToast("Lỗi khi crop ảnh: " + e.getMessage());
                        }

                    } else {
                        view.showToast("Không tìm thấy khuôn mặt!");
                    }
                })
                .addOnFailureListener(e -> view.showToast("Lỗi phát hiện khuôn mặt: " + e.getMessage()));
    }

    public void saveFaceEmbedding(Bitmap faceBitmap, String userId) {
        if (faceBitmap == null) {
            view.showToast("Vui lòng chụp ảnh khuôn mặt");
            return;
        }

        float[] embedding = faceNetHelper.getFaceEmbedding(faceBitmap);
        if (embedding == null || embedding.length != 512) {
            view.showToast("Lỗi trích xuất embedding");
            return;
        }

        List<Double> embList = new ArrayList<>();
        for (float val : embedding) embList.add((double) val);

        new StudentService().updateStudentEmbedding(
                userId,
                embList,
                () -> {
                    view.showToast("Đã lưu embedding!");
                    view.finishActivity();
                },
                errorMessage -> view.showToast("Lỗi lưu Firestore: " + errorMessage)
        );
    }

    // Điều chỉnh bounds để đảm bảo không vượt quá kích thước bitmap
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

    // Lật ảnh theo chiều ngang
    private Bitmap flipBitmapHorizontal(Bitmap src) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, src.getWidth() / 2f, src.getHeight() / 2f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}
