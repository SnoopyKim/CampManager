package android.example.campmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REGISTER_TEACHER_CODE = 1;
    public static Uri defaultProfileUri;

    FirebaseAuth auth;
    FirebaseFirestore db;

    RelativeLayout rlLoginForm;
    EditText etStudentName, etBirth, etEmail, etPassword;
    Button btnSearch, btnLogin, btnChangeForm, btnRegister;

    boolean switch_login = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rlLoginForm = findViewById(R.id.rlLoginFormP);

        etStudentName = findViewById(R.id.etSearchStudent);
        etBirth = findViewById(R.id.etBirth);
        btnSearch = findViewById(R.id.btnSearchStudent);
        btnSearch.setOnClickListener(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        btnChangeForm = findViewById(R.id.btnChangeForm);
        btnChangeForm.setOnClickListener(this);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Auto-Login
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSearchStudent:
                String stStudentName = etStudentName.getText().toString();
                int birth = Integer.parseInt(etBirth.getText().toString());

                searchStudent(stStudentName, birth);
                break;
            case R.id.btnLogin:
                String stEmail = etEmail.getText().toString();
                String stPassword = etPassword.getText().toString();

                loginTeacher(stEmail, stPassword);
                break;
            case R.id.btnChangeForm:
                if (switch_login) {
                    setFormPVisible(View.GONE);
                    setFormTVisible(View.VISIBLE);

                    btnChangeForm.setText(R.string.login_parents);
                    switch_login = false;
                } else {
                    setFormPVisible(View.VISIBLE);
                    setFormTVisible(View.GONE);

                    btnChangeForm.setText(R.string.login_teacher);
                    switch_login = true;
                }
                break;
            case R.id.btnRegister:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REGISTER_TEACHER_CODE);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REGISTER_TEACHER_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "register success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "register fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void searchStudent(String name, int birth) {
        db.collection("students").whereEqualTo("name", name).whereEqualTo("birth", birth)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // 확인 팝업
                } else {
                    Toast.makeText(getApplicationContext(), "해당 학생을 찾을 수가 없습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loginTeacher(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                }
            }
        });
    }

    public void setFormPVisible(int flag) {
        etStudentName.setVisibility(flag);
        etBirth.setVisibility(flag);
        btnSearch.setVisibility(flag);
    }
    public void setFormTVisible(int flag) {
        etEmail.setVisibility(flag);
        etPassword.setVisibility(flag);
        btnLogin.setVisibility(flag);
    }

}
