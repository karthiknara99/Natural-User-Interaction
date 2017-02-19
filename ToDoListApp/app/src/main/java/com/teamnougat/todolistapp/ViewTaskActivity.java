package com.teamnougat.todolistapp;

import android.gesture.Gesture;
import android.gesture.Prediction;
import android.graphics.Color;
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
    String task_id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if( b == null )
        {
            Log.d(TAG, "No ID");
        }
        task_id = getIntent().getStringExtra("TASK_ID");
        Log.d(TAG, task_id);
        GestureOverlayView gestureOverLay = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_view_task, null);
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
            case R.id.action_view_task:
                Toast.makeText(this, "Task Completed!", Toast.LENGTH_SHORT).show();
                updateDb();
                setResult(RESULT_OK, null);
                finish();
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
                + TaskContract.TaskEntry.COL_TASK_DUETIME + ", "
                + TaskContract.TaskEntry.COL_TASK_LOCATION
                + " FROM " + TaskContract.TaskEntry.TABLE
                + " WHERE " + TaskContract.TaskEntry.COL_TASK_ID + "=" +task_id +";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        TextView textTitle = (TextView)findViewById(R.id.txv_TaskName);
        TextView textType = (TextView)findViewById(R.id.txv_TaskType);
        TextView textDate = (TextView)findViewById(R.id.txv_DueDate);
        TextView textTime = (TextView)findViewById(R.id.txv_DueTime);
        TextView textLocation = (TextView)findViewById(R.id.txv_Location);

        if (cursor.moveToFirst()) {
            textTitle.setText(cursor.getString(0));
            textType.setText(cursor.getString(1));
            textDate.setText(cursor.getString(2));
            textTime.setText(cursor.getString(3));
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
        Log.d(TAG, "Updated");
        db.close();
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        ArrayList<Prediction> predictions = gestLib.recognize(gesture);
        for (Prediction prediction : predictions)
        {
            if (prediction.score > 4.0 && prediction.name.toLowerCase().equals("tick")) {
                Toast.makeText(this, "Task Completed!", Toast.LENGTH_SHORT).show();
                updateDb();
                setResult(RESULT_OK, null);
                finish();
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
