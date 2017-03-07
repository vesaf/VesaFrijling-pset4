package com.example.vesaf.vesafrijling_pset4;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {
    private DBManager dbManager;
    private EditText editText;
    private ListView listView;

    TodoCursorAdapter todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);

        //Initialize DB
        dbManager = new DBManager(this);
        dbManager.open();

        //
        Cursor cursor = dbManager.fetch();
        todoAdapter = new TodoCursorAdapter(this, cursor);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(todoAdapter);

        setListener();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dbManager != null) {
            dbManager.close();
        }

    }

    public void AddToWatchList(View view) {
        final String entry = editText.getText().toString();
        dbManager.insert(entry);

        fetchCursor();
    }

    public void fetchCursor() {
        Cursor cursor = dbManager.fetch();
        todoAdapter.changeCursor(cursor);
    }

    public class TodoCursorAdapter extends CursorAdapter {
        public TodoCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_item, parent,  false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView listitem = (TextView) view.findViewById(R.id.textView);
            String body = cursor.getString(cursor.getColumnIndexOrThrow("subject"));

            listitem.setText(body);
        }
    }

    private void setListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String itemToDelete = (listView.getItemAtPosition(position)).toString();
                dbManager.delete(id);

                fetchCursor();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                String itemToEdit = cursor.getString(cursor.getColumnIndex("subject"));
                if (itemToEdit.length() > 7 && itemToEdit.substring(0, 8).equals("[DONE!] ")) {
                    itemToEdit = itemToEdit.substring(8, itemToEdit.length());
                }
                else {
                    itemToEdit = "[DONE!] " + itemToEdit;
                }
                dbManager.update(id, itemToEdit);
                fetchCursor();
            }
        });
    }
}
