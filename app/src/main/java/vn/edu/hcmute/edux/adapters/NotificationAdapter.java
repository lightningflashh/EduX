package vn.edu.hcmute.edux.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.Notification;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<Notification> notificationList;
    private final OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationAdapter(List<Notification> notificationList, OnNotificationClickListener listener) {
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.titleTextView.setText(notification.getTitle());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        String time = notification.getTimestamp() != null ? sdf.format(notification.getTimestamp()) : "Không rõ thời gian";

        holder.timeTextView.setText(time);
        holder.classTextView.setText("Lớp: " + notification.getClassId());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onNotificationClick(notification);
        });
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, timeTextView, classTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_notification_title);
            timeTextView = itemView.findViewById(R.id.text_notification_time);
            classTextView = itemView.findViewById(R.id.text_notification_class);
        }
    }

    private Map<String, String> classIdNameMap = new HashMap<>();


    public NotificationAdapter(List<Notification> notificationList, Map<String, String> classIdNameMap, OnNotificationClickListener listener) {
        this.notificationList = notificationList;
        this.listener = listener;
        this.classIdNameMap = classIdNameMap;
    }

}
