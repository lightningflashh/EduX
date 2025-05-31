package vn.edu.hcmute.edux.views;

import android.content.Context;
import android.graphics.Bitmap;

public interface iRegisterFaceView {
    void showToast(String message);
    void showFacePreview(Bitmap faceBitmap);
    void finishActivity();
    Context getContext();
}
