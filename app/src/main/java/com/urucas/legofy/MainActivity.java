package com.urucas.legofy;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
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

        private final int screenHeight, screenWidth;

        public LegoView(Context context) {
            super(context);

            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Point size = new Point();
                display.getSize(size);
                screenHeight = size.y;
                screenWidth = size.x;
            }else {
                screenHeight = display.getHeight();
                screenWidth = display.getWidth();
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

                // resize flower image to fit screen width
                Bitmap flower = BitmapFactory.decodeResource(getResources(), R.drawable.flower);
                Bitmap flowerResized;
                int flower_width = flower.getWidth(), flower_height = flower.getHeight();
                if(flower_width < screenWidth) {
                    float percent = (flower_width*100)/screenWidth;
                    float scaleHeight = (percent*flower_height)/screenHeight;
                    flowerResized = Bitmap.createScaledBitmap(flower, flower_width, (int)scaleHeight, false);
                    flower.recycle();
                }else{
                    float percent = (screenWidth*100)/flower_width;
                    float scaleHeight = flower_height*(percent/100);
                    flowerResized = Bitmap.createScaledBitmap(flower, screenWidth, (int)scaleHeight, false);
                    flower.recycle();
                }

                // resize brick
                Bitmap brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick);
                int brick_width = (int)(brick.getWidth()*0.6f), brick_height = (int)(brick.getHeight()*0.6f);
                Bitmap brickResized = Bitmap.createScaledBitmap(brick, brick_width, brick_height, false);

                int y = 0, x = 0;
                while(y < flower_height) {
                    while(x < flower_width) {
                        int pos_x = (x + brick_width)/2, pos_y = (y + brick_height)/2;
                        String pos = String.valueOf(pos_x)+","+String.valueOf(pos_y);
                        Log.i("pos", pos);
                        ColorMatrix colorMatrix = new ColorMatrix();
                        colorMatrix.setSaturation(0f);
                        int colour = flowerResized.getPixel(pos_x,pos_y);

                        float[] colorTransform = {
                                0, 0, 0, 0, Color.red(colour),
                                0, 0, 0, 0, Color.green(colour),
                                0, 0, 0, 0, Color.blue(colour),
                                0, 0, 0, 1f, 0
                        };

                        colorMatrix.set(colorTransform);

                        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                        Paint paint = new Paint();
                        paint.setColorFilter(colorFilter);

                        canvas.drawBitmap(brickResized, x, y, paint);
                        x+= brick_width;
                    }
                    x = 0;
                    y+= brick_height;
                }

                //canvas.drawBitmap(flower, 0, 0, null);
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
