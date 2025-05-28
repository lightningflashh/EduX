package vn.edu.hcmute.edux.controllers.admin;


import android.util.Log;

import java.util.ArrayList;

import vn.edu.hcmute.edux.activities.Admin.ManageTeacherActivity;
import vn.edu.hcmute.edux.services.TeacherService;

public class ManageTeacherController {

    private ManageTeacherActivity activity;
    private TeacherService teacherService;

    public ManageTeacherController(ManageTeacherActivity activity) {
        this.activity = activity;
        this.teacherService = new TeacherService();
    }

    public void loadTeachers() {
        Log.d("ManageTeacherController", "Bắt đầu tải danh sách giảng viên...");
        teacherService.loadTeachers(
                teachers -> {
                    activity.updateTeacherList(teachers);
                    Log.d("ManageTeacherController", "Tải thành công " + teachers.size() + " giảng viên");
                },
                error -> {
                    activity.showToast("Lỗi khi tải danh sách: " + error);
                    activity.updateTeacherList(new ArrayList<>());
                    Log.e("ManageTeacherController", error);
                }
        );
    }
}