package vn.edu.hcmute.edux.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FaceOverlayView extends View {
    private List<Rect> faceBounds = new ArrayList<>();
    private double similarity = -1;
    private boolean isMatched = false;

    public FaceOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFaceBounds(List<Rect> rawBounds, double similarity, boolean matched, int imageWidth, int imageHeight) {
        this.faceBounds.clear();
        this.similarity = similarity;
        this.isMatched = matched;

        // Tính scale giữa ảnh và view
        float scaleX = (float) getWidth() / imageWidth;
        float scaleY = (float) getHeight() / imageHeight;

        for (Rect rect : rawBounds) {
            Rect scaled = new Rect(
                    (int) (rect.left * scaleX),
                    (int) (rect.top * scaleY),
                    (int) (rect.right * scaleX),
                    (int) (rect.bottom * scaleY)
            );
            this.faceBounds.add(scaled);
        }

        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        Paint paint = new Paint();
//        paint.setStrokeWidth(5f);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(isMatched ? Color.GREEN : Color.RED);
//
//        Paint textPaint = new Paint();
//        textPaint.setColor(Color.WHITE);
//        textPaint.setTextSize(40f);
//
//        for (Rect rect : faceBounds) {
//            canvas.drawRect(rect, paint);
//            if (similarity >= 0) {
//                canvas.drawText(
//                        String.format("Sim: %.2f", similarity),
//                        rect.left, rect.top - 10,
//                        textPaint
//                );
//                canvas.drawText(
//                        isMatched ? "MATCHED" : "NOT MATCHED",
//                        rect.left, rect.bottom + 40,
//                        textPaint
//                );
//            }
//        }
        super.onDraw(canvas);

        // Vẽ khung viền khuôn mặt
        Paint paint = new Paint();
        paint.setStrokeWidth(8f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(isMatched ? Color.GREEN : Color.RED);

        // Vẽ văn bản: similarity và trạng thái
        Paint textPaint = new Paint();
        textPaint.setColor(Color.YELLOW); // dễ thấy hơn màu trắng
        textPaint.setTextSize(60f);       // tăng kích thước chữ
        textPaint.setFakeBoldText(true);

        for (Rect rect : faceBounds) {
            // Vẽ khung
            canvas.drawRect(rect, paint);

            if (similarity >= 0) {
                // Vẽ similarity ở phía trên
                canvas.drawText(
                        String.format("Similarity: %.2f", similarity),
                        rect.left,
                        rect.top - 20,
                        textPaint
                );

                // Vẽ trạng thái bên dưới khung
                canvas.drawText(
                        isMatched ? "MATCHED" : "NOT MATCHED",
                        rect.left,
                        rect.bottom + 70,
                        textPaint
                );
            }
        }
    }
}
