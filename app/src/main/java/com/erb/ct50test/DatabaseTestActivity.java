package com.erb.ct50test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DatabaseTestActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText etTestData;
    private TextView tvRecordCount;
    private TextView tvLastRecords;
    private Button btnInsert;
    private Button btnQuery;
    private Button btnDelete;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        try {
            // Initialize database
            dbHelper = new DatabaseHelper(this);

            // Initialize views
            etTestData = findViewById(R.id.etTestData);
            tvRecordCount = findViewById(R.id.tvRecordCount);
            tvLastRecords = findViewById(R.id.tvLastRecords);
            btnInsert = findViewById(R.id.btnInsert);
            btnQuery = findViewById(R.id.btnQuery);
            btnDelete = findViewById(R.id.btnDelete);
            btnBack = findViewById(R.id.btnBack);

            // Initial query to show current state
            queryDatabase();

            // Insert Button
            btnInsert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String testData = etTestData.getText().toString().trim();
                    if (testData.isEmpty()) {
                        testData = "Test Entry " + System.currentTimeMillis();
                    }
                    insertTestRecord(testData);
                    etTestData.setText("");
                    queryDatabase();
                }
            });

            // Query Button
            btnQuery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    queryDatabase();
                }
            });

            // Delete All Button
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAllRecords();
                    queryDatabase();
                }
            });

            // Back Button
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing database: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void insertTestRecord(String testData) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_TIMESTAMP, getCurrentTimestamp());
            values.put(DatabaseHelper.COLUMN_TEST_DATA, testData);

            long newRowId = db.insert(DatabaseHelper.TABLE_NAME, null, values);

            if (newRowId != -1) {
                Toast.makeText(this, "✓ Record inserted! ID: " + newRowId,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "✗ Insert failed!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void queryDatabase() {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Get total count
            Cursor countCursor = db.rawQuery("SELECT COUNT(*) FROM " +
                    DatabaseHelper.TABLE_NAME, null);
            countCursor.moveToFirst();
            int count = countCursor.getInt(0);
            countCursor.close();

            tvRecordCount.setText("Total Records: " + count);

            // Get last 5 records
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DatabaseHelper.COLUMN_ID + " DESC",
                    "5"
            );

            StringBuilder records = new StringBuilder();
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                    String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_TIMESTAMP));
                    String data = cursor.getString(cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_TEST_DATA));

                    records.append("ID: ").append(id).append("\n");
                    records.append("Time: ").append(timestamp).append("\n");
                    records.append("Data: ").append(data).append("\n");
                    records.append("---\n");
                } while (cursor.moveToNext());
            } else {
                records.append("No records in database");
            }
            cursor.close();

            tvLastRecords.setText(records.toString());

        } catch (Exception e) {
            Toast.makeText(this, "Query Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void deleteAllRecords() {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int deletedRows = db.delete(DatabaseHelper.TABLE_NAME, null, null);
            Toast.makeText(this, "✓ Deleted " + deletedRows + " records",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Delete Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getCurrentTimestamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}