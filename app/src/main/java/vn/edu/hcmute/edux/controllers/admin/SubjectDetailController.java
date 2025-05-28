package vn.edu.hcmute.edux.controllers.admin;



import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.activities.Admin.SubjectDetailActivity;
import vn.edu.hcmute.edux.model.Student;
import vn.edu.hcmute.edux.model.Subject;
import vn.edu.hcmute.edux.model.Teacher;
import vn.edu.hcmute.edux.services.SubjectService;

public class SubjectDetailController {

    private SubjectDetailActivity activity;
    private SubjectService subjectService;
    private Subject subject;
    private List<Teacher> teacherList;
    private List<Student> selectedStudents;
    private Teacher originalTeacher;

    public SubjectDetailController(SubjectDetailActivity activity) {
        this.activity = activity;
        this.subjectService = new SubjectService();
        this.teacherList = new ArrayList<>();
        this.selectedStudents = new ArrayList<>();
    }

    public void loadSubject(String classId) {
        subjectService.loadSubjectById(
                classId,
                loadedSubject -> {
                    this.subject = loadedSubject;
                    activity.displaySubject(subject);
                    loadTeachers();
                    loadStudents();
                },
                error -> {
                    activity.showToast(error);
                    activity.finishActivity();
                }
        );
    }

    private void loadTeachers() {
        subjectService.loadTeachers(
                teachers -> {
                    teacherList.clear();
                    teacherList.addAll(teachers);
                    List<String> teacherNames = new ArrayList<>();
                    teacherNames.add("Chọn giảng viên...");
                    for (Teacher teacher : teacherList) {
                        teacherNames.add(teacher.getFullName());
                        if (teacher.getTeachingClasses() != null) {
                            boolean hasSubject = false;
                            for (Subject s : teacher.getTeachingClasses()) {
                                if (s != null && subject.getClassId().equals(s.getClassId())) {
                                    hasSubject = true;
                                    break;
                                }
                            }
                            if (hasSubject) {
                                originalTeacher = teacher;
                                activity.setTeacherSelection(teacherNames.indexOf(teacher.getFullName()));
                            }
                        }
                    }
                    activity.updateTeacherList(teacherNames);
                },
                error -> activity.showToast(error)
        );
    }

    private void loadStudents() {
        subjectService.loadStudents(
                students -> {
                    activity.updateStudentList(students);
                    selectedStudents.clear();
                    for (Student student : students) {
                        if (student.getJoinedClasses() != null) {
                            boolean hasSubject = false;
                            for (Subject s : student.getJoinedClasses()) {
                                if (s != null && subject.getClassId().equals(s.getClassId())) {
                                    hasSubject = true;
                                    break;
                                }
                            }
                            if (hasSubject) {
                                selectedStudents.add(student);
                                activity.setStudentSelection(student, true);
                            }
                        }
                    }
                },
                error -> activity.showToast(error)
        );
    }

    public void saveChanges(String subjectId, String subjectName, String description, int selectedTeacherPosition, List<Student> newSelectedStudents) {
        // Kiểm tra đầu vào
        if (subjectName.isEmpty()) {
            activity.showToast("Vui lòng điền tên môn học");
            return;
        }

        if (selectedTeacherPosition == 0) {
            activity.showToast("Vui lòng chọn giảng viên");
            return;
        }

        Teacher newTeacher = teacherList.get(selectedTeacherPosition - 1);
        subjectService.updateSubject(
                subjectId, subjectName, description, originalTeacher, newTeacher, selectedStudents, newSelectedStudents,
                success -> {
                    activity.showToast("Cập nhật môn học thành công!");
                    activity.disableEditMode();
                    subject = new Subject(subjectId, subjectName, description, null);
                    originalTeacher = newTeacher;
                    selectedStudents = new ArrayList<>(newSelectedStudents);
                },
                error -> activity.showToast(error)
        );
    }
}