package com.gauravbajaj.actionlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by gauravb on 1/14/17.
 */

public class ItemsListActivity extends AppCompatActivity {
    private static final String TAG = ItemsListActivity.class.getSimpleName();
    public static final String SELECTED_ITEM_CONTENT = "SELECTED_ITEM_CONTENT";
    public static final String ACTION_ITEM_FILE_NAME = "actionItems.txt";

    /**
     * List of current action items
     */
    private List<String> actionItems = new ArrayList<>();
    /**
     * Index of the item user requested to edit
     */
    private int editedItemIndex;
    /**
     * Action Items Adaptor
     */
    private ArrayAdapter<String> actionItemsAdapter;
    /**
     * ListView to display action items
     */
    private ListView listView;
    /**
     * Edit text to read enter action items content
     */
    private EditText editText;
    /**
     * button to save item
     */
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        //reference of list view
        listView = (ListView) findViewById(R.id.listView_items);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "setOnItemLongClickListener. onItemLongClick: " + position);
                //remove selected from the list
                actionItems.remove(position);

                //update list view
                actionItemsAdapter.notifyDataSetChanged();
                listView.setSelection(actionItemsAdapter.getCount() - 1);
                writeActionItemsToFile(actionItems);
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                final String selectedItemContent = actionItems.get(index);
                Log.d(TAG, "setOnItemClickListener. onItemClick: " + index);
                final Intent i = new Intent(ItemsListActivity.this, EditItemActivity.class);
                editedItemIndex = index;
                i.putExtra(SELECTED_ITEM_CONTENT, selectedItemContent);
                //Launch Edit Activity
                startActivityForResult(i, EditItemActivity.EDIT_REQUEST_CODE);
            }
        });

        //edit text reference
        editText = (EditText) findViewById(R.id.editText_addItem);
        editText.requestFocus();
        editText.addTextChangedListener(editTextWatcher);

        //reference to button
        btnAdd = (Button) findViewById(R.id.button_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String itemText = editText.getText().toString();
                if (itemText != null && itemText.trim().isEmpty() == false) {
                    //following code updates 'actionItems' as well
                    actionItemsAdapter.add(itemText);
                    Log.d(TAG, "onAddItem: ItemContent " + itemText);
                    editText.setText("");
                    //new entry added in the file
                    //By this time 'actionItems' should already be updated with recent entry
                    writeActionItemsToFile(actionItems);
                }
            }
        });
        //Disable the add button for the first time
        btnAdd.setEnabled(false);

        //read existing actionItems from the file
        actionItems = readActionItemsFromFile();

        //set actionItems into actionItems adaptor
        actionItemsAdapter = new ArrayAdapter<>(this, R.layout.list_layout, actionItems);
        listView.setAdapter(actionItemsAdapter);

        Log.d(TAG, "onCreate: existing action actionItems : " + actionItems);

        //Set action bar bar icon and enable it
        getSupportActionBar().setIcon(R.drawable.check);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Bring soft key board to focus
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * Saves the list of string to a file
     * @param actionItemsToSave
     */
    private void writeActionItemsToFile(List<String> actionItemsToSave) {
        final File dir = getFilesDir();
        final File actionItemsFile = new File(dir, ACTION_ITEM_FILE_NAME);
        try {
            if (actionItemsToSave != null) {
                FileUtils.writeLines(actionItemsFile, actionItemsToSave);
            }
        } catch (IOException e) {
            Log.e(TAG, "writeActionItemsToFile: FAILED", e);
            e.printStackTrace();
        }
    }

    /**
     * Reads file content
     * @return
     */
    private List<String> readActionItemsFromFile() {
        final File dir = getFilesDir();
        final File actionItemsFile = new File(dir, ACTION_ITEM_FILE_NAME);
        List<String> res = new ArrayList<>();
        try {
            res = FileUtils.readLines(actionItemsFile);
        } catch (IOException e) {
            Log.e(TAG, "readActionItemsFromFile: FAILED", e);
            e.printStackTrace();
        }
        return res;
    }


    //text watch listener for editText in order to make button enable and disable
    private TextWatcher editTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String enteredText = editText.getText().toString();
            //Disable the add button if text is empty or has white spaces
            if (enteredText.trim().length() > 0) {
                btnAdd.setEnabled(true);
            } else {
                btnAdd.setEnabled(false);
            }
        }
    };

    // Call Back method  to get the edited text  form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check if result is ok
        if (resultCode == RESULT_OK) {
            //Check if result is corresponding to the edit request
            if (requestCode == EditItemActivity.EDIT_REQUEST_CODE) {
                if (null != data) {
                    //get the data entered by the user
                    final String textForList = data.getStringExtra(EditItemActivity.ACTION_ITEM_CONTENT);
                    if (textForList != null && textForList.trim().isEmpty() == false) {
                        actionItems.set(editedItemIndex, textForList);
                        actionItemsAdapter.notifyDataSetChanged();
                        writeActionItemsToFile(actionItems);
                    }
                }
            }
        }
    }
}




