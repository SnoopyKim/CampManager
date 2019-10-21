package android.example.campmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.StudentViewHolder> implements Filterable {

    private TestResultActivity activity;
    private LayoutInflater mInflater;
    private List<Student> mStudents;
    private List<Student> listFIlterResult;
    private RequestManager glideRequestManager;

    public ResultListAdapter(TestResultActivity activity, List<Student> studentList, RequestManager requestManager) {
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
        mStudents = studentList;
        listFIlterResult = studentList;
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
        Student student = listFIlterResult.get(position);

        holder.tvName.setText(student.getName());

        int current = Calendar.getInstance().get(Calendar.YEAR);
        int birthYear = Integer.parseInt(student.getAge().substring(0,4));
        holder.tvAge.setText(String.format("%d세", current-birthYear));

        glideRequestManager
            .load(student.getPhoto())
            .centerCrop()
            .placeholder(R.drawable.default_profile)
            .into(holder.ivImage);
    }

    /*
    public void reset() {
        listFIlterResult.clear();
        listFIlterResult.addAll(mStudents);
        notifyDataSetChanged();
    }*/

    /*
    // add filter method (if don't implements Filterable)
    public void filter(String teacher) {
        Log.d("ResultListAdapter", "filter: "+teacher);
        listFIlterResult.clear();
        for (Student student : mStudents) {
            Log.d("ResultListAdapter", "student.getTeacher(): "+student.getTeacher());
            if (student.getTeacher().equals(teacher)) {
                listFIlterResult.add(student);
                Log.d("ResultListAdapter", "listFIlterResult.size(): "+listFIlterResult.size());
            }
        }
        notifyDataSetChanged();
    }
    */

    @Override
    public int getItemCount() {
        return listFIlterResult.size();
    }

    // using Filter Object to filter
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String teacher = constraint.toString();
                if(teacher.isEmpty()) {
                    listFIlterResult = mStudents;
                } else {
                    ArrayList<Student> filteringList = new ArrayList<>();
                    for (Student student : mStudents) {
                        if (student.getTeacher().equals(teacher)) {
                            filteringList.add(student);
                        }
                    }
                    listFIlterResult = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listFIlterResult;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listFIlterResult = (ArrayList<Student>)results.values;
                notifyDataSetChanged();
            }
        };
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
            activity.callResultData(listFIlterResult.get(position).getId(), listFIlterResult.get(position).getName());
        }
    }

}
