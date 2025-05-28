package vn.edu.hcmute.edux.controllers.user;


import vn.edu.hcmute.edux.fragments.QuizFragment;
import vn.edu.hcmute.edux.services.QuizService;
import vn.edu.hcmute.edux.utils.UserSessionManager;

public class QuizFragmentController {

    private QuizFragment fragment;
    private QuizService quizService;

    public QuizFragmentController(QuizFragment fragment) {
        this.fragment = fragment;
        this.quizService = new QuizService();
    }

    public void checkUserRole() {
        String role = UserSessionManager.getRole(fragment.getContext());
        if (role == null) {
            fragment.showToast("Người dùng chưa đăng nhập!");
            fragment.setCreateQuizButtonVisibility(false);
            return;
        }

        if ("TEACHER".equals(role)) {
            fragment.setCreateQuizButtonVisibility(true); // Hiển thị nút cho TEACHER
        } else if ("STUDENT".equals(role)) {
            fragment.setCreateQuizButtonVisibility(false); // Ẩn nút cho STUDENT
        } else {
            fragment.showToast("Không tìm thấy thông tin vai trò!");
            fragment.setCreateQuizButtonVisibility(false);
        }
    }

    public void handleCreateQuizButtonClick(String classId) {
        fragment.navigateToCreateQuizActivity(classId);
    }

    public void loadQuizListByClass(String classId) {
        quizService.getQuizzesByClassId(
                classId,
                quizzes -> fragment.updateQuizList(quizzes),
                error -> fragment.showToast("Lỗi tải quiz: " + error.getMessage())
        );
    }
}