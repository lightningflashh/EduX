package vn.edu.hcmute.edux.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.adapters.UploadAdapter;
import vn.edu.hcmute.edux.controllers.user.DocumentController;
import vn.edu.hcmute.edux.views.IDocumentView;
import vn.edu.hcmute.edux.model.EDocumentType;
import vn.edu.hcmute.edux.model.EUserRole;
import vn.edu.hcmute.edux.model.StudyMaterial;
import vn.edu.hcmute.edux.utils.FileDownloader;
import vn.edu.hcmute.edux.utils.UserSessionManager;

public class DocumentFragment extends Fragment implements IDocumentView {

    private static final int PICK_FILE_REQUEST = 999;

    private RecyclerView recyclerView;
    private UploadAdapter adapter;
    private ChipGroup chipGroup;
    private Button uploadBtn;

    private boolean isTeacher;
    private String classId;
    private String teacherId;
    private EDocumentType documentType;
    private EDocumentType currentFilter = null;

    private final List<StudyMaterial> allMaterials = new ArrayList<>();
    private DocumentController controller;
    private String pendingTitle;

    private SearchView searchView;
    private String searchKeyword = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_document, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        classId = getArguments() != null ? getArguments().getString("class_id") : null;
        teacherId = UserSessionManager.getUserId(requireContext());
        isTeacher = UserSessionManager.getRole(requireContext()).equals(EUserRole.TEACHER.name());

        controller = new DocumentController(this);

        setupRecyclerView(view);
        setupChipGroup(view);
        setupUploadButton(view);

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // không cần xử lý
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchKeyword = newText;
                updateList(); // gọi lại adapter để hiển thị kết quả lọc mới
                return true;
            }
        });


        controller.loadMaterials(classId, isTeacher);
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.list_document);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UploadAdapter(getContext(), allMaterials, new UploadAdapter.OnItemClickListener() {
            @Override
            public void onDownloadClick(StudyMaterial material) {
                // giá trị getContext sẽ truyền vào FileDowloader
                FileDownloader.downloadFile(getContext(), material.getUrl(), material.getTitle());
            }

            @Override
            public void onEditClick(StudyMaterial material) {
                if (isTeacher) showEditMaterialDialog(material);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void setupChipGroup(View view) {
        chipGroup = view.findViewById(R.id.chipGroupCategories);
        Chip chipAll = view.findViewById(R.id.chipAll);
        Chip chipSlides = view.findViewById(R.id.chipSlides);
        Chip chipReference = view.findViewById(R.id.chipReference);

        chipAll.setOnClickListener(v -> updateFilter(null, chipAll, chipSlides, chipReference));
        chipSlides.setOnClickListener(v -> updateFilter(EDocumentType.MATERIAL, chipSlides, chipAll, chipReference));
        chipReference.setOnClickListener(v -> updateFilter(EDocumentType.PRACTICE, chipReference, chipAll, chipSlides));
    }

    private void updateFilter(EDocumentType filter, Chip selected, Chip... others) {
        currentFilter = filter;
        selected.setChecked(true);
        for (Chip c : others) c.setChecked(false);
        updateList();
    }

    private void setupUploadButton(View view) {
        uploadBtn = view.findViewById(R.id.uploadBtn);
        uploadBtn.setVisibility(isTeacher ? View.VISIBLE : View.GONE);
        if (isTeacher) {
            uploadBtn.setOnClickListener(v -> showCustomUploadDialog());
        }
    }

    private void updateList() {
        adapter.setItems(controller.filter(allMaterials, currentFilter, searchKeyword));
    }


    private void showCustomUploadDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_document, null);

        EditText edtTitle = dialogView.findViewById(R.id.edtTitle);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Tài liệu", "Bài tập"}
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(spinnerAdapter);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Thông tin tài liệu")
                .setView(dialogView)
                .setPositiveButton("Tải lên", null)
                .setNegativeButton("Huỷ", null)
                .create();

        dialog.setOnShowListener(dlg -> {
            Button btnUpload = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnUpload.setOnClickListener(v -> {
                String title = edtTitle.getText().toString().trim();
                int selected = spinnerType.getSelectedItemPosition();
                EDocumentType selectedType = (selected == 0) ? EDocumentType.MATERIAL : EDocumentType.PRACTICE;

                if (title.isEmpty()) {
                    edtTitle.setError("Vui lòng nhập tiêu đề");
                    return;
                }

                documentType = selectedType;
                pendingTitle = title;
                dialog.dismiss();
                pickFileToUpload(title);
            });
        });

        dialog.show();
    }

    private void pickFileToUpload(String title) {
        this.pendingTitle = title;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Chọn file tài liệu"), PICK_FILE_REQUEST);
    }

    /**
     * Xử lý kết quả từ Intent.ACTION_GET_CONTENT,
     * → lấy Uri của file mà user chọn
     * → gọi controller để xử lý upload.
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            controller.uploadMaterial(requireContext(), uri, pendingTitle, classId, teacherId, documentType);
        }
    }

    private void showEditMaterialDialog(StudyMaterial material) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_material, null);
        EditText edtTitle = dialogView.findViewById(R.id.edtTitle);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        edtTitle.setText(material.getTitle());

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Chỉnh sửa tài liệu")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (d, which) -> {
                    String newTitle = edtTitle.getText().toString().trim();
                    controller.updateTitle(material.getId(), newTitle, classId);
                })
                .setNegativeButton("Huỷ", null)
                .create();

        btnDelete.setOnClickListener(v -> {
            controller.deleteMaterial(material.getId(), classId);
            dialog.dismiss();
        });

        dialog.show();
    }

    // -------------- IDocumentView Implementation --------------

    @Override
    public void showMaterials(List<StudyMaterial> materials) {
        allMaterials.clear();
        allMaterials.addAll(materials);
        updateList();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
