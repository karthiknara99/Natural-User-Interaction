package com.karthiknara99.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karthiknara99.todolist.db.TaskContract;
import com.karthiknara99.todolist.db.TaskDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TaskDbHelper myHelper;
    private ListView myList;
    private ArrayAdapter<String> myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myHelper = new TaskDbHelper(this);
        Log.d(TAG, myHelper.getDatabaseName());
        myList = (ListView) findViewById(R.id.list_todo);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Log.d(TAG, "Add a new task");
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
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = myHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE + " WHERE " + TaskContract.TaskEntry.COL_TASK_KEY + ";";
        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                taskList.add( cursor.getString(1) );
            } while (cursor.moveToNext());
        }

        if (myAdapter == null) {
            myAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
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
