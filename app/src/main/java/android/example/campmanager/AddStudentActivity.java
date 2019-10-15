package android.example.campmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {

    public static final int GET_IMAGE_CODE = 3;

    private FirebaseFirestore db;
    private StorageReference studentRef;

    EditText etStudentName, etStudentBirth;

    Uri profileUri = null;

    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        setBackButton();

        db = FirebaseFirestore.getInstance();
        studentRef = FirebaseStorage.getInstance().getReference();

        dialog = new LoadingDialog(this);

        etStudentName = findViewById(R.id.et_student_name);
        etStudentBirth = findViewById(R.id.et_student_birth);

        Button btnAddProfile = findViewById(R.id.btn_add_profile);
        btnAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        Button btnAddStudent = findViewById(R.id.btn_add_student);
        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stName = etStudentName.getText().toString();
                String stBirth = etStudentBirth.getText().toString();

                if(stName.equals("") || stBirth.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.check_inputs), Toast.LENGTH_SHORT).show();
                } else {
                    dialog.show();
                    addStudent(stName, stBirth);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            //Bitmap thumbnail = data.getParcelableExtra("data");
            profileUri = data.getData();
            // Do work with photo saved at fullPhotoUri
            ImageView ivProfile = findViewById(R.id.iv_student_image);
            Glide
                .with(this)
                .load(profileUri)
                .centerCrop()
                .placeholder(R.drawable.default_profile)
                .into(ivProfile);
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, GET_IMAGE_CODE);
        }
    }

    private void addStudent(String name, String birth) {
        Map<String, String> student = new HashMap<>();
        student.put("name", name);
        student.put("birth", birth);

        db.collection("students")
                .add(student)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull final Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            if (profileUri != null) {
                                uploadStudentProfileImage(studentRef.child("students").child(task.getResult().getId()), task.getResult());
                            } else {
                                uploadStudentDefaultImage(studentRef, task.getResult());
                            }
                        } else {
                            dialog.dismiss();
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    }
                });
    }

    private void uploadStudentProfileImage(final StorageReference sRef, final DocumentReference dRef) {
        sRef.putFile(profileUri)
            .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) { throw task.getException(); }
                    return sRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful() && task.getResult()!=null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.profile_upload_success), Toast.LENGTH_SHORT).show();
                        dRef.update("photo", task.getResult().toString());
                        setResult(RESULT_OK);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.profile_upload_failure), Toast.LENGTH_SHORT).show();
                        dRef.update("photo", "");
                        setResult(RESULT_CANCELED);
                    }
                    dialog.dismiss();
                    finish();
                }
            });
    }
    private void uploadStudentDefaultImage(final StorageReference sRef, final DocumentReference dRef) {
        sRef.child("default_profile.png").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful() && task.getResult()!=null) {
                    dRef.update("photo", task.getResult().toString());
                    setResult(RESULT_OK);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.profile_upload_failure), Toast.LENGTH_SHORT).show();
                    dRef.update("photo", "");
                    setResult(RESULT_CANCELED);
                }
                dialog.dismiss();
                finish();
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
