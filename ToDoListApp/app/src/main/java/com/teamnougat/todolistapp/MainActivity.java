package com.teamnougat.todolistapp;

import android.content.Context;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Gesture;
import android.gesture.Prediction;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;

import com.teamnougat.todolistapp.db.TaskContract;
import com.teamnougat.todolistapp.db.TaskDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnGesturePerformedListener {

    private static final String TAG = "MainActivity";
    private TaskDbHelper myHelper;
    private ListView myList;
    private NewArrayAdapter myAdapter;
    private GestureLibrary gestLib;
    private ArrayList<String> taskId;
    private final Calendar c = Calendar.getInstance();
    private Date newDate;
    private String sDate;
    private SimpleDateFormat dateFormat;
    private float scaleFactor;
    private FloatingActionButton myFab;
    GestureOverlayView gestureOverLay;
    ScaleGestureDetector mScaleDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureOverLay = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_main, null);
        gestureOverLay.addView(inflate);
        gestureOverLay.addOnGesturePerformedListener(this);
        gestLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        gestureOverLay.setGestureColor(getResources().getColor(R.color.gesture_color));
        if(!gestLib.load())
            finish();
        setContentView(gestureOverLay);

        taskId = new ArrayList<>();
        myHelper = new TaskDbHelper(this);
        myList = (ListView) findViewById(R.id.list_todo);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        c.add(Calendar.DAY_OF_YEAR, 0);
        newDate = c.getTime();
        sDate = dateFormat.format(newDate);

        String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE
                + " WHERE " + TaskContract.TaskEntry.COL_TASK_KEY + "=1"
                + " AND " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " >= \"" + sDate + "\""
                + " ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE + ";";
        updateUI(selectQuery);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int temp = (int)id;
                String msg = "" + taskId.get(temp);
                Intent i = new Intent(getApplicationContext(), ViewTaskActivity.class);
                i.putExtra("TASK_ID", msg);
                startActivityForResult(i, 1);
            }
        });

        mScaleDetector =  new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
        scaleFactor = 1.0f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override   //Insert
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_task:
                String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE
                        + " WHERE " + TaskContract.TaskEntry.COL_TASK_KEY + "=1"
                        + " AND " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " >= \"" + sDate + "\""
                        + " ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE + ";";
                updateUI(selectQuery);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_CANCELED || resultCode == RESULT_OK )
        {
            String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE
                    + " WHERE " + TaskContract.TaskEntry.COL_TASK_KEY + "=1"
                    + " AND " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " >= \"" + sDate + "\""
                    + " ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE + ";";
            updateUI(selectQuery);
        }
        else if( resultCode == RESULT_FIRST_USER )
        {
            String tempStartDate = data.getStringExtra("TASK_DATE") + " 00:00:00";
            String tempEndDate = data.getStringExtra("TASK_DATE") + " 23:59:59";
            String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE
                    + " WHERE " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " >= \"" + tempStartDate + "\""
                    + " AND " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " <= \"" + tempEndDate + "\""
                    + ";";
            updateUI(selectQuery);
        }
    }

    private void updateUI(String selectQuery) {
        ArrayList<Item> taskList = new ArrayList<>();
        taskId.clear();
        SQLiteDatabase db = myHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        String newDate = "";

        if (cursor.moveToFirst()) {
            do {
                taskId.add(cursor.getString(0));
                String[] input = cursor.getString(3).split(" ");
                input = input[0].split("-");
                switch (input[1]) {
                    case "01":  newDate = "Jan";    break;
                    case "02":  newDate = "Feb";    break;
                    case "03":  newDate = "Mar";    break;
                    case "04":  newDate = "Apr";    break;
                    case "05":  newDate = "May";    break;
                    case "06":  newDate = "Jun";    break;
                    case "07":  newDate = "Jul";    break;
                    case "08":  newDate = "Aug";    break;
                    case "09":  newDate = "Sep";    break;
                    case "10":  newDate = "Oct";    break;
                    case "11":  newDate = "Nov";    break;
                    case "12":  newDate = "Dec";    break;
                }
                newDate = cursor.getString(4) + ", " + newDate + " " + input[2];

                taskList.add(new Item(cursor.getString(1), cursor.getString(2), newDate));
            } while (cursor.moveToNext());
        }

        if (myAdapter == null) {
            myAdapter = new NewArrayAdapter(this,
                    R.layout.item_todo,
                    taskList);
            myList.setAdapter(myAdapter);
        } else {
            myAdapter.clear();
            myAdapter.addAll(taskList);
            myAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        ArrayList<Prediction> predictions = gestLib.recognize(gesture);
        for (Prediction prediction : predictions)
        {
            if (prediction.score > 3.5 && prediction.name.toLowerCase().equals("c")) {
                Intent i = new Intent(getApplicationContext(), CreateTaskActivity.class);
                startActivityForResult(i, 1);
            }
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            scaleFactor *= detector.getScaleFactor();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            scaleFactor = detector.getScaleFactor();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if( scaleFactor > 1 )
            {
                return;
            }
            else if( scaleFactor < 1 )
            {
                Intent i = new Intent(getApplicationContext(), WeeklyViewActivity.class);
                startActivityForResult(i, 1);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        mScaleDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

}
