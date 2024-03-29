package android.example.campmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class StudentActivity extends AppCompatActivity {

    FirebaseFirestore db;

    ImageView ivStudentProfile;
    TextView tvStudentName, tvStudentBirth;
    EditText etStudentTeacher;

    Student studentData;

    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        setBackButton();

        db = FirebaseFirestore.getInstance();

        dialog = new LoadingDialog(this);

        studentData = (Student)getIntent().getSerializableExtra("data");

        tvStudentName = findViewById(R.id.tv_student_name);
        tvStudentName.setText(studentData.getName());

        tvStudentBirth = findViewById(R.id.tv_student_birth);
        tvStudentBirth.setText(studentData.getAge());

        etStudentTeacher = findViewById(R.id.et_student_teacher);
        etStudentTeacher.setText(studentData.getTeacher());

        ivStudentProfile = findViewById(R.id.iv_student_image);
        Glide
                .with(getApplicationContext())
                .load(studentData.getPhoto())
                .centerCrop()
                .placeholder(R.drawable.default_profile)
                .into(ivStudentProfile);

        Button btnChageProfile = findViewById(R.id.btn_change_profile);
        btnChageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(intent, "Select an Image"), AddStudentActivity.GET_IMAGE_CODE);
                }
            }
        });

        Button btnConfirmTeacher = findViewById(R.id.btn_confirm_teacher);
        btnConfirmTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                confirmTeacherChanged();
            }
        });

        Button btnShowResult = findViewById(R.id.btn_show_result);
        btnShowResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, RecordActivity.class);
                intent.putExtra("ID", studentData.getId());
                startActivity(intent);
            }
        });

        if (MainActivity.user == null) {
            btnChageProfile.setVisibility(View.GONE);
            etStudentTeacher.setEnabled(false);
            btnConfirmTeacher.setVisibility(View.INVISIBLE);
            btnShowResult.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddStudentActivity.GET_IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            dialog.show();
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference("students").child(studentData.getId());
            storageReference.putFile(data.getData())
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
                        db.collection("students").document(studentData.getId())
                                .update("photo", task.getResult().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.profile_upload_failure), Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });

            Glide
                    .with(getApplicationContext())
                    .load(data.getData())
                    .centerCrop()
                    .placeholder(R.drawable.default_profile)
                    .into(ivStudentProfile);
        }
    }

    private void confirmTeacherChanged() {
        final String stTeacher = etStudentTeacher.getText().toString();
        db.collection("teachers").whereEqualTo("name", stTeacher)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "변경 성공!", Toast.LENGTH_SHORT).show();
                    db.collection("students").document(studentData.getId()).update("teacher", stTeacher);
                } else if (task.getResult() != null && task.getResult().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "해당 이름의 선생님이 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "오류가 생겼습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
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
