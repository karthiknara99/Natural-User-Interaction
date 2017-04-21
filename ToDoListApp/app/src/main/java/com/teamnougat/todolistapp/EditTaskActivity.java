package com.teamnougat.todolistapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.teamnougat.todolistapp.db.TaskContract;
import com.teamnougat.todolistapp.db.TaskDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity implements View.OnClickListener, OnGesturePerformedListener{

    private static final String TAG = "EditTaskActivity";
    private TaskDbHelper myHelper;
    private Spinner textType;
    private EditText textTitle, textLocation;
    private TextView textDate, textTime;
    private String task_id, task_name, task_type, task_date, task_time, task_loc, cyear, cmonth, cdate, chour, cmin;

    private int mYear, mMonth, mDate, mDay, mHour, mMinute, newMonth;
    private String finalDate, sMonth, sDate, sHour, sMinute;
    private GestureLibrary gestLib;
    private final Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        task_id = extras.getString("TASK_ID");
        task_name = extras.getString("TASK_NAME");
        task_type = extras.getString("TASK_TYPE");
        task_date = extras.getString("TASK_DATE");
        task_time = extras.getString("TASK_TIME");
        task_loc = extras.getString("TASK_LOC");
        cyear = extras.getString("CYEAR");
        cmonth = extras.getString("CMONTH");
        cdate = extras.getString("CDATE");
        chour = extras.getString("CHOUR");
        cmin = extras.getString("CMIN");
        Log.d(TAG, task_id);

        GestureOverlayView gestureOverLay = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_edit_task, null);
        gestureOverLay.addView(inflate);
        gestureOverLay.addOnGesturePerformedListener(this);
        gestLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        gestureOverLay.setGestureColor(getResources().getColor(R.color.gesture_color));
        if(!gestLib.load())
            finish();
        setContentView(gestureOverLay);
        getViews();
        getTaskDetails();

        textDate.setOnClickListener(this);
        textTime.setOnClickListener(this);
    }

    private void getViews()
    {
        textTitle = (EditText) findViewById(R.id.edit_taskName);
        textType = (Spinner) findViewById(R.id.edit_taskType);
        textDate = (TextView) findViewById(R.id.edit_date);
        textTime = (TextView) findViewById(R.id.edit_time);
        textLocation = (EditText) findViewById(R.id.edit_location);
    }

    private void getTaskDetails() {
         myHelper = new TaskDbHelper(this);
        textTitle.setText(task_name);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.task_types, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        textType.setAdapter(adapter);
        if (!task_type.equals(null)) {
            int spinnerPosition = adapter.getPosition(task_type);
            textType.setSelection(spinnerPosition);
        }

        mYear = Integer.parseInt(cyear);
        mMonth = Integer.parseInt(cmonth)-1;
        newMonth = mMonth; newMonth++;
        mDate = Integer.parseInt(cdate);
        mDay = c.get(Calendar.DAY_OF_WEEK);
        mHour = Integer.parseInt(chour);
        mMinute = Integer.parseInt(cmin);
        sHour = ""; sMinute = "";
        sDate = ""; sMonth = "";
        if( mDate < 10 )
            sDate = "0";
        sDate += mDate;
        if( mMonth-1 < 10 )
            sMonth = "0";
        sMonth += newMonth;

        finalDate = mYear + "-" + sMonth + "-" + sDate;

        textDate.setText(task_date);
        textTime.setText(task_time);
        textLocation.setText(task_loc);
    }

    public String findMonth( int mMonth ){
        String sMonth = "";
        switch(mMonth){
            case 1: sMonth = "Jan";   break;
            case 2: sMonth = "Feb";   break;
            case 3: sMonth = "Mar";   break;
            case 4: sMonth = "Apr";   break;
            case 5: sMonth = "May";   break;
            case 6: sMonth = "Jun";   break;
            case 7: sMonth = "Jul";   break;
            case 8: sMonth = "Aug";   break;
            case 9: sMonth = "Sep";   break;
            case 10: sMonth = "Oct";   break;
            case 11: sMonth = "Nov";   break;
            case 12: sMonth = "Dec";   break;
        }
        return sMonth;
    }

    @Override
    public void onClick(View v) {
        if( v == textDate ) {
            DatePickerDialog d = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
                            Date date = new Date(year, monthOfYear, dayOfMonth-1);
                            String dayOfWeek = sdf.format(date);
                            mDate = dayOfMonth;
                            mMonth = monthOfYear;
                            newMonth = mMonth; newMonth++;
                            mYear = year;
                            sDate = ""; sMonth = "";
                            if( mDate < 10 )
                                sDate = "0";
                            sDate += mDate;
                            if( mMonth < 10 )
                                sMonth = "0";
                            sMonth += newMonth;
                            textDate.setText( dayOfWeek + ", " + findMonth(mMonth+1) + " " + sDate + ", " + mYear );
                            int setMon = mMonth+1;
                            if( setMon < 10 )
                                sMonth = "0";
                            sMonth += setMon;
                            finalDate = mYear + "-" + sMonth + "-" + sDate;
                        }
                    }, mYear, mMonth, mDate);
            d.show();
        }
        if( v == textTime ) {
            final Calendar c = Calendar.getInstance();
            TimePickerDialog t = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mHour = hourOfDay;
                            mMinute = minute;
                            sHour = ""; sMinute = "";
                            if( mHour < 10 )
                                sHour = "0";
                            sHour += "" + mHour;
                            if( mMinute < 10 )
                                sMinute = "0";
                            sMinute += "" + mMinute;
                            textTime.setText(sHour + ":" + sMinute);
                        }
                    }, mHour, mMinute, false);
            t.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        ArrayList<Prediction> predictions = gestLib.recognize(gesture);
        for (Prediction prediction : predictions)
        {
            if (prediction.score > 4.0 && prediction.name.toLowerCase().equals("tick")) {
                if( textTitle.getText().toString().isEmpty() ){
                    Toast.makeText(this, "Task Name Empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    updateDB();
                    Intent i = new Intent(getApplicationContext(), ViewTaskActivity.class);
                    i.putExtra("TASK_ID", task_id);
                    startActivityForResult(i, 1);
                    this.finish();
                }
            }
            else if (prediction.score > 5.0 && prediction.name.toLowerCase().equals("right_swipe"))
            {
                Intent i = new Intent(getApplicationContext(), ViewTaskActivity.class);
                i.putExtra("TASK_ID", task_id);
                startActivityForResult(i, 1);
                this.finish();
            }
        }
    }

    public void updateDB() {

        finalDate = finalDate + " " + textTime.getText().toString() + ":00";
        try
        {
            SQLiteDatabase db = myHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(TaskContract.TaskEntry.COL_TASK_TITLE, textTitle.getText().toString().trim());
            cv.put(TaskContract.TaskEntry.COL_TASK_TYPE, textType.getSelectedItem().toString());
            cv.put(TaskContract.TaskEntry.COL_TASK_DUEDATE, finalDate);
            cv.put(TaskContract.TaskEntry.COL_TASK_DUEDAY, textDate.getText().toString().trim().substring(0,3));
            cv.put(TaskContract.TaskEntry.COL_TASK_LOCATION, textLocation.getText().toString().trim());
            cv.put(TaskContract.TaskEntry.COL_TASK_KEY, 1);

            db.update(TaskContract.TaskEntry.TABLE, cv, TaskContract.TaskEntry.COL_TASK_ID + "=" +task_id, null);
            db.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage().toString());
        }
    }

}
