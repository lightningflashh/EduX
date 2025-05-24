package vn.edu.hcmute.edux.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class để xử lý file trong Android.
 * Chuyển đổi từ Uri thành File thực (dành cho upload, preview,...)
 */
public class FileUtils {

    /**
     * Tạo file tạm từ Uri (thường được chọn từ ACTION_GET_CONTENT)
     *
     * @param context Context hiện tại (thường là Fragment hoặc Activity)
     * @param uri     Uri của file được chọn từ storage
     * @return        File được tạo tạm thời từ nội dung của Uri
     */
    public static File createTempFileFromUri(Context context, Uri uri) throws Exception {
        // Lấy tên file thực tế từ Uri (ví dụ: "abc.pdf", "image.png")
        String fileName = getFileName(context, uri);
        // Tạo file tạm với tiền tố và hậu tố rõ ràng, trong thư mục cache nội bộ
        File tempFile = File.createTempFile("upload_", "_" + fileName, context.getCacheDir()); //Tạo file tạm; context.getCacheDir() trả về thư mục /data/data/<package>/cache/ để user lưu file nội bộ tạm thời

        try (

                InputStream in = context.getContentResolver().openInputStream(uri); //Mở InputStream từ Uri; context.getContentResolver() dùng để truy cập nội dung file do user chọn
                OutputStream out = new FileOutputStream(tempFile)  // Mở output stream tới file tạm
        ) {
            byte[] buffer = new byte[4096]; // Buffer để đọc/ghi từng phần dữ liệu
            int read;
            // Đọc từng khối dữ liệu và ghi sang file tạm
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        // Trả về file tạm đã ghi xong
        return tempFile;
    }

    /**
     * Lấy tên file gốc từ Uri bằng ContentResolver
     *
     * @param context Context để truy cập hệ thống
     * @param uri     Uri cần lấy tên file
     * @return        Tên file, nếu không lấy được sẽ trả "file"
     */
    private static String getFileName(Context context, Uri uri) {
        String result = "file"; // giá trị mặc định nếu không có DISPLAY_NAME
        // Truy vấn thông tin từ Uri
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                // Tìm index của cột tên file
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                // Nếu tồn tại, lấy tên file
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
            }
        }
        // Trả về tên file hoặc "file" mặc định
        return result;
    }
}
