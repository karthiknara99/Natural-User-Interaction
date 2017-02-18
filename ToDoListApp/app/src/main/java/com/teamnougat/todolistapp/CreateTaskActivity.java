package com.teamnougat.todolistapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import android.gesture.Gesture;
import android.gesture.Prediction;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener, OnGesturePerformedListener{

    private static final String TAG = "CreateTaskActivity";
    private TaskDbHelper myHelper;

    Spinner ctaskType;
    EditText ctaskName, clocation;
    TextView cdate, ctime;
    private int mYear, mMonth, mDate, mDay, mHour, mMinute;
    private String finalDate, sMonth, sDate, sHour, sMinute;
    private GestureLibrary gestLib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GestureOverlayView gestureOverLay = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_create_task, null);
        gestureOverLay.addView(inflate);
        gestureOverLay.addOnGesturePerformedListener(this);
        gestLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        gestureOverLay.setGestureColor(Color.TRANSPARENT);
        if(!gestLib.load())
            finish();
        setContentView(gestureOverLay);

        myHelper = new TaskDbHelper(this);

        Spinner taskType = (Spinner) findViewById(R.id.create_taskType);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.task_types, R.layout.spinner_item);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item);
        taskType.setAdapter(typeAdapter);

        ctaskName = (EditText)findViewById(R.id.create_taskName);
        ctaskType = (Spinner) findViewById(R.id.create_taskType);
        cdate = (TextView) findViewById(R.id.create_date);
        ctime = (TextView) findViewById(R.id.create_time);
        clocation = (EditText)findViewById(R.id.create_location);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDate = c.get(Calendar.DAY_OF_MONTH);
        mDay = c.get(Calendar.DAY_OF_WEEK);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        sHour = ""; sMinute = "";
        sDate = ""; sMonth = "";
        if( mDate < 10 )
            sDate = "0";
        sDate += mDate;
        if( mMonth < 10 )
            sMonth = "0";
        sMonth += mMonth;

        finalDate = mYear + "-" + sMonth + "-" + sDate;

        cdate.setText( findDay(mDay) + ", " + findMonth(mMonth) + " " + sDate + ", " + mYear );
        ctime.setText("23" + ":" + "59");

        cdate.setOnClickListener(this);
        ctime.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override   //Save
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_task:
                if( ctaskName.getText().toString().isEmpty() ){
                    Toast.makeText(this, "Task Name Empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    insertDb();
                    setResult(RESULT_OK, null);
                    finish();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String findDay( int mDay ){
        String sDay = "";
        switch(mDay){
            case 1: sDay = "Sun";   break;
            case 2: sDay = "Mon";   break;
            case 3: sDay = "Tue";   break;
            case 4: sDay = "Wed";   break;
            case 5: sDay = "Thu";   break;
            case 6: sDay = "Fri";   break;
            case 7: sDay = "Sat";   break;
            default: sDay = "No" + mDay;   break;
        }
        return sDay;
    }

    public String findMonth( int mMonth ){
        String sMonth = "";
        switch(mMonth){
            case 0: sMonth = "Jan";   break;
            case 1: sMonth = "Feb";   break;
            case 2: sMonth = "Mar";   break;
            case 3: sMonth = "Apr";   break;
            case 4: sMonth = "May";   break;
            case 5: sMonth = "Jun";   break;
            case 6: sMonth = "Jul";   break;
            case 7: sMonth = "Aug";   break;
            case 8: sMonth = "Sep";   break;
            case 9: sMonth = "Oct";   break;
            case 10: sMonth = "Nov";   break;
            case 11: sMonth = "Dec";   break;
        }
        return sMonth;
    }

    public void insertDb() {
        SQLiteDatabase db = myHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, ctaskName.getText().toString());
        values.put(TaskContract.TaskEntry.COL_TASK_TYPE, ctaskType.getSelectedItem().toString());
        values.put(TaskContract.TaskEntry.COL_TASK_DUEDATE, finalDate);
        values.put(TaskContract.TaskEntry.COL_TASK_DUEDAY, cdate.getText().toString().substring(0,3));
        values.put(TaskContract.TaskEntry.COL_TASK_DUETIME, ctime.getText().toString());
        if(!clocation.getText().toString().isEmpty())
            values.put(TaskContract.TaskEntry.COL_TASK_LOCATION, clocation.getText().toString());

        db.insert(TaskContract.TaskEntry.TABLE, null, values);
        Log.d(TAG, "Inserted");
        db.close();
    }

    @Override
    public void onClick(View v) {
        if( v == cdate ) {
            final Calendar c = Calendar.getInstance();

            DatePickerDialog d = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
                            Date date = new Date(year, monthOfYear, dayOfMonth-1);
                            String dayOfWeek = sdf.format(date);
                            mDate = dayOfMonth;
                            mMonth = monthOfYear;
                            mYear = year;
                            sDate = ""; sMonth = "";
                            if( mDate < 10 )
                                sDate = "0";
                            sDate += mDate;
                            if( mMonth < 10 )
                                sMonth = "0";
                            sMonth += mMonth;
                            finalDate = mYear + "-" + sMonth + "-" + sDate;
                            cdate.setText( dayOfWeek + ", " + findMonth(mMonth) + " " + sDate + ", " + mYear );
                        }
                    }, mYear, mMonth, mDate);
            d.show();
        }
        if( v == ctime ) {
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
                            ctime.setText(sHour + ":" + sMinute);
                        }
                    }, mHour, mMinute, false);
            t.show();
        }
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        ArrayList<Prediction> predictions = gestLib.recognize(gesture);
        for (Prediction prediction : predictions)
        {
            if (prediction.score > 4.0 && prediction.name.toLowerCase().equals("tick")) {
                if( ctaskName.getText().toString().isEmpty() ){
                    Toast.makeText(this, "Task Name Empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    insertDb();
                    setResult(RESULT_OK, null);
                    finish();
                }
            }
            else if (prediction.score > 4.0 && prediction.name.toLowerCase().equals("right_swipe"))
            {
                Toast.makeText(this, "Return", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }
}
