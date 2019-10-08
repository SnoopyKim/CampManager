package android.example.campmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class RecordActivity extends AppCompatActivity {

    RecordAdapter recordAdapter;
    ArrayList<Record> recordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setBackButton();

        RecyclerView rvRecords = findViewById(R.id.rv_records);
        recordList = new ArrayList<>();
        recordAdapter = new RecordAdapter(this, recordList);
        rvRecords.setAdapter(recordAdapter);
        rvRecords.setLayoutManager(new LinearLayoutManager(this));

        setRecordData();
    }

    private void setRecordData() {
        FirebaseFirestore.getInstance().collection("students")
                .document(getIntent().getStringExtra("ID")).collection("daily")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot data : task.getResult().getDocuments()) {
                        String stDate = data.getId();
                        String stVolume = data.get("volume").toString();
                        String stEng = data.get("eng").toString();
                        String stMath = data.get("math").toString();
                        String stRemarks = data.get("remarks").toString();
                        recordList.add(new Record(stDate, stVolume, stEng, stMath, stRemarks));
                    }
                    Collections.reverse(recordList);
                    recordAdapter.notifyDataSetChanged();
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
