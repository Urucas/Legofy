package com.urucas.legofy;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.urucas.legofyLib.Callback;
import com.urucas.legofyLib.Legofy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by vruno on 11/18/15.
 */
public class ImageActivity extends ActionBarActivity {

    private static final int REQ_STORAGE_PERMISSION = 1;
    public static Bitmap picture;
    public static String sharePath = null;
    public static Bitmap newBmp;
    public static ByteArrayOutputStream bytes;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image);

        ImageView legoImage = (ImageView) findViewById(R.id.legoImage);
        if(savedInstanceState == null) {
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            int h, w;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Point size = new Point();
                display.getSize(size);
                w = size.x;
                h = size.y;
            }else {
                w = display.getWidth();
                h = display.getHeight();
            }

            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas shareCanvas = new Canvas(bmp);
            Legofy.me(ImageActivity.this, shareCanvas, picture, new Callback() {
                @Override
                public void done(Exception e) {
                    if(e!=null) e.printStackTrace();
                }
            });

            newBmp = bmp.copy(bmp.getConfig(), true);
            bytes = new ByteArrayOutputStream();
            newBmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        }

        legoImage.setImageBitmap(newBmp);

        ImageButton shareBtt = (ImageButton) findViewById(R.id.shareBtt);
        shareBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStoragePermissions();
            }
        });
    }

    private void checkStoragePermissions() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            onStoragePermissionGranted();
        }else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showPermissionRationaleDialog();
        }else {
            requestStoragePermission();
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
                    requestStoragePermission();
                }else{
                    onStoragePermissionDenied();
                }
            }
        };
        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.storage_access)
                .setMessage(R.string.storage_permission_rationale)
                .setPositiveButton(R.string.give_access, onClickListener)
                .setNegativeButton(R.string.no_way, onClickListener)
                .setCancelable(false)
                .create();
        dialog.show ();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_STORAGE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode != REQ_STORAGE_PERMISSION) {
            return;
        }
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onStoragePermissionGranted();
        }else {
            onStoragePermissionDenied();
        }
    }

    private void onStoragePermissionGranted() {
        share();
    }

    private void onStoragePermissionDenied() {
        Toast.makeText(ImageActivity.this, R.string.storage_permission_denied, Toast.LENGTH_LONG).show();
    }

    private void share() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        if(sharePath == null) {

            Random r = new Random();
            float rf = r.nextFloat();
            String imageName = String.format(getResources().getString(R.string.image_name), String.valueOf(rf));
            try {
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + imageName);
                sharePath = "file://" + f.getAbsolutePath();
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ImageActivity.this, R.string.error_getting_image, Toast.LENGTH_LONG).show();
                return;
            }
        }
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(sharePath));
        startActivity(Intent.createChooser(share, "Share Legofy'ed image!"));
    }
}
