package android.example.campmanager;


import android.content.Intent;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
    TextView tvUserName, tvUserType, tvUserEmail;

    Uri profileUri;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfile = v.findViewById(R.id.iv_user_image);
        tvUserName =  v.findViewById(R.id.tv_user_name);
        tvUserType = v.findViewById(R.id.tv_user_mode);
        tvUserEmail = v.findViewById(R.id.tv_user_email);

        final String mode = getArguments().getString("mode");
        String id = getArguments().getString("id");

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
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
            }
        });

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
                    if (mode.equals("teachers")) {
                        // when user is teacher
                        String stUserProfile = task.getResult().get("photo").toString();
                        String stUserName = task.getResult().get("name").toString();
                        String stUserEmail = task.getResult().get("email").toString();

                        drawImage(stUserProfile);
                        tvUserName.setText(stUserName);
                        tvUserType.setText(getString(R.string.teacher));
                        tvUserEmail.setText(stUserEmail);
                    } else {
                        // when user is parent or student.
                        Toast.makeText(getActivity(), getString(R.string.data_receive_fail), Toast.LENGTH_SHORT).show();
                    }
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

    public void drawImage(String url) {
        Log.d("ProfileChangeProcess", "drawImage: " + url);
        Glide
            .with(getContext())
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.default_profile)
            .into(ivProfile);
    }

}
