package com.example.todoapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends Activity implements OnClickListener{
    private static final int EDIT_ITEM_RESULT = 0;
    ListView lvItems;
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    Button mBtnAddItem;
    EditText mEtNewItem;
    int touchedPos;
    Button mBtnMoveUp;
    Button mBtnMoveDown;

    private SQLiteDatabase database;
    private String[] databaseCols = { SQLLiteHelper.COLUMN_ID,
        SQLLiteHelper.COLUMN_DESC };
    SQLLiteHelper  mDbHelper;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new SQLLiteHelper(this);
        database = mDbHelper.getWritableDatabase();
        setContentView(R.layout.activity_todo);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        readItems();
        itemsAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        mBtnAddItem = (Button) findViewById(R.id.btnAddItem);
        mBtnAddItem.setOnClickListener(this);
        setupListViewListener();
        setupListItemClickListener();
        mEtNewItem = (EditText)findViewById(R.id.etNewItem);
        lvItems.setOnTouchListener(new OnGestureDetectionListener(this) {
            @Override
            public void doubleTap(final MotionEvent e) {
                final int idx = lvItems.pointToPosition((int)e.getX(), (int)e.getY());
                Intent i = new Intent(getApplicationContext(), EditItemActivity.class);
                i.putExtra("itemdesc", items.get(idx));
                i.putExtra("itempos", idx);
                startActivityForResult(i, EDIT_ITEM_RESULT);
            }
        });

        mBtnMoveUp = (Button) findViewById(R.id.btnMoveUp);
        mBtnMoveDown = (Button) findViewById(R.id.btnMoveDown);
        mBtnMoveUp.setOnClickListener(this);
        mBtnMoveDown.setOnClickListener(this);
    }

    private void setupListItemClickListener() {
        lvItems.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int j = 0; j < parent.getChildCount(); j++) {
                    lvItems.setItemChecked(j, false);
                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                }
                selectItemAtPos(position);
            }
        });
    }

    private void selectItemAtPos(int position) {
        lvItems.setSelected(true);
        lvItems.setItemChecked(position, true);
        toggleButtonState();
        lvItems.getChildAt(position).setBackgroundColor(Color.LTGRAY);
    }

    private void toggleButtonState() {
        int position = lvItems.getCheckedItemPosition();
        if(position < 0)
            return;
        disableBtns();
        if(items.size() > 1) {
            if(position != 0)
                mBtnMoveUp.setEnabled(true);
            if(position != items.size() - 1)
                mBtnMoveDown.setEnabled(true);
        }
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(items.size() == 1)
                    lvItems.setItemChecked(position, false);
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                saveItems();
                return true;
            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        int clickedId = v.getId();
        switch(clickedId) {
            case R.id.btnAddItem:
                addTodoItem();
                break;
            case R.id.btnMoveUp:
                moveItemUp();
                break;
            case R.id.btnMoveDown:
                moveItemDown();
                break;
        }

    }

    private void moveItemDown() {
        int selectedPos;
        String tmp;
        selectedPos = lvItems.getCheckedItemPosition();
        tmp = items.get(selectedPos + 1 );
        items.set(selectedPos + 1, items.get(selectedPos));
        items.set(selectedPos, tmp);
        itemsAdapter.notifyDataSetChanged();
        lvItems.getChildAt(selectedPos).setBackgroundColor(Color.TRANSPARENT);
        selectItemAtPos(selectedPos + 1);
        saveItems();
    }

    private void disableBtns() {
        mBtnMoveDown.setEnabled(false);
        mBtnMoveUp.setEnabled(false);

    }

    private void moveItemUp() {
        int selectedPos;
        String tmp;
        selectedPos = lvItems.getCheckedItemPosition();
        tmp = items.get(selectedPos - 1);
        items.set(selectedPos - 1, items.get(selectedPos));
        items.set(selectedPos, tmp);
        itemsAdapter.notifyDataSetChanged();
        lvItems.getChildAt(selectedPos).setBackgroundColor(Color.TRANSPARENT);
        selectItemAtPos(selectedPos - 1);
        saveItems();
    }

    private void addTodoItem() {
        itemsAdapter.add(mEtNewItem.getText().toString());
        mEtNewItem.setText("");
        saveItems();
    }

    private void readItems() {
        try{
            Cursor cursor = database.query(SQLLiteHelper.TABLE_ITEMS,
                databaseCols, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                items.add(cursor.getString(1));
              cursor.moveToNext();
            }
            cursor.close();
        } catch(Exception e) {
            items = new ArrayList<String>();
            e.printStackTrace();
        }
    }

    private void saveItems() {
        toggleButtonState();
        try{
            mDbHelper.dropTable(database);
            database = mDbHelper.getWritableDatabase();
            for(String item : items){
                ContentValues values = new ContentValues();
                values.put(SQLLiteHelper.COLUMN_DESC, item);
                database.insert(SQLLiteHelper.TABLE_ITEMS, null,
                    values);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_ITEM_RESULT && resultCode == RESULT_OK) {
            int pos = data.getIntExtra("itempos", items.size());
            String newItemDesc = data.getStringExtra("itemdesc");
            items.set(pos, newItemDesc);
            itemsAdapter.notifyDataSetChanged();
            saveItems();
        }
    }
}


