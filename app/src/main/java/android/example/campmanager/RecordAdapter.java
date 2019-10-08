package android.example.campmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private ArrayList<Record> records;
    private Context context;

    RecordAdapter(Context context, ArrayList<Record> records) {
        this.context = context;
        this.records = records;
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvDate, tvVolume, tvEng, tvMath, tvRemarks;

        RecordViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_record_date);
            tvVolume = itemView.findViewById(R.id.tv_record_volume);
            tvEng = itemView.findViewById(R.id.tv_record_eng);
            tvMath = itemView.findViewById(R.id.tv_record_math);
            tvRemarks = itemView.findViewById(R.id.tv_record_remarks);
            itemView.setOnClickListener(this);
        }

        void bindData(Record data) {
            String year = data.getDate().substring(2,4);
            String month = data.getDate().substring(4,6);
            String day = data.getDate().substring(6);
            tvDate.setText(String.format("%s-%s-%s", year, month, day));
            tvVolume.setText(data.getVolume());
            tvEng.setText(data.getEng());
            tvMath.setText(data.getMath());
            tvRemarks.setText(data.getRemarks());
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
        }
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordViewHolder(LayoutInflater.from(context).inflate(R.layout.record_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = records.get(position);
        holder.bindData(record);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}
