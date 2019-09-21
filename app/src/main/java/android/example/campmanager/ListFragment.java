package android.example.campmanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ListFragment extends Fragment {

    FirebaseFirestore db;

    private RecyclerView studentListView;
    private StudentListAdapter adapter;
    private List<Student> studentList;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        studentListView = v.findViewById(R.id.rv_student);
        adapter = new StudentListAdapter(getContext(), studentList);
        studentListView.setAdapter(adapter);

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
