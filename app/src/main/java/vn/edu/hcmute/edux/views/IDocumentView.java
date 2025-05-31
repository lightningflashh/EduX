package vn.edu.hcmute.edux.views;


import java.util.List;

import vn.edu.hcmute.edux.model.StudyMaterial;

/**
 Interface trung gian giữa View (Fragment) và Controller
 Giúp controller gọi lại UI mà không phụ thuộc trực tiếp vào View
* */
public interface IDocumentView {
    void showMaterials(List<StudyMaterial> materials);
    void showMessage(String message);
    void showError(String message);

}
