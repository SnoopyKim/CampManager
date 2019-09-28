package android.example.campmanager;

import android.app.AlertDialog;
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

public class ResultDialog extends DialogFragment implements View.OnClickListener {

    Result data;

    public static ResultDialog newInstance(Result data) {
        ResultDialog dialog = new ResultDialog();

        Bundle args = new Bundle();
        args.putSerializable("data", data);
        dialog.setArguments(args);

        return dialog;
    }

    public static interface OnEditListener {
        public abstract void onEdit(Result data);
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_result, null);

        data = (Result)getArguments().getSerializable("data");
        builder.setTitle(data.getName());

        TextView tvEngResult = v.findViewById(R.id.tv_eng_result);
        tvEngResult.setText(data.getEng());

        TextView tvMathResult = v.findViewById(R.id.tv_math_result);
        tvMathResult.setText(data.getMath());

        TextView tvRemarks = v.findViewById(R.id.tv_remarks);
        tvRemarks.setText(String.format("오늘의 %s", data.getName().substring(1)));

        TextView tvRemarksResult = v.findViewById(R.id.tv_remarks_result);
        tvRemarksResult.setText(data.getRemarks());

        Button btnEditResult = v.findViewById(R.id.btn_edit_result);
        btnEditResult.setOnClickListener(this);
        Button btnConfirm = v.findViewById(R.id.btn_confirm_result);
        btnConfirm.setOnClickListener(this);

        builder.setView(v);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_edit_result:
                editListener.onEdit(data);
                dismiss(); break;
            case R.id.btn_confirm_result:
                editListener.onEdit(null);
                dismiss(); break;
        }
    }

}
