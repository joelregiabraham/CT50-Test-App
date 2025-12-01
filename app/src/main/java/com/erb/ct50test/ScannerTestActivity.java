package com.erb.ct50test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ScannerTestActivity extends AppCompatActivity {

    private TextView tvScanResult;
    private TextView tvScanCount;
    private TextView tvLastScanTime;
    private Button btnClearScans;
    private Button btnBack;

    private int scanCount = 0;
    private BroadcastReceiver scanReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_test);

        // Initialize views
        tvScanResult = findViewById(R.id.tvScanResult);
        tvScanCount = findViewById(R.id.tvScanCount);
        tvLastScanTime = findViewById(R.id.tvLastScanTime);
        btnClearScans = findViewById(R.id.btnClearScans);
        btnBack = findViewById(R.id.btnBack);

        // Clear button
        btnClearScans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCount = 0;
                tvScanResult.setText("Waiting for scan...");
                tvScanCount.setText("Scans: 0");
                tvLastScanTime.setText("Last scan: --");
                Toast.makeText(ScannerTestActivity.this, "Cleared!", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize scanner receiver
        setupScannerReceiver();
    }

    private void setupScannerReceiver() {
        scanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Method 1: DataWedge standard intent extras
                String scannedData = intent.getStringExtra("com.symbol.datawedge.data_string");
                String labelType = intent.getStringExtra("com.symbol.datawedge.label_type");

                // Method 2: Honeywell specific extras (alternative)
                if (scannedData == null) {
                    scannedData = intent.getStringExtra("data");
                }
                if (scannedData == null) {
                    scannedData = intent.getStringExtra("SCAN_BARCODE1");
                }

                // Method 3: Check for generic barcode data
                if (scannedData == null) {
                    scannedData = intent.getStringExtra("barc odeData");
                }

                if (scannedData != null && !scannedData.isEmpty()) {
                    scanCount++;

                    // Update UI
                    tvScanResult.setText("Scanned: " + scannedData);
                    tvScanCount.setText("Scans: " + scanCount);
                    tvLastScanTime.setText("Last scan: " + getCurrentTime());

                    if (labelType != null) {
                        tvScanResult.append("\nType: " + labelType);
                    }

                    // Vibrate feedback (if available)
                    Toast.makeText(ScannerTestActivity.this,
                            "✓ Scan successful!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScannerTestActivity.this,
                            "Scan received but no data found",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Register multiple intent filters to catch different scanner broadcast formats
        IntentFilter filter1 = new IntentFilter("com.symbol.datawedge.data");
        IntentFilter filter2 = new IntentFilter("com.honeywell.decode.intent.action.SCAN");
        IntentFilter filter3 = new IntentFilter("com.erb.ct50test.SCAN");

        registerReceiver(scanReceiver, filter1);
        registerReceiver(scanReceiver, filter2);
        registerReceiver(scanReceiver, filter3);

        Toast.makeText(this, "Scanner ready! Scan a barcode to test.", Toast.LENGTH_LONG).show();
    }

    private String getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss",
                java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(currentTime));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanReceiver != null) {
            try {
                unregisterReceiver(scanReceiver);
            } catch (Exception e) {
                // Receiver not registered
            }
        }
    }
}