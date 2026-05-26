package ashot.harutyunyan.smartstudy;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    TextView tvStreak;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStreak = findViewById(R.id.tvStreak);
        bottomNav = findViewById(R.id.bottomNav);

        loadFragment(new MainFragment());
        bottomNav.setSelectedItemId(R.id.nav_subjects);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_subjects) {
                loadFragment(new MainFragment());
                return true;
            } else if (id == R.id.nav_ai) {
                loadFragment(new AiTutorFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }
            return true;
        });

        updateStreak();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStreak();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

    }

    private void updateStreak() {
        SharedPreferences prefs = getSharedPreferences("progress", MODE_PRIVATE);
        int streak = prefs.getInt("streak_count", 0);
        tvStreak.setText(String.valueOf(streak));
    }
}