package android.example.campmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    LoadingDialog dialog;

    TextView tvEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setBackButton();

        tvEmptyList = findViewById(R.id.tv_empty_list);

        RecyclerView rvRecords = findViewById(R.id.rv_records);
        recordList = new ArrayList<>();
        recordAdapter = new RecordAdapter(this, recordList);
        rvRecords.setAdapter(recordAdapter);
        rvRecords.setLayoutManager(new LinearLayoutManager(this));

        dialog = new LoadingDialog(this);

        setRecordData();
    }

    private void setRecordData() {
        dialog.show();
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

                    if (task.getResult().isEmpty()) {
                        tvEmptyList.setText("데이터가 아직 없습니다.");
                        tvEmptyList.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.data_receive_fail), Toast.LENGTH_LONG).show();
                    tvEmptyList.setText("데이터를 불러오는데 문제가 생겼습니다\n다시 시도해주세요.");
                    tvEmptyList.setVisibility(View.VISIBLE);
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
