package android.example.campmanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    FirebaseFirestore db;

    private RecyclerView studentListView;
    public StudentListAdapter adapter;
    private ArrayList<Student> studentList;

    LoadingDialog dialog;

    public ListFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        dialog = new LoadingDialog(getActivity());
        dialog.show();

        callStudentData();

        studentListView = v.findViewById(R.id.rv_student);
        adapter = new StudentListAdapter(getActivity(), studentList, Glide.with(getActivity()));
        studentListView.setAdapter(adapter);
        studentListView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fabAddStudent = v.findViewById(R.id.fab_add_student);
        fabAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddStudentActivity.class);
                getActivity().startActivityForResult(intent, MainActivity.ADD_STUDENT_CODE);
            }
        });
        if (MainActivity.user != null) { fabAddStudent.show(); }
        else { fabAddStudent.hide(); }

        return v;
    }

    void callStudentData() {
        // Call students data
        db = FirebaseFirestore.getInstance();
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
                            Toast.makeText(getActivity(),getString(R.string.data_receive_fail), Toast.LENGTH_SHORT).show();
                            Log.d(getClass().getName(), getString(R.string.data_receive_fail));
                        }
                        dialog.dismiss();
                    }
                });

        studentList = new ArrayList<>();
    }

}
