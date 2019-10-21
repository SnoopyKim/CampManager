package android.example.campmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestResultActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ArrayList<Student> studentList;
    ResultListAdapter adapter;

    String date;

    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        setBackButton();

        date = getIntent().getStringExtra("date");
        ((TextView)findViewById(R.id.tv_result_date)).setText(date);
        // Call students data
        db = FirebaseFirestore.getInstance();
        dialog = new LoadingDialog(this);

        callStudentData();

        Switch switchFIlter = findViewById(R.id.btn_filter_student);
        TextView tvTeacherStudent = findViewById(R.id.tv_teacher_student);
        if (MainActivity.user != null) {
            switchFIlter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) { adapter.getFilter().filter(MainActivity.user.getDisplayName()); }
                    else { adapter.getFilter().filter(""); }
                }
            });
        } else {
            tvTeacherStudent.setVisibility(View.INVISIBLE);
            switchFIlter.setVisibility(View.INVISIBLE);
            switchFIlter.setEnabled(false);
        }

        RecyclerView studentView = findViewById(R.id.rv_student_grid);
        adapter = new ResultListAdapter(this, studentList, Glide.with(this));
        studentView.setAdapter(adapter);
        studentView.setLayoutManager(new GridLayoutManager(this, 4));
    }

    private void callStudentData() {
        dialog.show();

        studentList = new ArrayList<>();

        db.collection("students").orderBy("name")
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
                                String teacher = studentData.get("teacher").toString();
                                Student student = new Student(id, name, age, photo, teacher);

                                studentList.add(student);
                            }
                            adapter.getFilter().filter("");
                        } else {
                            Toast.makeText(getApplicationContext(),getString(R.string.data_receive_fail), Toast.LENGTH_SHORT).show();
                            Log.d(getClass().getName(), getString(R.string.data_receive_fail));
                        }
                        dialog.dismiss();
                    }
                });
    }

    public void callResultData(String id, final String name) {
        dialog.show();
        final String path = "students/" + id + "/daily/" + date.replaceAll("/","");
        db.document(path).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult()!=null && task.getResult().exists()) {
                            String volume = task.getResult().getString("volume");
                            String eng = task.getResult().getString("eng");
                            String math = task.getResult().getString("math");
                            String remarks = task.getResult().getString("remarks");

                            dialog.dismiss();
                            showResultDialog(new Result(path, name, volume, eng, math, remarks));
                        } else if (task.isSuccessful() && task.getResult()!=null && !task.getResult().exists()) {
                            createResultData(path, name);
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), getString(R.string.data_receive_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void createResultData(final String path, final String name) {
        Map<String, String> init = new HashMap<>();
        init.put("volume","");
        init.put("eng", "");
        init.put("math", "");
        init.put("remarks", "");
        db.document(path).set(init).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    dialog.dismiss();
                    showResultDialog(new Result(path, name, "", "", "", ""));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.create_result_data_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showResultDialog(Result result) {
        ResultDialog resultDialog = ResultDialog.newInstance(result);
        resultDialog.setCancelable(false);
        resultDialog.show(getSupportFragmentManager(), "dialog");
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
