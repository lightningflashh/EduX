package vn.edu.hcmute.edux.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class FileDownloader {

    /**
     * Hàm này dùng để tải một file từ internet về thiết bị Android
     *
     * @param context   Context của Activity để truy cập DownloadManager
     * @param fileUrl   URL của file cần tải
     * @param fileName  Tên file muốn lưu
     */
    public static void downloadFile(Context context, String fileUrl, String fileName) {
        //B1: Tạo đối tượng Request từ URL cần tải
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        //B2: Thiết lập metadata cho request
        request.setTitle(fileName); // tiêu đề hiển thị trong notification
        request.setDescription("Đang tải tài liệu...");
        //B3: Hiển thị thông báo sau khi tải xong
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //B4: Thiết lập nơi lưu file vào thư mục "Downloads"
        // Environment.DIRECTORY_DOWNLOADS = /sdcard/Download/
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        //B5: Lấy instance của DownloadManager từ hệ thống Android
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //B6: Nếu lấy được downloadManager
        if (downloadManager != null) {
            // Thêm request vào hàng đợi download
            downloadManager.enqueue(request);
            // Thông báo trên UI
            Toast.makeText(context, "Đang tải " + fileName, Toast.LENGTH_SHORT).show();
        } else {
            // Nếu không lấy được downloadManager, báo lỗi
            Toast.makeText(context, "Không thể tải file", Toast.LENGTH_SHORT).show();
        }
    }
}
