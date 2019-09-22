package android.example.campmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class StudentActivity extends AppCompatActivity {

    FirebaseFirestore db;

    ImageView ivStudentProfile;
    TextView tvStudentName, tvStudentBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        getSupportActionBar().setHomeButtonEnabled(true);

        db = FirebaseFirestore.getInstance();

        ivStudentProfile = findViewById(R.id.iv_student_image);
        tvStudentName = findViewById(R.id.tv_student_name);
        tvStudentBirth = findViewById(R.id.tv_student_birth);

        String id = getIntent().getStringExtra("ID");
        db.collection("students").document(id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult()!=null) {
                    tvStudentName.setText(task.getResult().get("name").toString());
                    tvStudentBirth.setText(task.getResult().get("birth").toString());
                    Glide
                        .with(getApplicationContext())
                        .load(task.getResult().get("photo"))
                        .centerCrop()
                        .placeholder(R.drawable.default_profile)
                        .into(ivStudentProfile);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.data_receive_fail), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
