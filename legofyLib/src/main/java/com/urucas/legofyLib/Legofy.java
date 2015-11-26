package com.urucas.legofyLib;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import com.urucas.legofyLib.R;

/**
 * Created by vruno on 11/18/15.
 */

public abstract class Legofy {

    public static void me(Context context, Canvas canvas, int imageResource) {
        Resources res = context.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, imageResource);
        Legofy.me(context, canvas, bmp);
    }

    public static void me(Context context, Canvas canvas, Bitmap bmp) {

        Log.i("here", "yes");
        // set canvas background color to white
        canvas.drawColor(Color.BLACK);
        int cw = canvas.getWidth(), ch = canvas.getHeight();

        // resize flower image to fit screen width
        Bitmap bmpResized;
        int flower_width = bmp.getWidth(), flower_height = bmp.getHeight();

        if(flower_width < cw) {
            float percent = (cw*100)/flower_width;
            flower_width = cw;
            float scaleHeight = (percent*flower_height)/100;
            Log.i("scale height", String.valueOf((int)scaleHeight));
            bmpResized = Bitmap.createScaledBitmap(bmp, flower_width, (int)scaleHeight, false);
            flower_height = scaleHeight > ch ? ch : (int) scaleHeight;
            // bmp.recycle();
        }else{
            Log.i("aca", "2");
            float percent = (cw*100)/flower_width;
            float scaleHeight = flower_height*(percent/100);
            bmpResized = Bitmap.createScaledBitmap(bmp, cw, (int)scaleHeight, false);
            flower_width = cw;
            flower_height = (int)scaleHeight;
            // bmp.recycle();
        }

        // resize brick
        Resources res = context.getResources();
        Bitmap brick = BitmapFactory.decodeResource(res, R.drawable.brick);
        int brick_width = (int)(brick.getWidth()*0.4f), brick_height = (int)(brick.getHeight()*0.4f);
        Bitmap brickResized = Bitmap.createScaledBitmap(brick, brick_width, brick_height, false);

        int y = 0, x = 0;
        while(y < flower_height) {
            while(x < flower_width) {
                // get image pixel center colour
                int pos_x = x + brick_width/2, pos_y = y + brick_height/2;
                int colour = bmpResized.getPixel(pos_x, pos_y);
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
        // canvas.drawBitmap(bmpResized, 0, 0, new Paint());
    }
}
