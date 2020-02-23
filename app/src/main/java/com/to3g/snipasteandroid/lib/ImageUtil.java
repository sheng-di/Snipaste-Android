package com.to3g.snipasteandroid.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Size;

import java.io.IOException;

public class ImageUtil {
    public static Size getImageSize(String imageLocalPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imageLocalPath, options);
        int width = options.outWidth;
        int height = options.outHeight;

        int orientation = getImageOrientation(imageLocalPath);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_ROTATE_270: {
                return new Size(height, width);
            }
            default: {
                return new Size(width, height);
            }
        }
    }

    public static int getImageOrientation(String imageLocalPath) {
        try {
            ExifInterface exifInterface = new ExifInterface(imageLocalPath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            return orientation;
        } catch (IOException e) {
            e.printStackTrace();
            return ExifInterface.ORIENTATION_NORMAL;
        }
    }

}
