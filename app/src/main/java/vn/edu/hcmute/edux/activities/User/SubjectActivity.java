package vn.edu.hcmute.edux.activities.User;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import vn.edu.hcmute.edux.R;
import vn.edu.hcmute.edux.fragments.QuizFragment;
import vn.edu.hcmute.edux.fragments.DocumentFragment;

public class SubjectActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView subjectTitle;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();

        btnBack.setOnClickListener(v -> finish());

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Quiz" : "Tài liệu")
        ).attach();
    }

    private void init() {
        btnBack = findViewById(R.id.btn_Back);
        subjectTitle = findViewById(R.id.subject_name);
        tabLayout = findViewById(R.id.tab_subject);
        viewPager = findViewById(R.id.viewSubject);

        // Lấy dữ liệu từ Intent
        String nameSubject = getIntent().getStringExtra("name_subject");
        String classId = getIntent().getStringExtra("class_id");

        subjectTitle.setText(nameSubject != null ? nameSubject : "Không xác định");

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, classId);
        viewPager.setAdapter(adapter);
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private final String classId;

        public ViewPagerAdapter(@NonNull FragmentActivity fa, String classId) {
            super(fa);
            this.classId = classId;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("class_id", classId);

            Fragment fragment = (position == 0) ? new QuizFragment() : new DocumentFragment();
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
