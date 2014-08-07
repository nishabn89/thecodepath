package com.example.todoapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditItemActivity extends Activity implements OnClickListener{
    EditText mEditItem;
    Button mBtnSave;
    int mItemPos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        mEditItem = (EditText) findViewById(R.id.editItemEditText);
        mBtnSave = (Button) findViewById(R.id.saveBtn);
        mBtnSave.setOnClickListener(this);
        if(getIntent() != null) {
            handleIntent();
        }
    }

    private void handleIntent() {
        String itemOldDesc = getIntent().getStringExtra("itemdesc");
        mEditItem.setText(itemOldDesc);
        mEditItem.setSelection(mEditItem.length());
        mItemPos = getIntent().getIntExtra("itempos", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_item, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        int clickedId = v.getId();
        switch(clickedId) {
            case R.id.saveBtn:
                Intent data = new Intent();
                data.putExtra("itemdesc", mEditItem.getText().toString());
                data.putExtra("itempos", mItemPos);
                setResult(RESULT_OK, data);
                finish();
                break;
        }
    }

}
