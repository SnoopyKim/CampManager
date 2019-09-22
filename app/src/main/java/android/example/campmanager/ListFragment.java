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
    private StudentListAdapter adapter;
    private ArrayList<Student> studentList;

    public ListFragment() {
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
                                String age = studentData.get("age").toString();
                                String photo = studentData.get("photo").toString();
                                Student student = new Student(id, name, age, photo);
                                studentList.add(student);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(),getString(R.string.data_receive_fail), Toast.LENGTH_SHORT).show();
                            Log.d(getClass().getName(), getString(R.string.data_receive_fail));
                        }
                    }
                });

        studentList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        studentListView = v.findViewById(R.id.rv_student);
        adapter = new StudentListAdapter(getContext(), studentList);
        studentListView.setAdapter(adapter);
        studentListView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fabAddStudent = v.findViewById(R.id.fab);
        fabAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddStudentActivity.class);
                getActivity().startActivityForResult(intent, MainActivity.ADD_STUDENT_CODE);
            }
        });

        return v;
    }

}
