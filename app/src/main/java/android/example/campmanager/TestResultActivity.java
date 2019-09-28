package android.example.campmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TestResultActivity extends AppCompatActivity implements ResultDialog.OnEditListener {

    private final int ADD_RESULT = 10;
    private final int EDIT_RESULT = 11;

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
                    intent.setAction("add");
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
        } else if(requestCode == EDIT_RESULT && resultCode == RESULT_OK) {
            Log.d("TestResultActivity", "EDIT_RESULT: OK");
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

    public void callResultData(final String id, final String name) {
        db.collection("students").document(id)
          .collection("daily").document(date.replaceAll("/",""))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult()!=null && task.getResult().exists()) {
                            String eng = task.getResult().getString("eng");
                            String math = task.getResult().getString("math");
                            String remarks = task.getResult().getString("remarks");
                            showResultDialog(new Result(id, name, eng, math, remarks));
                        } else {
                            showEmptyDialog(name);
                        }
                    }
                });
    }

    public void showResultDialog(Result result) {
        ResultDialog resultDialog = ResultDialog.newInstance(result);
        resultDialog.setCancelable(false);
        resultDialog.show(getSupportFragmentManager(), "dialog");
    }

    public void showEmptyDialog(String name) {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(name)
                .setMessage("저장된 데이터가 없습니다.")
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
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

    @Override
    public void onEdit(Result data) {
        if (data != null) {
            Log.d("TestResultActivity", "onEdit: true");
            Intent intent = new Intent(getApplicationContext(), AddResultActivity.class);
            intent.setAction("edit");
            intent.putExtra("students",studentList);
            intent.putExtra("date", date.replaceAll("/",""));
            intent.putExtra("data", data);
            startActivityForResult(intent, EDIT_RESULT);
        } else {
            Log.d("TestResultActivity", "onEdit: false");
        }
    }
}
