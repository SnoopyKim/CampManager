package android.example.campmanager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ResultDialog extends DialogFragment {

    TextView tvEngResult, tvMathResult, tvRemarks, tvRemarksResult;

    public static ResultDialog newInstance(String name, String eng, String math, String remarks) {
        ResultDialog dialog = new ResultDialog();

        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("eng", eng);
        args.putString("math", math);
        args.putString("remarks", remarks);
        dialog.setArguments(args);

        return dialog;
    }

    public static interface OnEditListener {
        public abstract void onEdit(boolean on);
    }

    private OnEditListener editListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.editListener = (OnEditListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnCompleteListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_result, container, false);

        Bundle data = getArguments();

        getDialog().setTitle(data.getString("name"));

        tvEngResult = v.findViewById(R.id.tv_eng_result);
        tvEngResult.setText(data.getString("eng"));
        tvMathResult = v.findViewById(R.id.tv_math_result);
        tvMathResult.setText(data.getString("math"));
        tvRemarks = v.findViewById(R.id.tv_remarks);
        tvRemarks.setText(String.format("오늘의 %s이", data.getString("name").substring(1)));
        tvRemarksResult = v.findViewById(R.id.tv_remarks_result);
        tvRemarksResult.setText(data.getString("remarks"));

        Button btnEditResult = v.findViewById(R.id.btn_edit_result);
        btnEditResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editListener.onEdit(true);
            }
        });
        Button btnConfirm = v.findViewById(R.id.btn_confirm_result);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editListener.onEdit(false);
            }
        });

        return v;
    }
}
