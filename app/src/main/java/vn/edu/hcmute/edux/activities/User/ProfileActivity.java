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


public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "ProfileActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StudentService studentService;
    private TextView fullnameTextView, mssvTextView, emailTextView, dateTextView, studyTimeTextView, majorTextView;
    private ImageView avatarImageView, btnEditProfile, btnBackProfile;
    private Uri imageUri;
    private String currentImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        studentService = new StudentService();

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

        // Back button
        btnBackProfile.setOnClickListener(v -> finish());

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            fetchStudentData(currentUser.getUid());
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Edit profile button
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
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

    private void showEditProfileDialog() {
        // Inflate custom dialog layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Profile");

        // Create a layout for the dialog
        ConstraintLayout dialogLayout = new ConstraintLayout(this);
        dialogLayout.setPadding(16, 16, 16, 16);

        // Add EditText fields
        EditText fullNameInput = new EditText(this);
        fullNameInput.setId(View.generateViewId());
        fullNameInput.setHint("Full Name");
        fullNameInput.setText(fullnameTextView.getText());
        dialogLayout.addView(fullNameInput, new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT));

        EditText emailInput = new EditText(this);
        emailInput.setId(View.generateViewId());
        emailInput.setHint("Email");
        emailInput.setText(emailTextView.getText());
        dialogLayout.addView(emailInput, new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT));

        TextView birthDateInput = new TextView(this);
        birthDateInput.setId(View.generateViewId());
        birthDateInput.setHint("Birth Date");
        birthDateInput.setText(dateTextView.getText());
        birthDateInput.setPadding(8, 8, 8, 8);
        dialogLayout.addView(birthDateInput, new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT));

        EditText studyTimeInput = new EditText(this);
        studyTimeInput.setId(View.generateViewId());
        studyTimeInput.setHint("Study Time");
        studyTimeInput.setText(studyTimeTextView.getText());
        dialogLayout.addView(studyTimeInput, new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT));

        EditText majorInput = new EditText(this);
        majorInput.setId(View.generateViewId());
        majorInput.setHint("Major");
        majorInput.setText(majorTextView.getText());
        dialogLayout.addView(majorInput, new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT));

        Button uploadImageButton = new Button(this);
        uploadImageButton.setId(View.generateViewId());
        uploadImageButton.setText("Pick Profile Image");
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

        constraintSet.connect(studyTimeInput.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(studyTimeInput.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(studyTimeInput.getId(), ConstraintSet.TOP, birthDateInput.getId(), ConstraintSet.BOTTOM, 16);

        constraintSet.connect(majorInput.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(majorInput.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(majorInput.getId(), ConstraintSet.TOP, studyTimeInput.getId(), ConstraintSet.BOTTOM, 16);

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
            if (newFullName.isEmpty() || newEmail.isEmpty() || newBirthDateStr.isEmpty() ||
                    newStudyTime.isEmpty() || newMajor.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
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
                updates.put("studyTime", newStudyTime);
                updates.put("major", newMajor);
                if (imageUri != null) {
                    String imageDataUrl = convertImageToDataUrl();
                    if (imageDataUrl != null) {
                        updates.put("imageUrl", imageDataUrl);
                    } else {
                        updates.put("imageUrl", currentImageUrl != null ? currentImageUrl : "");
                    }
                } else {
                    updates.put("imageUrl", currentImageUrl != null ? currentImageUrl : "");
                }
                updateStudentInFirestore(currentUser.getUid(), updates);
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

    private void updateStudentInFirestore(String userId, Map<String, Object> updates) {
        db.collection("students").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    fetchStudentData(userId); // Refresh UI
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update Firestore: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}