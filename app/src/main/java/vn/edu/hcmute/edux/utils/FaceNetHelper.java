package vn.edu.hcmute.edux.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FaceNetHelper {

    private static final String TAG = "FaceNetHelper";
    private final Interpreter interpreter;
    private final int inputSize = 160; // Kích thước đầu vào của mô hình FaceNet là 160x160 pixels

    public FaceNetHelper(Context context) throws IOException {
        interpreter = new Interpreter(loadModelFile(context), new Interpreter.Options()); // Tạo Interpreter từ mô hình FaceNet
    }

    // Load mô hình FaceNet từ file assets
    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        FileInputStream inputStream = new FileInputStream(
                context.getAssets().openFd("facenet.tflite").getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = context.getAssets().openFd("facenet.tflite").getStartOffset();
        long declaredLength = context.getAssets().openFd("facenet.tflite").getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public float[] getFaceEmbedding(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Bitmap input is NULL");
            return new float[512];
        }

        Bitmap squareBitmap = cropToSquare(bitmap); // Cắt ảnh thành hình vuông
        Bitmap resized = Bitmap.createScaledBitmap(squareBitmap, inputSize, inputSize, true); // Thay đổi kích thước về 160x160 pixels nếu cần

        if (resized.getWidth() == 0 || resized.getHeight() == 0) {
            Log.e(TAG, "Resized bitmap has invalid dimensions: " +
                    resized.getWidth() + "x" + resized.getHeight());
            return new float[512];
        }

        // input[batch][y][x][channel]
        // Tạo mảng đầu vào với kích thước [1][inputSize][inputSize][3] cho RGB
        float[][][][] input = new float[1][inputSize][inputSize][3];
        float[][] output = new float[1][512];

        // Tách từng pixel RGB, chia cho 255 để chuẩn hóa về [0, 1]
        for (int y = 0; y < inputSize; y++) {
            for (int x = 0; x < inputSize; x++) {
                int px = resized.getPixel(x, y);
                input[0][y][x][0] = ((px >> 16) & 0xFF) / 255.0f; // R bit 16-23
                input[0][y][x][1] = ((px >> 8) & 0xFF) / 255.0f;  // G bit 8-15
                input[0][y][x][2] = (px & 0xFF) / 255.0f;         // B bit 0-7
            }
        }

        try {
            interpreter.run(input, output);
        } catch (Exception e) {
            Log.e(TAG, "Error while running model: " + e.getMessage());
            return new float[512];
        }

        return l2Normalize(output[0]);
    }

    private Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int edgeLength = Math.min(width, height);

        int offsetX = (width - edgeLength) / 2;
        int offsetY = (height - edgeLength) / 2;

        return Bitmap.createBitmap(bitmap, offsetX, offsetY, edgeLength, edgeLength);
    }

    /*
        Cosine similarity đo góc giữa hai vector chứ không quan tâm đến độ dài.
        Do đó, cần chuẩn hóa vector về cùng một độ dài (l2 normalization).
        L2 normalization chia mỗi phần tử của vector cho độ dài của nó, làm cho tổng bình phương các phần tử bằng 1.
     */

    private float[] l2Normalize(float[] embedding) {
        float sum = 0f;
        for (float val : embedding) {
            sum += val * val; // Tính tổng bình phương tất cả phần tử trong embedding
        }
        float norm = (float) Math.sqrt(sum);
        if (norm == 0f) return embedding; // Nếu norm bằng 0, trả về embedding gốc để tránh chia cho 0
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] /= norm; // Chia mỗi phần tử cho norm để chuẩn hóa
        }
        return embedding;
    }

    public void close() {
        interpreter.close();
    }
}
