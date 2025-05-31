package vn.edu.hcmute.edux.views;

import android.content.Context;

import java.util.Date;

public interface iCreateQuizView {
    void showToast(String message);
    void finishActivity();
    String getQuizTitle();
    String getQuizDescription();
    Date getStartTime();
    Date getEndTime();
    Context getContext();
}
