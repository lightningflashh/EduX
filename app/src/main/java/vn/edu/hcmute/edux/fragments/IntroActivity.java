package vn.edu.hcmute.edux.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmute.edux.Adapter.IntroViewPagerAdapter;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.model.ScreenItem;


public class IntroActivity extends AppCompatActivity {


    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;

    TabLayout tabIndication;

    int position = 0;

    ConstraintLayout btnNext,btnStart;

    Animation btnAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        if (restorePrefData()){
            Intent loginActivity = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }
        List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Kết Nối Dễ Dàng","Giúp giáo viên quản lý lớp học hiệu quả và học sinh theo dõi bài tập, điểm danh, nhận thông báo, và nộp bài trực tuyến.",R.drawable.img_2));
        mList.add(new ScreenItem("Xác Thực Face ID","Nhận diện khuôn mặt để điểm danh, làm bài tập và cập nhật thông tin, đảm bảo minh bạch và chính xác.",R.drawable.img_3));
        mList.add(new ScreenItem("Quản Lý Hiện Đại","Giáo viên có thể giao bài, chấm điểm, gửi thông báo và theo dõi tiến độ học sinh nhanh chóng, tiện lợi.",R.drawable.img_1));

        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);
        tabIndication = findViewById(R.id.tab_indicator);
        btnNext = findViewById(R.id.btn_next_question);
        btnStart = findViewById(R.id.btnStart);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.btn_anim_intro);

        //tab
        tabIndication.setupWithViewPager(screenPager);

        //btn next

        btnNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                position = screenPager.getCurrentItem();
                if(position < mList.size()){
                    position++;
                    screenPager.setCurrentItem(position);
                }

                if( position == mList.size()){
                    loadLastScreen();
                }
            }
        } );

        tabIndication.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition()== mList.size()-1){
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Chuyen qua trang main
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                savePrefsData();
                finish();

            }
        });

    }

    private boolean restorePrefData(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpenedBefore = pref.getBoolean("isIntroOpened",false);
        return isIntroActivityOpenedBefore;

    }
    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened",true);
        editor.commit();

    }
    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnStart.setVisibility(View.VISIBLE);
        tabIndication.setVisibility(View.INVISIBLE);

        //anim
        btnStart.setAnimation(btnAnim);

    }
}