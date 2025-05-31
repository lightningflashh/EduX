package vn.edu.hcmute.edux.controllers.user;


import android.content.Context;
import android.net.Uri;

import java.util.List;

import vn.edu.hcmute.edux.views.IDocumentView;
import vn.edu.hcmute.edux.model.EDocumentType;
import vn.edu.hcmute.edux.model.StudyMaterial;
import vn.edu.hcmute.edux.services.DocumentService;

public class DocumentController {

    private final IDocumentView view;
    private final DocumentService documentService = new DocumentService();

    // Constructor truyền View vào
    public DocumentController(IDocumentView view) {
        this.view = view;
    }

    /**
     * Load tài liệu từ Firebase theo vai trò (giáo viên / học sinh).
     * Gọi đến DocumentService để lấy dữ liệu, sau đó đẩy kết quả về View.
     */
    public void loadMaterials(String classId, boolean isTeacher) {
        if (isTeacher) {
            documentService.loadMaterialsForTeacher(classId, new DocumentService.MaterialListener() {
                @Override
                public void onSuccess(List<StudyMaterial> materials) {
                    view.showMaterials(materials); // gửi về View hiển thị
                }

                @Override
                public void onError(Exception e) {
                    view.showError("Lỗi tải tài liệu: " + e.getMessage());
                }
            });
        } else {
            documentService.loadMaterialsForStudent(classId, new DocumentService.MaterialListener() {
                @Override
                public void onSuccess(List<StudyMaterial> materials) {
                    view.showMaterials(materials);
                }

                @Override
                public void onError(Exception e) {
                    view.showError("Lỗi tải tài liệu: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Gọi model để upload tài liệu mới.
     * Sau khi thành công, tự động gọi lại loadMaterials() để refresh UI.
     */
    public void uploadMaterial(Context context, Uri uri, String title, String classId,
                               String teacherId, EDocumentType type) {
        documentService.uploadMaterial(context, uri, title, classId, teacherId, type, new DocumentService.SimpleListener() {
            @Override
            public void onSuccess() {
                view.showMessage("Tải lên thành công");
                loadMaterials(classId, true); // load lại danh sách
            }

            @Override
            public void onError(Exception e) {
                view.showError("Tải lên thất bại: " + e.getMessage());
            }
        });
    }

    /**
     * Cập nhật tiêu đề tài liệu.
     * Sau khi cập nhật xong sẽ gọi lại loadMaterials() để refresh.
     */
    public void updateTitle(String docId, String newTitle, String classId) {
        documentService.updateTitle(docId, newTitle, new DocumentService.SimpleListener() {
            @Override
            public void onSuccess() {
                view.showMessage("Đã cập nhật");
                loadMaterials(classId, true);
            }

            @Override
            public void onError(Exception e) {
                view.showError("Cập nhật thất bại: " + e.getMessage());
            }
        });
    }

    /**
     * Xóa một tài liệu theo ID.
     * Sau khi xóa sẽ load lại danh sách tài liệu.
     */
    public void deleteMaterial(String docId, String classId) {
        documentService.deleteMaterial(docId, new DocumentService.SimpleListener() {
            @Override
            public void onSuccess() {
                view.showMessage("Đã xóa tài liệu");
                loadMaterials(classId, true);
            }

            @Override
            public void onError(Exception e) {
                view.showError("Xóa thất bại: " + e.getMessage());
            }
        });
    }

    /**
     * Lọc danh sách tài liệu theo loại và từ khóa tìm kiếm.
     * Gọi đến hàm filter của DocumentService.
     */
    public List<StudyMaterial> filter(List<StudyMaterial> all, EDocumentType type, String keyword) {
        return documentService.filter(all, type, keyword);
    }

}
