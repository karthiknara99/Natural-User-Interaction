package com.teamnougat.todolistapp;

import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Gesture;
import android.gesture.Prediction;
import android.graphics.Color;
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

import com.teamnougat.todolistapp.db.TaskContract;
import com.teamnougat.todolistapp.db.TaskDbHelper;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnGesturePerformedListener {

    private static final String TAG = "MainActivity";
    private TaskDbHelper myHelper;
    private ListView myList;
    private NewArrayAdapter myAdapter;
    private GestureLibrary gestLib;
    ArrayList<String> taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GestureOverlayView gestureOverLay = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_main, null);
        gestureOverLay.addView(inflate);
        gestureOverLay.addOnGesturePerformedListener(this);
        gestLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        gestureOverLay.setGestureColor(Color.TRANSPARENT);
        if(!gestLib.load())
            finish();
        setContentView(gestureOverLay);

        taskId = new ArrayList<>();
        myHelper = new TaskDbHelper(this);
        myList = (ListView) findViewById(R.id.list_todo);
        updateUI();

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override   //Insert
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Intent i = new Intent(getApplicationContext(), CreateTaskActivity.class);
                startActivityForResult(i, 1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_OK ){
            updateUI();
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
            if (prediction.score > 3.5 && prediction.name.toLowerCase().equals("w")) {
                //Toast.makeText(this, prediction.name + " - score:" + prediction.score, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), WeeklyViewActivity.class);
                startActivityForResult(i, 1);
                //startActivity(i);
            }
        }
    }

    private void updateUI() {
        ArrayList<Item> taskList = new ArrayList<>();
        taskId.clear();
        SQLiteDatabase db = myHelper.getReadableDatabase();
        //SQLiteDatabase db = myHelper.getWritableDatabase();
        //db.execSQL("DELETE FROM " + TaskContract.TaskEntry.TABLE + ";");
        //db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE);
        String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE
                + " WHERE " + TaskContract.TaskEntry.COL_TASK_KEY + "=1 ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE + ";";
        /*
        SELECT TITLE, TYPE, DUEDATE FROM TASKS WHERE KEY=1 ORDER BY DUEDATE;
        */

        Cursor cursor = db.rawQuery(selectQuery, null);
        String newDate = "";

        if (cursor.moveToFirst()) {
            do {
                taskId.add(cursor.getString(0));
                String[] input = cursor.getString(3).split("-");
                switch(input[1])
                {
                    case "01": newDate = "Jan";   break;
                    case "02": newDate = "Feb";   break;
                    case "03": newDate = "Mar";   break;
                    case "04": newDate = "Apr";   break;
                    case "05": newDate = "May";   break;
                    case "06": newDate = "Jun";   break;
                    case "07": newDate = "Jul";   break;
                    case "08": newDate = "Aug";   break;
                    case "09": newDate = "Sep";   break;
                    case "10": newDate = "Oct";   break;
                    case "11": newDate = "Nov";   break;
                    case "12": newDate = "Dec";   break;
                }
                newDate = cursor.getString(4) + ", " + newDate + " " + input[2];

                taskList.add( new Item( cursor.getString(1), cursor.getString(2), newDate ) );
            } while (cursor.moveToNext());
        }

        if (myAdapter == null) {
            myAdapter = new NewArrayAdapter(this,
                    R.layout.item_todo,
                    taskList);
            myList.setAdapter(myAdapter);
        }
        else {
            myAdapter.clear();
            myAdapter.addAll(taskList);
            myAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }
}
