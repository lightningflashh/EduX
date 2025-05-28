package vn.edu.hcmute.edux.controllers.admin;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.activities.Admin.AddSubjectActivity;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.model.Teacher;
import vn.edu.hcmute.edux.services.SubjectService;

public class AddSubjectController {

    private AddSubjectActivity activity;
    private SubjectService service;
    private List<Teacher> teacherList;

    public AddSubjectController(AddSubjectActivity activity) {
        this.activity = activity;
        this.service = new SubjectService();
        this.teacherList = new ArrayList<>();
    }

    public void loadTeachers() {
        service.loadTeachers(
                teachers -> {
                    teacherList.clear();
                    teacherList.addAll(teachers);
                    List<String> teacherNames = new ArrayList<>();
                    teacherNames.add("Chọn giảng viên...");
                    for (Teacher teacher : teacherList) {
                        teacherNames.add(teacher.getFullName());
                    }
                    activity.updateTeacherList(teacherNames);
                },
                error -> activity.showToast(error)
        );
    }

    public void loadStudents() {
        service.loadStudents(
                students -> activity.updateStudentList(students),
                error -> activity.showToast(error)
        );
    }

    public void createSubject(String subjectId, String subjectName, String description,
                              int selectedTeacherPosition, List<Student> selectedStudents) {
        // Kiểm tra đầu vào
        if (subjectId.isEmpty() || subjectName.isEmpty()) {
            activity.showToast("Vui lòng điền mã và tên môn học");
            return;
        }

        if (selectedTeacherPosition == 0) {
            activity.showToast("Vui lòng chọn giảng viên");
            return;
        }

        Teacher selectedTeacher = teacherList.get(selectedTeacherPosition - 1);
        service.createSubject(
                subjectId, subjectName, description, selectedTeacher, selectedStudents,
                success -> {
                    activity.showToast("Thêm môn học thành công!");
                    activity.navigateBack();
                },
                error -> activity.showToast(error)
        );
    }
}