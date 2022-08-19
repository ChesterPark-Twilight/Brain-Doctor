package com.example.braindoctor.DrawImage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import com.example.braindoctor.ReadNII.NiftiVolume;

public class DrawImage{
    private static int width = 240;
    private static int length = 240;

    public static Bitmap drawBitmap(NiftiVolume niftiVolume, int height, boolean tumour, boolean core, boolean enhance, NiftiVolume mask) {
        Bitmap bitmap = Bitmap.createBitmap(width, length, Bitmap.Config.ARGB_8888);
        for (int i=0; i<width; i++) {
            for (int j=0; j<length; j++) {
                int color = (int) niftiVolume.data.get(i, j, height, 0);
                bitmap.setPixel(i, j, Color.argb(255, color, color, color));
            }
        }
        if (tumour || core || enhance) {
            Bitmap bitmapMask = Bitmap.createBitmap(width, length, Bitmap.Config.ARGB_8888);
            for (int i=0; i<width; i++) {
                for (int j=0; j<length; j++) {
                    int type = (int) mask.data.get(i, j, height, 0);
                    if (type==4 && tumour) {
                        bitmapMask.setPixel(i, j, Color.argb(255, 255, 0, 0));
                        Log.d("测试", "Red");
                    }else if (type==2 && core) {
                        bitmapMask.setPixel(i, j, Color.argb(255, 0, 255, 0));
                    }else if (type==1 && enhance) {
                        bitmapMask.setPixel(i, j, Color.argb(255, 0, 0, 255));
                    }else {
                        bitmapMask.setPixel(i, j, Color.argb(0, 0, 0, 0));
                    }
                }
            }
            bitmap = combineBitmap(bitmap, bitmapMask);
        }
        return bitmap;
    }

    private static Bitmap combineBitmap(Bitmap background, Bitmap mask) {
        Bitmap resultBitmap = Bitmap.createBitmap(width, length, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(mask, 0, 0, null);
        canvas.save();
        canvas.restore();
        return resultBitmap;
    }
}
