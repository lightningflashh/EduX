package vn.edu.hcmute.edux.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class DownloadHelper {

    public static void downloadFile(Context context, String fileUrl, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle(fileName);
        request.setDescription("Đang tải tài liệu...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Thiết lập nơi lưu file
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(context, "Đang tải " + fileName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Không thể tải file", Toast.LENGTH_SHORT).show();
        }
    }
}
