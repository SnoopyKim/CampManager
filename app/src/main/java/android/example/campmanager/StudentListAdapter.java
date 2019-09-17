package android.example.campmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        String stName = student.getName();
        String stAge = student.getAge();

        holder.tvName.setText(stName);
        holder.tvAge.setText(stAge);
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
        }
    }
}
