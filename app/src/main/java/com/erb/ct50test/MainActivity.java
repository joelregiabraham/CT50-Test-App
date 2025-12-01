package com.erb.ct50test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private Button btnScannerTest;
    private Button btnDatabaseTest;
    private Button btnUITest;
    private Button btnCheckPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        btnScannerTest = findViewById(R.id.btnScannerTest);
        btnDatabaseTest = findViewById(R.id.btnDatabaseTest);
        btnUITest = findViewById(R.id.btnUITest);
        btnCheckPermissions = findViewById(R.id.btnCheckPermissions);

        // Request permissions on startup
        checkAndRequestPermissions();

        // Scanner Test Button
        btnScannerTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScannerTestActivity.class);
                startActivity(intent);
            }
        });

        // Database Test Button
        btnDatabaseTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DatabaseTestActivity.class);
                startActivity(intent);
            }
        });

        // UI Test Button (stays on main activity for now)
        btnUITest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "UI Test: All buttons are glove-friendly size!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Check Permissions Button
        btnCheckPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionStatus();
            }
        });
    }

    private void checkAndRequestPermissions() {
        // For Android 6.0 (API 23) we need runtime permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        },
                        PERMISSION_REQUEST_CODE);
            } else {
                Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPermissionStatus() {
        StringBuilder status = new StringBuilder("Permission Status:\n\n");

        // Check WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            status.append("✓ Storage: GRANTED\n");
        } else {
            status.append("✗ Storage: DENIED\n");
        }

        // Check CAMERA
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            status.append("✓ Camera: GRANTED\n");
        } else {
            status.append("✗ Camera: DENIED\n");
        }

        status.append("\nAndroid Version: ").append(Build.VERSION.RELEASE);
        status.append("\nSDK: ").append(Build.VERSION.SDK_INT);

        Toast.makeText(this, status.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Some permissions denied. App may not work correctly.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}