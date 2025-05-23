package vn.edu.hcmute.edux.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.List;

public interface iFacialRecognitionView {
    void showToast(String message);
    void showFaceOverlay(List<Rect> faceBounds, double similarity, boolean matched, int width, int height);
    void navigateToQuiz();
    Context getContext();
}