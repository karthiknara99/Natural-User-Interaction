package com.teamnougat.todolistapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamnougat.todolistapp.db.TaskContract;
import com.teamnougat.todolistapp.db.TaskDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeeklyViewActivity extends AppCompatActivity implements View.OnClickListener, GestureOverlayView.OnGesturePerformedListener {

    private static final String TAG = "WeeklyViewActivity";
    private TaskDbHelper myHelper;
    private int mYear, mMonth, mDay, subd, row_cnt;
    private String sDate, eDate, sMonth;
    private Date newDate;
    private SimpleDateFormat dateFormat;
    private float density;
    private TextView[] myDay = new TextView[7];
    private View[] myMarker = new View[7];
    private LinearLayout[] myLayout = new LinearLayout[7];
    private TextView myMonth;
    private TextView[] msg;
    private ArrayList<String> taskId;
    private final Calendar c = Calendar.getInstance();
    private GestureLibrary gestLib;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        GestureOverlayView gestureOverLay = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_weekly_view, null);
        gestureOverLay.addView(inflate);
        gestureOverLay.addOnGesturePerformedListener(this);
        gestLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        gestureOverLay.setGestureColor(Color.TRANSPARENT);
        if(!gestLib.load())
            finish();
        setContentView(gestureOverLay);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getviews();
        getVars();

        myMonth.setText( sMonth + " " + mYear );
        myMarker[mDay-1].setVisibility(View.VISIBLE);

        subd = 1 - mDay;
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

    private void getviews()
    {
        myMonth = (TextView)findViewById(R.id.currentMonthTextView);
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
        myLayout[0] = (LinearLayout) findViewById(R.id.sundayLinearLayout);
        myLayout[1] = (LinearLayout) findViewById(R.id.mondayLinearLayout);
        myLayout[2] = (LinearLayout) findViewById(R.id.tuesdayLinearLayout);
        myLayout[3] = (LinearLayout) findViewById(R.id.wednesdayLinearLayout);
        myLayout[4] = (LinearLayout) findViewById(R.id.thursdayLinearLayout);
        myLayout[5] = (LinearLayout) findViewById(R.id.fridayLinearLayout);
        myLayout[6] = (LinearLayout) findViewById(R.id.saturdayLinearLayout);
    }

    private void getVars()
    {
        row_cnt = 0;
        density = getResources().getDisplayMetrics().density;
        myHelper = new TaskDbHelper(this);
        taskId = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        sMonth = getMonth(mMonth);
        mDay = c.get(Calendar.DAY_OF_WEEK);
    }

    private String getMonth( int mMonth )
    {
        String sMonth = "";
        switch( mMonth+1 )
        {
            case 1: sMonth = "JANUARY";   break;
            case 2: sMonth = "FEBRUARY";   break;
            case 3: sMonth = "MARCH";   break;
            case 4: sMonth = "APRIL";   break;
            case 5: sMonth = "MAY";   break;
            case 6: sMonth = "JUNE";   break;
            case 7: sMonth = "JULY";   break;
            case 8: sMonth = "AUGUST";   break;
            case 9: sMonth = "SEPTEMBER";   break;
            case 10: sMonth = "OCTOBER";   break;
            case 11: sMonth = "NOVEMBER";   break;
            case 12: sMonth = "DECEMBER";   break;
        }
        return sMonth;
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

    @Override
    public void onClick(View v) {
        int z;
        for( z = 0; v != msg[z]; z++ );
        String msg = "" + taskId.get(z);
        Intent i = new Intent(getApplicationContext(), ViewTaskActivity.class);
        i.putExtra("TASK_ID", msg);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_CANCELED ){
            Intent i = new Intent(getApplicationContext(), WeeklyViewActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        ArrayList<Prediction> predictions = gestLib.recognize(gesture);
        for (Prediction prediction : predictions)
        {
            if (prediction.score > 3.5 && prediction.name.toLowerCase().equals("c")) {
                //Toast.makeText(this, prediction.name + " - score:" + prediction.score, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), CreateTaskActivity.class);
                startActivityForResult(i, 1);
            }
            else if (prediction.score > 5.0 && prediction.name.toLowerCase().equals("right_swipe"))
            {
                //Toast.makeText(this, prediction.name + " - score:" + prediction.score, Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "Return", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }

    private void getTaskDetails() {
        SQLiteDatabase db = myHelper.getReadableDatabase();

        String selectQuery = "SELECT "
                + TaskContract.TaskEntry.COL_TASK_ID + ", "
                + TaskContract.TaskEntry.COL_TASK_TITLE + ", "
                + TaskContract.TaskEntry.COL_TASK_TYPE + ", "
                + TaskContract.TaskEntry.COL_TASK_DUEDATE + ", "
                + TaskContract.TaskEntry.COL_TASK_DUEDAY
                + " FROM " + TaskContract.TaskEntry.TABLE
                + " WHERE " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " BETWEEN "
                + "\"" + sDate + " 00:00:00\" AND \"" + eDate + " 23:59:59\""
                + " ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE
                + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);
        row_cnt = cursor.getCount();
        msg = new TextView[row_cnt];
        int col = 0;
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                taskId.add( cursor.getString(0) );
                msg[i] = new TextView(WeeklyViewActivity.this);
                msg[i].setText( cursor.getString(1) );
                msg[i].setTextSize(15);
                msg[i].setTypeface(Typeface.DEFAULT_BOLD);
                msg[i].setTextColor(getResources().getColor(R.color.white));
                msg[i].setGravity(Gravity.CENTER);
                int px = 0;
                if( cursor.getString(1).length() < 15 )
                    px = (int)(70 * density);
                else if( cursor.getString(1).length() < 23 )
                    px = (int)(100 * density);
                else
                    px = (int)(140 * density);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, px);
                params.setMargins(10,0,10,10);
                msg[i].setLayoutParams(params);
                msg[i].setPadding(0,10,0,10);
                GradientDrawable shape =  new GradientDrawable();
                shape.setCornerRadius( 10f );
                switch (col)
                {
                    case 0: shape.setColor(getResources().getColor(R.color.color1));   break;
                    case 1: shape.setColor(getResources().getColor(R.color.color2));   break;
                    case 2: shape.setColor(getResources().getColor(R.color.color3));   break;
                    case 3: shape.setColor(getResources().getColor(R.color.color4));   break;
                    case 4: shape.setColor(getResources().getColor(R.color.color5));   break;
                }
                col = ( col + 1 ) % 5;
                msg[i].setBackground(shape);

                int layoutId = 0;
                switch( cursor.getString(4) )
                {
                    case "Sun": layoutId = 0;   break;
                    case "Mon": layoutId = 1;   break;
                    case "Tue": layoutId = 2;   break;
                    case "Wed": layoutId = 3;   break;
                    case "Thu": layoutId = 4;   break;
                    case "Fri": layoutId = 5;   break;
                    case "Sat": layoutId = 6;   break;
                }
                myLayout[layoutId].addView(msg[i]);
                msg[i].setOnClickListener(this);
                i++;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }
}
