package android.example.campmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    public static final int STUDENT_DETAIL_CODE = 4;
    public static final int DAILY_RESULT_CODE = 5;

    FirebaseUser user;

    BottomNavigationView navView;

    Fragment recentFragment = null;
    FragmentTransaction ft;

    String mode, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Toast.makeText(getApplicationContext(), "선생님 로그인", Toast.LENGTH_SHORT).show();
            mode = "teachers";
            id = user.getUid();
        } else {
            Toast.makeText(getApplicationContext(), "부모님 로그인", Toast.LENGTH_SHORT).show();
            mode = "students";
            id = getIntent().getStringExtra("ID");
        }

        navView.setSelectedItemId(R.id.navigation_profile);
    }

    private void switchFragment(Fragment fragment, String tag) {
        Log.d("MainActivity", "switchFragment: " + tag);
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
                    recentFragment = ProfileFragment.newInstance(mode, id);
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
                Log.d("MainActivityResult", "ADD_STUDENT_CODE: OK");
                navView.setSelectedItemId(R.id.navigation_list);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("MainActivityResult", "ADD_STUDENT_CODE: CANCELED");
            }
        } else if (requestCode == STUDENT_DETAIL_CODE) {
            Log.d("MainActivityResult", "STUDENT_DETAIL: ");

        } else if (requestCode == DAILY_RESULT_CODE) {
            Log.d("MainActivityResult", "DAILY_RESULT: ");

        } else if (requestCode == AddStudentActivity.GET_IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            Log.d("MainActivityResult", "GET_IMAGE_CODE: OK");
            //Bitmap thumbnail = data.getParcelableExtra("data");
            Uri profileUri = data.getData();
            // Do work with photo saved at fullPhotoUri
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(mode).child(id);
            storageReference.putFile(profileUri)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) { throw task.getException(); }
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful() && task.getResult()!=null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.profile_upload_success), Toast.LENGTH_SHORT).show();
                        FirebaseFirestore.getInstance().collection(mode).document(id)
                                .update("photo", task.getResult().toString());
                        ((ProfileFragment)recentFragment).drawImage(task.getResult().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.profile_upload_failure), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
