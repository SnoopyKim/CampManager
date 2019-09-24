package android.example.campmanager;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyFragment extends Fragment {

    FirebaseFirestore db;

    public DailyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_daily, container, false);

        final CalendarView calendarView = v.findViewById(R.id.cv_daily);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = ""+year+"/"+(month+1)+"/" +dayOfMonth;
                try {
                    view.setDate(new SimpleDateFormat("yyyy/MM/dd").parse(selectedDate).getTime(), true, true);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(), selectedDate , Toast.LENGTH_SHORT).show();
            }
        });

        Button btnViewResult = v.findViewById(R.id.btn_view_result);
        btnViewResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(calendarView.getDate()));
                Toast.makeText(getContext(),calendar.get(Calendar.DATE)+"", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

}
