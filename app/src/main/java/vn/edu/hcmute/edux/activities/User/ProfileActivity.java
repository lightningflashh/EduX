package vn.edu.hcmute.edux.activities.User;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.activities.LoginActivity;
import vn.edu.hcmute.edux.services.StudentService;
import vn.edu.hcmute.edux.services.TeacherService;
import vn.edu.hcmute.edux.utils.UserSessionManager;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "ProfileActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StudentService studentService;
    private TeacherService teacherService;
    private TextView fullnameTextView, mssvTextView, emailTextView, dateTextView, studyTimeTextView, majorTextView, mssvLabel, studyTimeLabel;
    private ImageView avatarImageView, btnEditProfile, btnBackProfile;
    View divider1, divider3;
    private Uri imageUri;
    private String currentImageUrl;

    private String role, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        role = UserSessionManager.getRole(this);
        userId = UserSessionManager.getUserId(this);

        init();

        renderInfo();

        btnBackProfile.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
    }

    private void init() {
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        studentService = new StudentService();
        teacherService = new TeacherService();

        // Initialize UI elements
        fullnameTextView = findViewById(R.id.fullname);
        mssvTextView = findViewById(R.id.mssv);
        emailTextView = findViewById(R.id.email);
        dateTextView = findViewById(R.id.date);
        studyTimeTextView = findViewById(R.id.studyTime);
        majorTextView = findViewById(R.id.major);
        avatarImageView = findViewById(R.id.avarta);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnBackProfile = findViewById(R.id.btn_back_profile);
        mssvLabel = findViewById(R.id.mssvLabel);
        studyTimeLabel = findViewById(R.id.studyTimeLabel);
        divider1 = findViewById(R.id.divider1);
        divider3 = findViewById(R.id.divider3);
    }

    private void renderInfo() {

        if (userId != null) {
            if ("TEACHER".equals(role)) {
                fetchTeacherData(userId);
            } else if ("STUDENT".equals(role)) {
                fetchStudentData(userId);
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void fetchStudentData(String userId) {
        studentService.loadStudentById(userId,
                student -> {
                    // Update UI
                    fullnameTextView.setText(student.getFullName() != null ? student.getFullName() : "Unknown");
                    mssvTextView.setText(student.getStudentCode() != null ? student.getStudentCode() : "N/A");
                    emailTextView.setText(student.getEmail() != null ? student.getEmail() : "N/A");
                    studyTimeTextView.setText(student.getStudyTime() != null ? student.getStudyTime() : "N/A");
                    majorTextView.setText(student.getMajor() != null ? student.getMajor() : "N/A");

                    // Format birthDate
                    if (student.getBirthDate() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateTextView.setText(sdf.format(student.getBirthDate()));
                    } else {
                        dateTextView.setText("N/A");
                    }

                    // Load profile image
                    currentImageUrl = student.getImageUrl();
                    if (student.getImageUrl() != null && !student.getImageUrl().isEmpty()) {
                        Glide.with(this).load(student.getImageUrl()).error(R.drawable.img_1).into(avatarImageView);
                    } else {
                        Glide.with(this).load(R.drawable.img_1).into(avatarImageView);
                    }
                },
                error -> {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    fullnameTextView.setText("Unknown");
                    mssvTextView.setText("N/A");
                    emailTextView.setText("N/A");
                    dateTextView.setText("N/A");
                    studyTimeTextView.setText("N/A");
                    majorTextView.setText("N/A");
                    Glide.with(this).load(R.drawable.img_1).into(avatarImageView);
                });
    }

    private void fetchTeacherData(String userId) {
        teacherService.loadTeacherById(userId,
                teacher -> {
                    // Update UI
                    fullnameTextView.setText(teacher.getFullName() != null ? teacher.getFullName() : "Unknown");
                    emailTextView.setText(teacher.getEmail() != null ? teacher.getEmail() : "N/A");
                    majorTextView.setText(teacher.getMajor() != null ? teacher.getMajor() : "N/A");

                    // Format birthDate
                    if (teacher.getBirthDate() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateTextView.setText(sdf.format(teacher.getBirthDate()));
                    } else {
                        dateTextView.setText("N/A");
                    }

                    // Load profile image
                    currentImageUrl = teacher.getImageUrl();
                    if (teacher.getImageUrl() != null && !teacher.getImageUrl().isEmpty()) {
                        Glide.with(this).load(teacher.getImageUrl()).error(R.drawable.img_1).into(avatarImageView);
                    } else {
                        Glide.with(this).load(R.drawable.img_1).into(avatarImageView);
                    }
                    mssvLabel.setVisibility(View.GONE);
                    mssvTextView.setVisibility(View.GONE);
                    studyTimeLabel.setVisibility(View.GONE);
                    studyTimeTextView.setVisibility(View.GONE);
                    divider1.setVisibility(View.GONE);
                    divider3.setVisibility(View.GONE);
                },
                error -> {
                    Toast.makeText(this, "Lỗi khi tải thông tin giáo viên: " + error, Toast.LENGTH_SHORT).show();
                    fullnameTextView.setText("Unknown");
                    emailTextView.setText("N/A");
                    majorTextView.setText("N/A");
                    dateTextView.setText("N/A");
                    mssvTextView.setText("");
                    studyTimeTextView.setText("");
                    mssvLabel.setVisibility(View.GONE);
                    mssvTextView.setVisibility(View.GONE);
                    studyTimeLabel.setVisibility(View.GONE);
                    studyTimeTextView.setVisibility(View.GONE);
                    divider1.setVisibility(View.GONE);
                    divider3.setVisibility(View.GONE);
                    Glide.with(this).load(R.drawable.img_1).into(avatarImageView);
                });
    }

    private boolean isAnyEmpty(String... values) {
        for (String val : values) {
            if (val == null || val.trim().isEmpty()) return true;
        }
        return false;
    }

    private EditText createInput(String hint, String text, ConstraintLayout parent) {
        EditText input = new EditText(this);
        input.setId(View.generateViewId());
        input.setHint(hint);
        input.setText(text);
        parent.addView(input, new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        return input;
    }

    private void showEditProfileDialog() {
        // Inflate custom dialog layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa thông tin cá nhân");

        // Create a layout for the dialog
        ConstraintLayout dialogLayout = new ConstraintLayout(this);
        dialogLayout.setPadding(16, 16, 16, 16);

        // Add EditText fields
        EditText fullNameInput = createInput("Họ và tên", fullnameTextView.getText().toString(), dialogLayout);
        EditText emailInput = createInput("Email", emailTextView.getText().toString(), dialogLayout);

        TextView birthDateInput = new TextView(this);
        birthDateInput.setId(View.generateViewId());
        birthDateInput.setHint("Ngày sinh");
        birthDateInput.setText(dateTextView.getText());
        birthDateInput.setPadding(8, 8, 8, 8);
        dialogLayout.addView(birthDateInput, new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT));

        EditText studyTimeInput = new EditText(this);
        studyTimeInput.setId(View.generateViewId());
        studyTimeInput.setHint("Thời gian học dự kiến");
        studyTimeInput.setText(studyTimeTextView.getText());
        if (role.equals("STUDENT")) {
            dialogLayout.addView(studyTimeInput, new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        }
        EditText majorInput = createInput("Chuyên ngành", majorTextView.getText().toString(), dialogLayout);

        Button uploadImageButton = new Button(this);
        uploadImageButton.setId(View.generateViewId());
        uploadImageButton.setText("Tải hình đại diện");
        dialogLayout.addView(uploadImageButton, new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT));

        // Set constraints
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(dialogLayout);
        constraintSet.connect(fullNameInput.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(fullNameInput.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(fullNameInput.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

        constraintSet.connect(emailInput.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(emailInput.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(emailInput.getId(), ConstraintSet.TOP, fullNameInput.getId(), ConstraintSet.BOTTOM, 16);

        constraintSet.connect(birthDateInput.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(birthDateInput.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(birthDateInput.getId(), ConstraintSet.TOP, emailInput.getId(), ConstraintSet.BOTTOM, 16);

        constraintSet.connect(majorInput.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(majorInput.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

        if (role.equals("STUDENT")) {
            constraintSet.connect(studyTimeInput.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(studyTimeInput.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.connect(studyTimeInput.getId(), ConstraintSet.TOP, birthDateInput.getId(), ConstraintSet.BOTTOM, 16);

            constraintSet.connect(majorInput.getId(), ConstraintSet.TOP, studyTimeInput.getId(), ConstraintSet.BOTTOM, 16);
        } else {
            constraintSet.connect(majorInput.getId(), ConstraintSet.TOP, birthDateInput.getId(), ConstraintSet.BOTTOM, 16);
        }
        constraintSet.connect(uploadImageButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(uploadImageButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(uploadImageButton.getId(), ConstraintSet.TOP, majorInput.getId(), ConstraintSet.BOTTOM, 16);

        constraintSet.applyTo(dialogLayout);

        // Date picker for birthDate
        birthDateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ProfileActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        birthDateInput.setText(sdf.format(calendar.getTime()));
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        // Image picker
        uploadImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        builder.setView(dialogLayout);

        // Dialog buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newFullName = fullNameInput.getText().toString().trim();
            String newEmail = emailInput.getText().toString().trim();
            String newBirthDateStr = birthDateInput.getText().toString().trim();
            String newStudyTime = studyTimeInput.getText().toString().trim();
            String newMajor = majorInput.getText().toString().trim();

            // Validate inputs
            if (role.equals("STUDENT") && isAnyEmpty(newFullName, newEmail, newBirthDateStr, newStudyTime, newMajor)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (role.equals("TEACHER") && isAnyEmpty(newFullName, newEmail, newBirthDateStr, newMajor)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse birthDate
            Date newBirthDate = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                newBirthDate = sdf.parse(newBirthDateStr);
            } catch (Exception e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update Firestore
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("fullName", newFullName);
                updates.put("email", newEmail);
                updates.put("birthDate", newBirthDate);
                updates.put("major", newMajor);
                if (role.equals("STUDENT")) {
                    updates.put("studyTime", newStudyTime);
                }

                String finalImageUrl = currentImageUrl != null ? currentImageUrl : "";
                if (imageUri != null) {
                    String imageDataUrl = convertImageToDataUrl();
                    if (imageDataUrl != null) {
                        finalImageUrl = imageDataUrl;
                    }
                }
                updates.put("imageUrl", finalImageUrl);

                String collection = role.equals("STUDENT") ? "students" : "teachers";
                updateInFireStore(currentUser.getUid(), collection, updates);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            avatarImageView.setImageURI(imageUri);
        }
    }

    private String convertImageToDataUrl() {
        try {
            // Load and compress image
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();

            // Convert to Base64
            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            String dataUrl = "data:image/jpeg;base64," + base64Image;
            Log.d(TAG, "Generated data URL length: " + dataUrl.length());

            // Check size (Firestore document size limit: 1 MB)
            if (dataUrl.length() > 500_000) {
                Toast.makeText(this, "Image too large after compression", Toast.LENGTH_SHORT).show();
                return null;
            }

            return dataUrl;
        } catch (Exception e) {
            Log.e(TAG, "Error converting image to data URL: " + e.getMessage(), e);
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void updateInFireStore(String userId, String collectionsPath, Map<String, Object> updates) {
        db.collection(collectionsPath).document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    if (role.equals("STUDENT")) {
                        fetchStudentData(userId);
                    } else {
                        fetchTeacherData(userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}