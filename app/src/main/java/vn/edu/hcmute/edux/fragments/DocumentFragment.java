package vn.edu.hcmute.edux.fragments;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import vn.edu.hcmute.edux.R;

public class DocumentFragment extends Fragment {
    public DocumentFragment() {
        super(R.layout.fragment_document);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String classId = getArguments() != null ? getArguments().getString("class_id") : null;
        // TODO: Dùng classId để load document
    }
}
