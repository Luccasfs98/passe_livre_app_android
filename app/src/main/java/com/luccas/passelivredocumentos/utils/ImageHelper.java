package com.luccas.passelivredocumentos.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;


public class ImageHelper {

    public static Bitmap setupBitmap(Bitmap bitmap) {

        Matrix matrix = new Matrix();

        if(bitmap.getWidth() > bitmap.getHeight()){
            matrix.postRotate(90);
        }

        int nh = (int) ( bitmap.getHeight() * (1024.0 / bitmap.getWidth()) );

        bitmap = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return bitmap;
    }
}