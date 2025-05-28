package vn.edu.hcmute.edux.controllers.admin;

import android.util.Log;

import vn.edu.hcmute.edux.activities.Admin.AdminActivity;
import vn.edu.hcmute.edux.services.AdminService;

public class AdminController {

    private AdminActivity activity;
    private AdminService service;

    public AdminController(AdminActivity activity) {
        this.activity = activity;
        this.service = new AdminService();
    }

    public void loadCounts() {
        Log.d("AdminController", "loadCounts started");

        // Tải số sinh viên
        service.getStudentCount((count, error) -> {
            if (error == null) {
                activity.updateStudentCount(String.valueOf(count));
            } else {
                handleError(error, "sinh viên");
            }
        });

        // Tải số giảng viên
        service.getTeacherCount((count, error) -> {
            if (error == null) {
                activity.updateTeacherCount(String.valueOf(count));
            } else {
                handleError(error, "giảng viên");
            }
        });

        // Tải số môn học
        service.getSubjectCount((count, error) -> {
            if (error == null) {
                activity.updateSubjectCount(String.valueOf(count));
            } else {
                handleError(error, "môn học");
            }
        });


    }
    private void handleError(String error, String type) {
        Log.e("FirestoreError", type + " query failed: " + error);
        if (error.contains("PERMISSION_DENIED")) {
            activity.showToast("Không có quyền truy cập danh sách " + type);
        } else {
            activity.showToast("Lỗi khi tải số " + type + ": " + error);
        }
    }

    public void logout() {
        service.logout();
        activity.navigateToLogin();
        activity.showToast("Đăng xuất thành công!");
    }
}