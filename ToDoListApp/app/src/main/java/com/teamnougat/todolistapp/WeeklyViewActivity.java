package com.teamnougat.todolistapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamnougat.todolistapp.db.TaskContract;
import com.teamnougat.todolistapp.db.TaskDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeeklyViewActivity extends AppCompatActivity {

    private static final String TAG = "WeeklyViewActivity";
    private TaskDbHelper myHelper;
    private int mMonth, mDay, subd, mHour, mMinute, locY;
    private String sDate, eDate, sMonth;
    private Date newDate;
    private SimpleDateFormat dateFormat;
    private float density;
    TextView[] myDay = new TextView[7];
    View[] myMarker = new View[7];
    RelativeLayout[] myLayout = new RelativeLayout[7];
    TextView[] myMonth = new TextView[3];
    LinearLayout timeMarker;
    ViewGroup.MarginLayoutParams params;
    ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        locY = 0;
        myHelper = new TaskDbHelper(this);
        final Calendar c = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        getviews();

        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_WEEK);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mMinute = (mHour * 60) + mMinute - 1;

        myMarker[mDay-1].setVisibility(View.VISIBLE);

        params = (ViewGroup.MarginLayoutParams) timeMarker.getLayoutParams();
        density = getResources().getDisplayMetrics().density;
        final int px = (int)(mMinute * density);
        params.topMargin = px;
        timeMarker.setLayoutParams(params);

        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.scrollTo(0, px-600);
            }
        });

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
        getTaskDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weekly_view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override   //Done
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getviews()
    {
        sv = (ScrollView) findViewById(R.id.calendarScrollView);
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
        myMarker[0] = (View)findViewById(R.id.sundayMarkerView);
        myMarker[1] = (View)findViewById(R.id.mondayMarkerView);
        myMarker[2] = (View)findViewById(R.id.tuesdayMarkerView);
        myMarker[3] = (View)findViewById(R.id.wednesdayMarkerView);
        myMarker[4] = (View)findViewById(R.id.thursdayMarkerView);
        myMarker[5] = (View)findViewById(R.id.fridayMarkerView);
        myMarker[6] = (View)findViewById(R.id.saturdayMarkerView);
        myLayout[0] = (RelativeLayout) findViewById(R.id.sundayRelativeLayout);
        myLayout[1] = (RelativeLayout) findViewById(R.id.mondayRelativeLayout);
        myLayout[2] = (RelativeLayout) findViewById(R.id.tuesdayRelativeLayout);
        myLayout[3] = (RelativeLayout) findViewById(R.id.wednesdayRelativeLayout);
        myLayout[4] = (RelativeLayout) findViewById(R.id.thursdayRelativeLayout);
        myLayout[5] = (RelativeLayout) findViewById(R.id.fridayRelativeLayout);
        myLayout[6] = (RelativeLayout) findViewById(R.id.saturdayRelativeLayout);
    }

    private void getTaskDetails() {
        SQLiteDatabase db = myHelper.getReadableDatabase();

        String selectQuery = "SELECT " + TaskContract.TaskEntry.COL_TASK_ID + ", "
                + TaskContract.TaskEntry.COL_TASK_TITLE + ", "
                + TaskContract.TaskEntry.COL_TASK_TYPE + ", "
                + TaskContract.TaskEntry.COL_TASK_DUEDAY + ", "
                + TaskContract.TaskEntry.COL_TASK_DUETIME
                + " FROM " + TaskContract.TaskEntry.TABLE
                + " WHERE " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " BETWEEN \"" + sDate +"\" AND \"" + eDate + "\";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TextView msg = new TextView(WeeklyViewActivity.this);
                msg.setBackgroundResource(R.color.medium_dark_gray);
                msg.setText( cursor.getString(1) );
                msg.setTextSize(15);
                msg.setTextColor(getResources().getColor(R.color.strong_blue));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(60*density));
                String[] input = cursor.getString(4).split(":");
                int px = Integer.parseInt(input[0])*60 + Integer.parseInt(input[1]);
                px = (int)( (px-60) * density );
                params.setMargins(0, px, 0, 0);
                msg.setLayoutParams(params);
                msg.setGravity(Gravity.CENTER);

                int layoutId = 0;
                switch( cursor.getString(3) )
                {
                    case "Sun":
                        layoutId = 0;
                        break;
                    case "Mon":
                        layoutId = 1;
                        break;
                    case "Tue":
                        layoutId = 2;
                        break;
                    case "Wed":
                        layoutId = 3;
                        break;
                    case "Thu":
                        layoutId = 4;
                        break;
                    case "Fri":
                        layoutId = 5;
                        break;
                    case "Sat":
                        layoutId = 6;
                        break;
                }

                myLayout[layoutId].addView(msg);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }
}
