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
    }

    public class LegoView extends SurfaceView implements SurfaceHolder.Callback2 {

        public LegoView(Context context) {
            super(context);
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
                int cw = canvas.getWidth(), ch = canvas.getHeight();

                // resize flower image to fit screen width
                Bitmap flower = BitmapFactory.decodeResource(getResources(), R.drawable.flower);
                Bitmap flowerResized;
                int flower_width = flower.getWidth(), flower_height = flower.getHeight();
                if(flower_width < cw) {
                    float percent = (flower_width*100)/cw;
                    float scaleHeight = (percent*flower_height)/ch;
                    flowerResized = Bitmap.createScaledBitmap(flower, flower_width, (int)scaleHeight, false);
                    flower_height = (int) scaleHeight;
                    flower.recycle();
                }else{
                    float percent = (cw*100)/flower_width;
                    float scaleHeight = flower_height*(percent/100);
                    flowerResized = Bitmap.createScaledBitmap(flower, cw, (int)scaleHeight, false);
                    flower_width = cw;
                    flower_height = (int)scaleHeight;
                    flower.recycle();
                }

                // resize brick
                Bitmap brick = BitmapFactory.decodeResource(getResources(), R.drawable.brick);
                int brick_width = (int)(brick.getWidth()*0.4f), brick_height = (int)(brick.getHeight()*0.4f);
                Bitmap brickResized = Bitmap.createScaledBitmap(brick, brick_width, brick_height, false);

                int y = 0, x = 0;
                while(y < flower_height) {
                    while(x < flower_width) {

                        // get image pixel center colour
                        int pos_x = x + brick_width/2, pos_y = y + brick_height/2;
                        int colour = flowerResized.getPixel(pos_x, pos_y);
                        int r = Color.red(colour),
                            g = Color.green(colour),
                            b = Color.blue(colour);

                        // draw square
                        Paint paint = new Paint();
                        paint.setColor(Color.rgb(r, g, b));
                        canvas.drawRect(x, y, x + brick_width, y + brick_height, paint);

                        // draw brick
                        ColorMatrix colorMatrix = new ColorMatrix();
                        colorMatrix.setSaturation(0f);
                        float[] colorTransform = {
                                0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0,
                                0, 0, 0, 0.3f, 0
                        };
                        colorMatrix.set(colorTransform);
                        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                        Paint paint1 = new Paint();
                        paint1.setColorFilter(colorFilter);
                        canvas.drawBitmap(brickResized, x, y, paint1);

                        x+= brick_width;
                    }
                    x = 0;
                    y+= brick_height;
                }
                // canvas.drawBitmap(flowerResized, 0, 0, null);
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
