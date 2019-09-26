package android.example.campmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddResultActivity extends AppCompatActivity {

    FirebaseFirestore db;

    ArrayList<Student> studentsList;

    EditText etName, etBirth, etEng, etMath, etRemarks;
    Button btnAddResult;

    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_result);
        setBackButton();

        studentsList = (ArrayList<Student>)getIntent().getSerializableExtra("students");
        date = getIntent().getStringExtra("date");

        etName = findViewById(R.id.et_student_name);
        etBirth = findViewById(R.id.et_student_birth);
        etEng = findViewById(R.id.et_eng_result);
        etMath = findViewById(R.id.et_math_result);
        etRemarks = findViewById(R.id.et_remarks);

        btnAddResult = findViewById(R.id.btn_add_result);
        btnAddResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = checkInputs(etName.getText().toString(), etBirth.getText().toString());
                if (id == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.search_student_fail), Toast.LENGTH_SHORT).show();
                } else {
                    addResult(id);
                }
            }
        });
    }

    private String checkInputs(String name, String birth) {
        for (Student student : studentsList) {
            if (student.getName().equals(name) && student.getAge().equals(birth)) {
                return student.getId();
            }
        }
        return null;
    }

    private void addResult(String id) {
        Map<String, Object> result = new HashMap<>();
        result.put("eng", etEng.getText());
        result.put("math", etMath.getText());
        result.put("remarks", etRemarks.getText());

        db.collection("students").document(id)
          .collection("daily").document(date)
                .set(result).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),etName.getText()+"의 데일리 결과 추가 완료!",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"업로드 실패",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
