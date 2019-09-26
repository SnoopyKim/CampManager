package android.example.campmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TestResultActivity extends AppCompatActivity {

    private final int ADD_RESULT = 10;

    FirebaseFirestore db;
    ArrayList<Student> studentList;
    ResultListAdapter adapter;

    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        setBackButton();

        date = getIntent().getStringExtra("date");
        ((TextView)findViewById(R.id.tv_result_date)).setText(date);
        // Call students data
        db = FirebaseFirestore.getInstance();
        callStudentData();

        RecyclerView studentView = findViewById(R.id.rv_student_grid);
        adapter = new ResultListAdapter(this, studentList, Glide.with(this));
        studentView.setAdapter(adapter);
        studentView.setLayoutManager(new GridLayoutManager(this, 4));

        FloatingActionButton fabAddResult = findViewById(R.id.fab_add_result);
        fabAddResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!studentList.isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), AddResultActivity.class);
                    intent.putExtra("students",studentList);
                    intent.putExtra("date", date.replaceAll("/",""));
                    startActivityForResult(intent, ADD_RESULT);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_RESULT && resultCode == RESULT_OK) {
            Log.d("TestResultActivity", "ADD_RESULT: OK");
        }
    }

    private void callStudentData() {
        studentList = new ArrayList<>();

        db.collection("students")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(getClass().getName(), getString(R.string.data_receive_success));
                            for (QueryDocumentSnapshot studentData : task.getResult()) {
                                String id = studentData.getId();
                                String name = studentData.get("name").toString();
                                String age = studentData.get("birth").toString();
                                String photo = studentData.get("photo").toString();
                                Student student = new Student(id, name, age, photo);

                                studentList.add(student);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(),getString(R.string.data_receive_fail), Toast.LENGTH_SHORT).show();
                            Log.d(getClass().getName(), getString(R.string.data_receive_fail));
                        }
                    }
                });
    }

    public void callDailyData(String id) {

    }

    public void runDialog() {

    }

    private void setBackButton() {
        ImageButton ibBack = findViewById(R.id.btn_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
