package com.teamnougat.todolistapp;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.Prediction;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;

import com.teamnougat.todolistapp.db.TaskDbHelper;
import com.teamnougat.todolistapp.db.TaskContract;

import java.util.ArrayList;

public class ViewTaskActivity extends AppCompatActivity implements OnGesturePerformedListener{

    private static final String TAG = "ViewTaskActivity";
    private TaskDbHelper myHelper;
    private GestureLibrary gestLib;
    String task_id, newDate;
    String setmonth, setdate, setyear, setHour, setMin;
    ActionBar ab;
    TextView textTitle;
    TextView textType;
    TextView textDate;
    TextView textTime;
    TextView textLocation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task_id = getIntent().getStringExtra("TASK_ID");
        Log.d(TAG, task_id);
        GestureOverlayView gestureOverLay = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_view_task, null);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        gestureOverLay.addView(inflate);
        gestureOverLay.addOnGesturePerformedListener(this);
        gestLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        gestureOverLay.setGestureColor(Color.TRANSPARENT);
        if(!gestLib.load())
            finish();
        setContentView(gestureOverLay);
        myHelper = new TaskDbHelper(this);
        getTaskDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override   //Done
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_edit_task:



                Intent i = new Intent(getApplicationContext(), EditTaskActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TASK_ID", task_id);
                extras.putString("TASK_NAME", textTitle.getText().toString());
                extras.putString("TASK_TYPE", textType.getText().toString());
                extras.putString("TASK_DATE", textDate.getText().toString());
                extras.putString("CYEAR", setyear);
                extras.putString("CMONTH", setmonth);
                extras.putString("CDATE", setdate);
                extras.putString("TASK_TIME", textTime.getText().toString());
                extras.putString("CHOUR", setHour);
                extras.putString("CMIN", setMin);
                extras.putString("TASK_LOC", textLocation.getText().toString());
                i.putExtras(extras);
                startActivityForResult(i, 1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getTaskDetails() {
        SQLiteDatabase db = myHelper.getReadableDatabase();

        String selectQuery = "SELECT " + TaskContract.TaskEntry.COL_TASK_TITLE + ", "
                + TaskContract.TaskEntry.COL_TASK_TYPE + ", "
                + TaskContract.TaskEntry.COL_TASK_DUEDATE + ", "
                + TaskContract.TaskEntry.COL_TASK_DUEDAY + ", "
                + TaskContract.TaskEntry.COL_TASK_LOCATION
                + " FROM " + TaskContract.TaskEntry.TABLE
                + " WHERE " + TaskContract.TaskEntry.COL_TASK_ID + "=" +task_id +";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        textTitle = (TextView)findViewById(R.id.txv_TaskName);
        textType = (TextView)findViewById(R.id.txv_TaskType);
        textDate = (TextView)findViewById(R.id.txv_DueDate);
        textTime = (TextView)findViewById(R.id.txv_DueTime);
        textLocation = (TextView)findViewById(R.id.txv_Location);

        if (cursor.moveToFirst()) {
            textTitle.setText(cursor.getString(0));
            //if( cursor.getString(0) !=null )
            //    ab.setTitle(cursor.getString(0));
            if( cursor.getString(1) != null )
            {
                if( cursor.getString(1).equalsIgnoreCase("personal") )
                {
                    Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_action_home );
                    textType.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                }
                else if( cursor.getString(1).equalsIgnoreCase("work") )
                {
                    Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_action_work );
                    textType.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                }
                else
                {
                    Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_action_other);
                    textType.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                }
            }
            textType.setText(cursor.getString(1));
            String[] input = cursor.getString(2).split(" ");
            textTime.setText(input[1].substring(0,5));
            String[] tmpTime = input[1].split(":");
            setHour = tmpTime[0];
            setMin = tmpTime[1];
            input = input[0].split("-");
            setmonth = input[1];
            setdate = input[2];
            setyear = input[0];
            switch(input[1])
            {
                case "01": input[1] = "Jan";   break;
                case "02": input[1] = "Feb";   break;
                case "03": input[1] = "Mar";   break;
                case "04": input[1] = "Apr";   break;
                case "05": input[1] = "May";   break;
                case "06": input[1] = "Jun";   break;
                case "07": input[1] = "Jul";   break;
                case "08": input[1] = "Aug";   break;
                case "09": input[1] = "Sep";   break;
                case "10": input[1] = "Oct";   break;
                case "11": input[1] = "Nov";   break;
                case "12": input[1] = "Dec";   break;
            }
            newDate = cursor.getString(3) + ", " + input[1] + " " + input[2] + ", " + input[0];
            textDate.setText(newDate);
            if( cursor.getString(4) == null )
            {
                textLocation.setText("No Location");
                textLocation.setTextColor(Color.parseColor("#A9A9A9"));
            }
            else
                textLocation.setText(cursor.getString(4));
        }
        else{
            Toast.makeText(this, "Task Not Found", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    public void updateDb() {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        String updateQuery = "UPDATE " + TaskContract.TaskEntry.TABLE + " SET " +
                TaskContract.TaskEntry.COL_TASK_KEY + "=0 WHERE " + TaskContract.TaskEntry.COL_TASK_ID + "=" +task_id +";";
        db.execSQL(updateQuery);
        db.close();
    }

    public void deleteDb() {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        String updateQuery = "DELETE FROM " + TaskContract.TaskEntry.TABLE + " WHERE " +
                TaskContract.TaskEntry.COL_TASK_ID + "=" +task_id +";";
        db.execSQL(updateQuery);
        db.close();
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        ArrayList<Prediction> predictions = gestLib.recognize(gesture);
        for (Prediction prediction : predictions)
        {
            if (prediction.score > 4.0 && prediction.name.toLowerCase().equals("tick")) {
                //Toast.makeText(this, prediction.name + " - score:" + prediction.score, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Task Completed!", Toast.LENGTH_SHORT).show();
                updateDb();
                setResult(RESULT_CANCELED, null);
                finish();
            }
            else if (prediction.score > 2.0 && prediction.name.toLowerCase().equals("alpha")) {
                //Toast.makeText(this, prediction.name + " - score:" + prediction.score, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Task Deleted!", Toast.LENGTH_SHORT).show();
                deleteDb();
                setResult(RESULT_CANCELED, null);
                finish();
            }
            else if (prediction.score > 5.0 && prediction.name.toLowerCase().equals("right_swipe"))
            {
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }
}
