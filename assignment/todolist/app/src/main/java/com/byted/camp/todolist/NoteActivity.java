package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;

public class NoteActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText editText;
    private Button addBtn;
    SQLiteDatabase db;
    TodoDbHelper todoDbHelper;
    int priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);
        todoDbHelper= new TodoDbHelper(this);
        db=todoDbHelper.getWritableDatabase();

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }
        Spinner mSpinner;
        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim(),priority);
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        mSpinner = findViewById(R.id.spinner1);

        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1.arr)
        mSpinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean saveNote2Database(String content, int priority) {
        // TODO 插入一条新数据，返回是否插入成功
        ContentValues values = new ContentValues();
        values.put(TodoContract.Todo.COLUMN_NAME_CONTENT,content);
        values.put(TodoContract.Todo.COLUMN_NAME_DATE, System.currentTimeMillis());
        values.put(TodoContract.Todo.COLUMN_NAME_STATE, State.TODO.intValue);
        System.out.println("??????"+priority);
        values.put(TodoContract.Todo.COLUMN_NAME_PRIORITY,priority);
        if(db.insert(TodoContract.Todo.TABLE_NAME,null,values) != -1 ){
            System.out.println("success");
            return true;
        }
        else {
            System.out.println("fail");
            return false;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String content=adapterView.getItemAtPosition(position).toString();
        priority = Integer.parseInt(content);
        //Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
