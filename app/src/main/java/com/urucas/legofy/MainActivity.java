package com.urucas.legofy;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CAMERA_PERMISSION = 1;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        Button cameraButton = (Button) findViewById(R.id.cameraBtt);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermissions();
            }
        });

        Button aboutButton = (Button) findViewById(R.id.aboutBtt);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkCameraPermissions() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA)){
            onCameraPermissionGranted();
        }else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            showPermissionRationaleDialog();
        }else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(dialog != null) {
            dialog.dismiss();
        }
    }

    private void showPermissionRationaleDialog() {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
                if(dialogInterface.BUTTON_POSITIVE == i) {
                    requestCameraPermission();
                }else{
                    onCameraPermissionDenied();
                }
            }
        };
        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.camera_access)
                .setMessage(R.string.camera_permission_rationale)
                .setPositiveButton(R.string.give_access, onClickListener)
                .setNegativeButton(R.string.no_way, onClickListener)
                .setCancelable(false)
                .create();
        dialog.show ();
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQ_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode != REQ_CAMERA_PERMISSION) {
            return;
        }
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        }else {
            onCameraPermissionDenied();
        }
    }

    private void onCameraPermissionGranted() {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }

    private void onCameraPermissionDenied() {
        Toast.makeText(MainActivity.this, R.string.camera_permission_denied, Toast.LENGTH_LONG).show();
    }

}
