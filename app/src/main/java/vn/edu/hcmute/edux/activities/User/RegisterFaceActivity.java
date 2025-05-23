package vn.edu.hcmute.edux.activities.User;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.controllers.user.RegisterFaceController;
import vn.edu.hcmute.edux.views.iRegisterFaceView;

public class RegisterFaceActivity extends AppCompatActivity implements iRegisterFaceView {

    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int CAMERA_PERMISSION_CODE = 1001;

    private ImageView imgPreview, btnBack;
    private Button btnCapture, btnSave;
    private Bitmap faceBitmap;

    private FirebaseUser currentUser;
    private RegisterFaceController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_face);

        initViews();

        try {
            controller = new RegisterFaceController(this);
        } catch (Exception e) {
            showToast("Lỗi khi load FaceNet");
            finish();
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showToast("Bạn chưa đăng nhập");
            finish();
        }

        btnCapture.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());
        btnSave.setOnClickListener(v -> controller.saveFaceEmbedding(faceBitmap, currentUser.getUid()));
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        btnCapture = findViewById(R.id.btnCapture);
        btnSave = findViewById(R.id.btnSave);
        imgPreview = findViewById(R.id.imgFacePreview);
        btnBack = findViewById(R.id.btn_back);
    }

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

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap rawBitmap = (Bitmap) data.getExtras().get("data");
            if (rawBitmap != null && currentUser != null) {
                controller.detectAndExtractFace(rawBitmap, currentUser.getUid());
            }
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFacePreview(Bitmap faceBitmap) {
        this.faceBitmap = faceBitmap;
        imgPreview.setImageBitmap(faceBitmap);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
