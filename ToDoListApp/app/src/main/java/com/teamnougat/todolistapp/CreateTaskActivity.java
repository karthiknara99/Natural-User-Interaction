package com.teamnougat.todolistapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
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
import java.util.Calendar;
import java.util.Date;

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "CreateTaskActivity";
    private TaskDbHelper myHelper;

    Button csubmit;
    Spinner ctaskType;
    EditText ctaskName, clocation;
    TextView cdate, ctime;
    private int mYear, mMonth, mDate, mDay, mHour, mMinute;
    private String sDay, sMonth, sHour, sMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

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
        csubmit = (Button)findViewById(R.id.create_submit);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDate = c.get(Calendar.DAY_OF_MONTH);
        mDay = c.get(Calendar.DAY_OF_WEEK);
        sDay = findDay(mDay);
        sMonth = findMonth(mMonth);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        sHour = "";
        sMinute = "";

        cdate.setText( sDay + ", " + sMonth + " " + mDate + ", " + mYear );
        ctime.setText("23" + ":" + "59");

        cdate.setOnClickListener(this);
        ctime.setOnClickListener(this);
        csubmit.setOnClickListener(this);
    }

    public String findDay( int mDay ){
        String sDay = "";
        switch(mDay){
            case 0: sDay = "Sun";   break;
            case 1: sDay = "Mon";   break;
            case 2: sDay = "Tue";   break;
            case 3: sDay = "Wed";   break;
            case 4: sDay = "Thu";   break;
            case 5: sDay = "Fri";   break;
            case 6: sDay = "Sat";   break;
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
        values.put(TaskContract.TaskEntry.COL_TASK_DUEDATE, cdate.getText().toString());
        values.put(TaskContract.TaskEntry.COL_TASK_DUETIME, ctime.getText().toString());
        if(!clocation.getText().toString().isEmpty())
            values.put(TaskContract.TaskEntry.COL_TASK_LOCATION, clocation.getText().toString());

        db.insert(TaskContract.TaskEntry.TABLE, null, values);
        Log.d(TAG, "Inserted");
        db.close();
    }

    @Override
    public void onClick(View v) {
        if( v == csubmit ){
            if( ctaskName.getText().toString().isEmpty() ){
                Toast.makeText(this, "Task Name Empty", Toast.LENGTH_SHORT).show();
            }
            else{
                insertDb();
                setResult(RESULT_OK, null);
                finish();
            }
        }
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
                            sMonth = findMonth(monthOfYear);
                            mYear = year;
                            cdate.setText( dayOfWeek + ", " + sMonth + " " + mDate + ", " + mYear );
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
}
