package com.example.todoapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity implements OnClickListener{
    private static final int EDIT_ITEM_RESULT = 0;
    ListView lvItems;
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    Button mBtnAddItem;
    EditText mEtNewItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void setupListItemClickListener() {
        lvItems.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), EditItemActivity.class);
                i.putExtra("itemdesc", items.get(position));
                i.putExtra("itempos", position);
                startActivityForResult(i, EDIT_ITEM_RESULT);

            }});
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
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
        }

    }

    private void addTodoItem() {
        itemsAdapter.add(mEtNewItem.getText().toString());
        mEtNewItem.setText("");
        saveItems();
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try{
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch(IOException e) {
            items = new ArrayList<String>();
            e.printStackTrace();
        }
    }

    private void saveItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try{
            FileUtils.writeLines(todoFile,items);
        } catch(IOException e) {
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
