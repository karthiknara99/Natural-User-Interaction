package com.karthiknara99.todolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.karthiknara99.todolist.db.TaskContract;
import com.karthiknara99.todolist.db.TaskDbHelper;

import java.util.Calendar;
import java.util.Date;

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "CreateTaskActivity";
    private TaskDbHelper myHelper;

    Button btnDatePicker, btnTimePicker, btnSubmit;
    Spinner spTaskType;
    EditText txtDate, txtTime, txtTitle, txtLocation;
    private int mYear, mMonth, mDay, mHour, mMinute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        myHelper = new TaskDbHelper(this);

        btnDatePicker=(Button)findViewById(R.id.btnDatePick);
        btnTimePicker=(Button)findViewById(R.id.btnTimePick);
        txtDate=(EditText)findViewById(R.id.txt_DueDate);
        txtTime=(EditText)findViewById(R.id.txt_DueTime);
        txtTitle=(EditText)findViewById(R.id.txt_taskName);
        txtLocation=(EditText)findViewById(R.id.txt_Location);
        btnSubmit=(Button)findViewById(R.id.btn_Submit);
        spTaskType = (Spinner) findViewById(R.id.sp_taskTypes);


        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        txtDate.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);
        txtTime.setText("00" + ":" + "00");

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    public void insertDb() {
        SQLiteDatabase db = myHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, txtTitle.getText().toString());
        values.put(TaskContract.TaskEntry.COL_TASK_TYPE, spTaskType.getSelectedItem().toString());
        values.put(TaskContract.TaskEntry.COL_TASK_DUEDATE, txtDate.getText().toString());
        values.put(TaskContract.TaskEntry.COL_TASK_DUETIME, txtTime.getText().toString());
        if(!txtLocation.getText().toString().isEmpty())
            values.put(TaskContract.TaskEntry.COL_TASK_LOCATION, txtLocation.getText().toString());

        db.insert(TaskContract.TaskEntry.TABLE, null, values);
        Log.d(TAG, "Inserted");
        db.close();
    }

    @Override
    public void onClick(View v) {
        if(v == btnSubmit){
            if(txtTitle.getText().toString().isEmpty()){
                Toast.makeText(this, "Task Name Empty", Toast.LENGTH_SHORT).show();
            }
            else{
                insertDb();
                setResult(RESULT_OK, null);
                finish();
            }
        }
        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
}
