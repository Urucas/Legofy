package com.urucas.legofy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

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

    public static Bitmap picture;
    private LegoView legoView;
    private FrameLayout frame;
    public static String sharePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image);
        frame = (FrameLayout) findViewById(R.id.legoFrame);
        if(savedInstanceState == null) {
            legoView = new LegoView(ImageActivity.this);
            frame.addView(legoView);
        }

        ImageButton shareBtt = (ImageButton) findViewById(R.id.shareBtt);
        shareBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareBtt:
                share();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void share() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        Log.i("sharePath", String.valueOf(sharePath));
        if(sharePath == null) {

            Bitmap bmp = Bitmap.createBitmap(this.legoView.getWidth(), this.legoView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas shareCanvas = new Canvas(bmp);
            Legofy.me(ImageActivity.this, shareCanvas, picture);

            Bitmap newBmp = bmp.copy(bmp.getConfig(), true);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            newBmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

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
                Toast.makeText(ImageActivity.this, R.string.error_getting_image, Toast.LENGTH_LONG);
                return;
            }
        }
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(sharePath));
        startActivity(Intent.createChooser(share, "Share Legofy'ed image!"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return true;
    }

    public class LegoView extends SurfaceView implements SurfaceHolder.Callback2 {

        public LegoView(Context context) {
            super(context);
            getHolder().addCallback(this);
        }

        @Override
        public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {}

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
           Canvas canvas = surfaceHolder.lockCanvas();
            if(canvas != null) {
                Legofy.me(ImageActivity.this, canvas, picture);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}
    }
}
