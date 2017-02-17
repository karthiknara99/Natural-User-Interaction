package com.teamnougat.todolistapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.teamnougat.todolistapp.db.TaskContract;
import com.teamnougat.todolistapp.db.TaskDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TaskDbHelper myHelper;
    private ListView myList;
    private NewArrayAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myHelper = new TaskDbHelper(this);
        myList = (ListView) findViewById(R.id.list_todo);
        updateUI();
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

    private void updateUI() {
        ArrayList<Item> taskList = new ArrayList<>();
        SQLiteDatabase db = myHelper.getReadableDatabase();

        String selectQuery = "SELECT " + TaskContract.TaskEntry.COL_TASK_TITLE + ", "
                + TaskContract.TaskEntry.COL_TASK_TYPE + ", " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " FROM " + TaskContract.TaskEntry.TABLE
                + " WHERE " + TaskContract.TaskEntry.COL_TASK_KEY + " ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                taskList.add( new Item( cursor.getString(0), cursor.getString(1), cursor.getString(2) ) );
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
