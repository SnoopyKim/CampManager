package android.example.campmanager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_STUDENT_CODE = 2;

    FirebaseUser user;

    Fragment recentFragment = null;
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.navigation_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Toast.makeText(getApplicationContext(), "선생님 로그인", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "부모님 로그인", Toast.LENGTH_SHORT).show();
        }
    }

    private void switchFragment(Fragment fragment, String tag) {
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fm, fragment, tag);
        ft.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_list:
                    recentFragment = new ListFragment();
                    switchFragment(recentFragment, "list");
                    return true;
                case R.id.navigation_daily:
                    recentFragment = new DailyFragment();
                    switchFragment(recentFragment, "daily");
                    return true;
                case R.id.navigation_profile:
                    recentFragment = new ProfileFragment();
                    switchFragment(recentFragment, "profile");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_STUDENT_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("MainActivity", "onActivityResult: OK");
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("MainActivity", "onActivityResult: CANCELED");
            }
        }
    }
}
