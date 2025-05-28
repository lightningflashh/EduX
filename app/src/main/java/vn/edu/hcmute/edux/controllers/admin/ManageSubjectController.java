package vn.edu.hcmute.edux.controllers.admin;



import android.util.Log;

import vn.edu.hcmute.edux.activities.Admin.ManageSubjectActivity;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.services.SubjectService;

public class ManageSubjectController {

    private ManageSubjectActivity activity;
    private SubjectService subjectService;

    public ManageSubjectController(ManageSubjectActivity activity) {
        this.activity = activity;
        this.subjectService = new SubjectService();
    }

    public void loadSubjects() {
        subjectService.loadSubjects(
                subjects -> {
                    activity.updateSubjectList(subjects);
                    Log.d("ManageSubjectController", "Tải thành công " + subjects.size() + " môn học");
                },
                error -> {
                    activity.showToast("Lỗi khi tải danh sách môn học: " + error);
                    Log.e("ManageSubjectController", error);
                }
        );
    }

    public void deleteSubject(Subject subject) {
        subjectService.deleteSubject(
                subject.getClassId(),
                success -> {
                    loadSubjects(); // Tải lại danh sách sau khi xóa
                    activity.showToast("Xóa môn học thành công!");
                },
                error -> activity.showToast(error)
        );
    }
}