package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.DebugActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    TodoDbHelper dbHelper ;
    SQLiteDatabase db ;

    static Context context = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new TodoDbHelper(this);
        db = dbHelper.getWritableDatabase();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        if(db == null){
            return Collections.emptyList();
        }

        List<Note> result = new LinkedList<>();
        Cursor cursor= null;
        try {
            cursor = db.query(TodoContract.Todo.TABLE_NAME,null,null,null,null,null,"priority DESC, date DESC");
            while (cursor.moveToNext()){
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.Todo.COLUMN_NAME_CONTENT));
                long dateMs = cursor.getLong(cursor.getColumnIndex(TodoContract.Todo.COLUMN_NAME_DATE));
                int intState = cursor.getInt(cursor.getColumnIndex(TodoContract.Todo.COLUMN_NAME_STATE));
                long _id = cursor.getLong(cursor.getColumnIndex(TodoContract.Todo._ID));
                int priority= cursor.getInt(cursor.getColumnIndex(TodoContract.Todo.COLUMN_NAME_PRIORITY));
                Note note = new Note(_id);
                note.setContent(content);
                note.setDate(new Date(dateMs));
                note.setState(State.from(intState));
                note.setPriority(priority);
                result.add(note);
            }
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return result;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        if(db == null){
            return ;
        }
        System.out.println("尝试删除");
        if( db.delete(TodoContract.Todo.TABLE_NAME,"_id=?",new String[]{String.valueOf(note.id)})>0){
            notesAdapter.refresh(loadNotesFromDatabase());
        }
        else{
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNode(Note note) {
        // 更新数据
        if(db == null){
            return ;
        }
        System.out.println("尝试更新");
        ContentValues values = new ContentValues();
        values.put(TodoContract.Todo.COLUMN_NAME_CONTENT,note.getContent());
        values.put(TodoContract.Todo.COLUMN_NAME_DATE, note.getDate().getTime());
        values.put(TodoContract.Todo.COLUMN_NAME_STATE,note.getState().intValue);
        if( db.update(TodoContract.Todo.TABLE_NAME,values,"_id=?",new String[]{String.valueOf(note.id)})>0){
            notesAdapter.refresh(loadNotesFromDatabase());
        }
        else{
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
        }
    }

    public static Context getContext(){
        return context;
    }

}
