package android.example.campmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResultDialog extends DialogFragment {

    private Result data;

    private EditText etVolume, etEngResult, etMathResult, etRemarksResult;

    public static ResultDialog newInstance(Result data) {
        ResultDialog dialog = new ResultDialog();

        Bundle args = new Bundle();
        args.putSerializable("data", data);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ResultDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_result, container, false);

        data = (Result)getArguments().getSerializable("data");

        initView(v);

        return v;
    }

    private void initView(View v) {
        TextView tvStudentName = v.findViewById(R.id.tv_student_name);
        tvStudentName.setText(data.getName());

        etVolume = v.findViewById(R.id.et_volume);
        etVolume.setText(data.getVolume());

        etEngResult = v.findViewById(R.id.et_eng_result);
        etEngResult.setText(data.getEng());

        etMathResult = v.findViewById(R.id.et_math_result);
        etMathResult.setText(data.getMath());

        TextView tvRemarks = v.findViewById(R.id.tv_remarks);
        tvRemarks.setText(String.format("오늘의 %s", data.getName().substring(1)));

        etRemarksResult = v.findViewById(R.id.et_remarks_result);
        etRemarksResult.setText(data.getRemarks());

        Button btnConfirm = v.findViewById(R.id.btn_confirm_result);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    editResultData();
                } else {
                    dismiss();
                }
            }
        });

        // 학생 or 부모 사용자일 경우
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            etVolume.setEnabled(false);
            etEngResult.setEnabled(false);
            etMathResult.setEnabled(false);
            etRemarksResult.setEnabled(false);
        }
    }

    private void editResultData() {
        Map<String, String> result = new HashMap<>();
        result.put("volume", etVolume.getText().toString());
        result.put("eng", etEngResult.getText().toString());
        result.put("math", etMathResult.getText().toString());
        result.put("remarks", etRemarksResult.getText().toString());

        FirebaseFirestore.getInstance().document(data.getPath())
                .set(result).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(),data.getName()+"의 데일리 결과 추가 완료!",Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getActivity(),"업로드 실패",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
