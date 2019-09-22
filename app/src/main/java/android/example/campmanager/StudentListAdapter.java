package android.example.campmanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder> {

    private LayoutInflater mInflater;
    private List<Student> mStudents;

    public StudentListAdapter(Context context, List<Student> studentList) {
        mInflater = LayoutInflater.from(context);
        mStudents = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View studentVIew = mInflater.inflate(R.layout.student_list_item, parent, false);
        return new StudentViewHolder(studentVIew);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = mStudents.get(position);

        holder.tvName.setText(student.getName());

        int current = Calendar.getInstance().get(Calendar.YEAR);
        int birthYear = Integer.parseInt(student.getAge().substring(0,3));
        holder.tvAge.setText(current-birthYear + "세");

        Glide
            .with(mInflater.getContext())
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

        private final ImageView ivImage;
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
            Intent intent = new Intent(mInflater.getContext(), StudentActivity.class);
            intent.putExtra("ID", mStudents.get(position).getId());
            mInflater.getContext().startActivity(intent);
        }
    }
}
