package vn.edu.hcmute.edux.adapters;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.StudyMaterial;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.UploadViewHolder> {

    public interface OnItemClickListener {
        void onDownloadClick(StudyMaterial material);
        void onEditClick(StudyMaterial material);
    }

    private final Context context;
    private List<StudyMaterial> materials;
    private final OnItemClickListener listener;

    public UploadAdapter(Context context, List<StudyMaterial> materials, OnItemClickListener listener) {
        this.context = context;
        this.materials = materials;
        this.listener = listener;
    }

    public void setItems(List<StudyMaterial> newMaterials) {
        this.materials = newMaterials;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.document_item, parent, false);
        return new UploadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadViewHolder holder, int position) {
        StudyMaterial material = materials.get(position);

        holder.tvTitle.setText(material.getTitle());
        holder.tvType.setText(material.getType().name());

        // Định dạng ngày upload
        long uploadedAt = material.getUploadedAt();
        if (uploadedAt > 0) {
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date(uploadedAt));
            holder.tvDate.setText(formattedDate);
        } else {
            holder.tvDate.setText("Không rõ");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(material); // callback khi giáo viên bấm vào tài liệu
            }
        });

        // Gán sự kiện tải xuống
        holder.ivDownload.setOnClickListener(v -> {
            Log.d("UploadAdapter", "Download icon clicked: " + material.getTitle());
            if (listener != null) {
                listener.onDownloadClick(material);
            }  else {
                Log.e("UploadAdapter", "Listener is null");
            }
        });


    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    public static class UploadViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvType, tvDate;
        ImageView ivDownload, ivIcon;

        public UploadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvDocumentTitle);
            tvType = itemView.findViewById(R.id.tvDocumentType);
            tvDate = itemView.findViewById(R.id.tvUploadDate);
            ivDownload = itemView.findViewById(R.id.ivDownload);
            ivIcon = itemView.findViewById(R.id.ivDocumentIcon);
        }
    }
}

