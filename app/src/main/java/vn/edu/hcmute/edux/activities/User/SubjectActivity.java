package vn.edu.hcmute.edux.activities.User;

import android.os.Bundle;
import android.view.WindowManager;
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

        btnBack = findViewById(R.id.btn_Back);
        subjectTitle = findViewById(R.id.subject_name);
        tabLayout = findViewById(R.id.tab_subject);
        viewPager = findViewById(R.id.viewSubject);

        // Nhận className từ Intent
        String nameSubject = getIntent().getStringExtra("name_subject");
        String classId = getIntent().getStringExtra("class_id"); // Nếu cần sử dụng classId
        subjectTitle.setText(nameSubject != null ? nameSubject : "Không xác định");

        btnBack.setOnClickListener(v -> finish());

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Quiz");
                    else tab.setText("Tài liệu");
                }).attach();
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return new QuizFragment();
            else return new DocumentFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    public static class QuizFragment extends Fragment {
        public QuizFragment() {
            super(R.layout.fragment_quiz);
        }
    }

    public static class DocumentFragment extends Fragment {
        public DocumentFragment() {
            super(R.layout.fragment_document);
        }
    }
}