package android.example.campmanager;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.Calendar;
import java.util.List;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.StudentViewHolder> {

    private TestResultActivity activity;
    private LayoutInflater mInflater;
    private List<Student> mStudents;
    private RequestManager glideRequestManager;

    public ResultListAdapter(TestResultActivity activity, List<Student> studentList, RequestManager requestManager) {
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
        mStudents = studentList;
        glideRequestManager = requestManager;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View studentVIew = mInflater.inflate(R.layout.student_list_item_grid, parent, false);
        return new StudentViewHolder(studentVIew);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = mStudents.get(position);

        holder.tvName.setText(student.getName());

        int current = Calendar.getInstance().get(Calendar.YEAR);
        int birthYear = Integer.parseInt(student.getAge().substring(0,4));
        holder.tvAge.setText(current-birthYear + "세");

        glideRequestManager
            .load(student.getPhoto())
            .centerCrop()
            .placeholder(R.drawable.default_profile)
            .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return mStudents.size();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final SquareImageView ivImage;
        private final TextView tvName, tvAge;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.iv_student_image);
            tvName = itemView.findViewById(R.id.tv_student_name);
            tvAge = itemView.findViewById(R.id.tv_student_age);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            activity.callDailyData(mStudents.get(position).getId());
        }
    }

}
