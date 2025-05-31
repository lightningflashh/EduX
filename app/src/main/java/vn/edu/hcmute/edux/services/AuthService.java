package vn.edu.hcmute.edux.services;


import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.function.Consumer;

public class AuthService {

    private final FirebaseAuth auth;

    public AuthService() {
        this.auth = FirebaseAuth.getInstance();
    }

    public void sendPasswordResetEmail(String email, Runnable onSuccess, Consumer<Exception> onFailure) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                    } else {
                        if (onFailure != null) {
                            onFailure.accept(task.getException());
                        }
                    }
                });
    }
    public void changePassword(String currentPassword, String newPassword, Runnable onSuccess, Consumer<Exception> onFailure) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            if (onFailure != null) {
                onFailure.accept(new Exception("Người dùng chưa đăng nhập!"));
            }
            return;
        }
        // Reauthenticate người dùng với mật khẩu hiện tại
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        user.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    // Reauthentication thành công, tiến hành cập nhật mật khẩu mới
                    user.updatePassword(newPassword)
                            .addOnSuccessListener(unused -> {
                                if (onSuccess != null) {
                                    onSuccess.run();
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (onFailure != null) {
                                    onFailure.accept(e);
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    if (onFailure != null) {
                        onFailure.accept(e); // Lỗi reauthentication (mật khẩu hiện tại không đúng, v.v.)
                    }
                });
    }
}