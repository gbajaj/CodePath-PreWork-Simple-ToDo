package com.gauravbajaj.actionlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by gauravb on 1/14/17.
 */

public class EditItemActivity extends AppCompatActivity {
    public static final String ACTION_ITEM_CONTENT = "ACTION_ITEM_CONTENT";
    public static int EDIT_REQUEST_CODE = 0x1;
    /**
     * Save button
     */
    private Button btnSave;
    /**
     * Edit Text to edit the content as requested
     */
    private EditText itemText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        String contentToEdit = getIntent().getStringExtra(ItemsListActivity.SELECTED_ITEM_CONTENT);
        //reference of itemText
        itemText = (EditText) findViewById(R.id.editText_editItem);
        itemText.setText(contentToEdit);

        //Move the cursor to the end
        itemText.setSelection(contentToEdit.length());

        //Set text watched to enable disable save button
        itemText.addTextChangedListener(editTextWatcher);

        //reference of button
        btnSave = (Button) findViewById(R.id.button_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String textFromEditText = itemText.getText().toString();
                final Intent resultIntent = new Intent();

                // Set the content
                resultIntent.putExtra(ACTION_ITEM_CONTENT, textFromEditText);
                // Set result intent
                setResult(RESULT_OK, resultIntent);
                //kill activity by calling finish
                finish();
            }
        });

        //disabling save button
        btnSave.setEnabled(false);

        //Set Action bar icon and show that
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.check);

        //Bring keyboard to focus as soon as activity is visible
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    //text watch listener for itemText in order to make button enable and disable
    private TextWatcher editTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String textFromEditText = itemText.getText().toString();
            //check if user entered some data, enable the button
            if (textFromEditText.trim().length() > 0) {
                btnSave.setEnabled(true);
            } else {
                btnSave.setEnabled(false);
            }
        }
    };
}
