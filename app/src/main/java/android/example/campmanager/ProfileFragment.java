package android.example.campmanager;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    FirebaseFirestore db;

    ImageView ivProfile;
    TextView tvUserName, tvUserType, tvUserEmail, tvStudentBirth;

    public RequestManager glideRequestManager;

    public static ProfileFragment newInstance(final String mode, String id) {
        ProfileFragment pf = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("mode", mode);
        bundle.putString("id", id);

        pf.setArguments(bundle);
        return pf;
    }

    public ProfileFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glideRequestManager = Glide.with(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        final String mode = getArguments().getString("mode");
        final String id = getArguments().getString("id");

        ivProfile = v.findViewById(R.id.iv_user_image);
        tvUserName =  v.findViewById(R.id.tv_user_name);
        tvUserType = v.findViewById(R.id.tv_user_mode);
        tvUserEmail = v.findViewById(R.id.tv_user_email);
        tvStudentBirth = v.findViewById(R.id.tv_student_birth);

        setUserData(mode, id);

        Button btnChangeProfile = v.findViewById(R.id.btn_change_profile);
        btnChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Button btnLogout = v.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                }
                getActivity().finish();
            }
        });

        Button btnShowResult = v.findViewById(R.id.btn_show_result);
        btnShowResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecordActivity.class);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });

        if (mode.equals("teachers")) {
            LinearLayout llUserBirth = v.findViewById(R.id.ll_student_birth);
            llUserBirth.setVisibility(View.GONE);
            btnShowResult.setVisibility(View.GONE);
        } else {
            LinearLayout llUserEmail = v.findViewById(R.id.ll_user_email);
            llUserEmail.setVisibility(View.GONE);
        }

        return v;
    }

    void setUserData(final String mode, String id) {
        Log.d(getTag(), "setUserData: id: " + id);
        db = FirebaseFirestore.getInstance();
        db.collection(mode).document(id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult()!=null && task.getResult().exists()) {
                    String stUserProfile = task.getResult().get("photo").toString();
                    String stUserName = task.getResult().get("name").toString();

                    drawImage(stUserProfile);
                    tvUserName.setText(stUserName);
                    if (mode.equals("teachers")) {
                        // when user is teacher
                        String stUserEmail = task.getResult().get("email").toString();

                        tvUserType.setText(getString(R.string.teacher));
                        tvUserEmail.setText(stUserEmail);
                    } else {
                        // when user is parent or student.
                        String stUserBirth = task.getResult().get("birth").toString();

                        tvUserType.setText(getString(R.string.student));
                        tvStudentBirth.setText(stUserBirth);
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.data_receive_fail), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            getActivity().startActivityForResult(Intent.createChooser(intent, "Select an Image"), AddStudentActivity.GET_IMAGE_CODE);
        }
    }

    public void drawImage(final String url) {
        Log.d("ProfileChangeProcess", "drawImage: " + url);
        ivProfile.post(new Runnable() {
            @Override
            public void run() {
                glideRequestManager
                        .load(url)
                        .centerCrop()
                        .placeholder(R.drawable.default_profile)
                        .into(ivProfile);
            }
        });
    }

}
