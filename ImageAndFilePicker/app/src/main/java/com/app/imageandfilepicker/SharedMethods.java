package com.app.imageandfilepicker;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

//use this class to write methods, which are used in many other classes
public class SharedMethods {

    public static String encodeImageBitmap(Bitmap bm) {
        String encImage = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            byte[] b = baos.toByteArray();
            encImage = Base64.encodeToString(b, Base64.DEFAULT);

        } catch (Exception e) {
            Log.d("HomeActivity", "encodeImage: " + e.getMessage());
        }
        return encImage;
    }

}
