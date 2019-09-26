package android.example.campmanager;


import android.content.Intent;
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
import java.util.Locale;

public class DailyFragment extends Fragment {

    FirebaseFirestore db;

    String selectedDate;

    public DailyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_daily, container, false);

        CalendarView calendarView = v.findViewById(R.id.cv_daily);
        calendarView.setMaxDate(Calendar.getInstance().getTimeInMillis());
        selectedDate = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA).format(calendarView.getDate());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = String.format(Locale.KOREA,"%d/%02d/%02d", year, month+1, dayOfMonth);
                try {
                    view.setDate(new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA).parse(selectedDate).getTime(), true, true);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnViewResult = v.findViewById(R.id.btn_view_result);
        btnViewResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TestResultActivity.class);
                intent.putExtra("date", selectedDate);
                startActivityForResult(intent, MainActivity.DAILY_RESULT_CODE);
            }
        });

        return v;
    }

}
