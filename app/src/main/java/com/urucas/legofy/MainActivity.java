package com.urucas.legofy;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new LegoView(MainActivity.this));

        /*
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int height, width;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            height = size.y;
            width = size.x;
        }else {
            height = display.getHeight();
            width = display.getWidth();
        }

        LinearLayout ll = (LinearLayout) findViewById(R.id.canvas);
        Bitmap bg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        Canvas canvas = new Canvas(bg);

        Drawable d = getResources().getDrawable(R.drawable.brick);
        Bitmap brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick);
        canvas.drawBitmap(brick, 0, 0, null);
        */

    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public class LegoView extends SurfaceView implements SurfaceHolder.Callback2 {

        private final int height, width;

        public LegoView(Context context) {
            super(context);

            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Point size = new Point();
                display.getSize(size);
                height = size.y;
                width = size.x;
            }else {
                height = display.getHeight();
                width = display.getWidth();
            }

            getHolder().addCallback(this);
        }

        @Override
        public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if(canvas != null) {
                // set canvas background color to white
                canvas.drawColor(Color.WHITE);

                Bitmap flower = BitmapFactory.decodeResource(getResources(), R.drawable.flower);

                Bitmap brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick);
                float[] colorTransform = {
                        0, 1f, 0, 0, 0,
                        0, 0, 0f, 0, 0,
                        0, 0, 0, 0f, 0,
                        0, 0, 0, 1f, 0};

                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0f); //Remove Colour
                colorMatrix.set(colorTransform); //Apply the Red

                ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                Paint paint = new Paint();
                paint.setColorFilter(colorFilter);

                // drawing bricks
                int brick_width = brick.getWidth(), brick_height = brick.getHeight();
                int y = 0, x = 0;
                while(y < height) {
                    while(x < width) {
                        canvas.drawBitmap(brick, x, y, paint);
                        x+= brick_width;
                    }
                    x = 0;
                    y+= brick_height;
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    }
}
