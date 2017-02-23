package com.teamnougat.todolistapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamnougat.todolistapp.db.TaskDbHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeeklyViewActivity extends AppCompatActivity {

    private static final String TAG = "WeeklyViewActivity";
    private TaskDbHelper myHelper;
    private int mYear, mMonth, mDate, mDay, subd, mHour, mMinute;
    private String sDate, eDate, sMonth;
    private Date newDate;
    private SimpleDateFormat dateFormat;
    TextView[] myDay = new TextView[7];
    TextView[] myMonth = new TextView[3];
    LinearLayout timeMarker;
    ViewGroup.MarginLayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myHelper = new TaskDbHelper(this);
        final Calendar c = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        timeMarker = (LinearLayout) findViewById(R.id.currentTimeMarkerLinearLayout);
        myMonth[0] = (TextView)findViewById(R.id.currentMonthTextView1);
        myMonth[1] = (TextView)findViewById(R.id.currentMonthTextView2);
        myMonth[2] = (TextView)findViewById(R.id.currentMonthTextView3);
        myDay[0] = (TextView)findViewById(R.id.sundayDateTextView);
        myDay[1] = (TextView)findViewById(R.id.mondayDateTextView);
        myDay[2] = (TextView)findViewById(R.id.tuesdayDateTextView);
        myDay[3] = (TextView)findViewById(R.id.wednesdayDateTextView);
        myDay[4] = (TextView)findViewById(R.id.thursdayDateTextView);
        myDay[5] = (TextView)findViewById(R.id.fridayDateTextView);
        myDay[6] = (TextView)findViewById(R.id.saturdayDateTextView);

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDate = c.get(Calendar.DAY_OF_MONTH);
        mDay = c.get(Calendar.DAY_OF_WEEK);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mMinute = (mHour * 60) + mMinute - 1;

        params = (ViewGroup.MarginLayoutParams) timeMarker.getLayoutParams();
        float density = getResources().getDisplayMetrics().density;
        int px = (int)(mMinute * density);
        params.topMargin = px;
        timeMarker.setLayoutParams(params);

        switch(mMonth+1){
            case 1: sMonth = "JAN";   break;
            case 2: sMonth = "FEB";   break;
            case 3: sMonth = "MAR";   break;
            case 4: sMonth = "APR";   break;
            case 5: sMonth = "MAY";   break;
            case 6: sMonth = "JUN";   break;
            case 7: sMonth = "JUL";   break;
            case 8: sMonth = "AUG";   break;
            case 9: sMonth = "SEP";   break;
            case 10: sMonth = "OCT";   break;
            case 11: sMonth = "NOV";   break;
            case 12: sMonth = "DEC";   break;
        }

        for( int i = 0; i < 3; i++ )
            myMonth[i].setText( sMonth.charAt(i) + "" );

        switch (mDay)
        {
            case 1: subd = -0;  break;
            case 2: subd = -1;  break;
            case 3: subd = -2;  break;
            case 4: subd = -3;  break;
            case 5: subd = -4;  break;
            case 6: subd = -5;  break;
            case 7: subd = -6;  break;
        }

        c.add(Calendar.DAY_OF_YEAR, subd);
        newDate = c.getTime();
        sDate = dateFormat.format(newDate);
        myDay[0].setText( sDate.substring(8) );

        for( int i = 1; i < 7; i++ )
        {
            c.add(Calendar.DAY_OF_YEAR, 1);
            newDate = c.getTime();
            eDate = dateFormat.format(newDate);
            myDay[i].setText( eDate.substring(8) );
        }
    }
}
