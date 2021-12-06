package com.delivery.deliveryapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import android.util.Base64;

import java.util.Calendar;
import java.util.Date;

import static android.graphics.BitmapFactory.decodeByteArray;

public class Utils {

    //trovato da qualche parte su stackoverflow
    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    //riferimento https://stackoverflow.com/questions/36492084/how-to-convert-an-image-to-base64-string-in-java
    public static void base64ImgEncode()
    {

    }

    public static Bitmap base64BitmapDecode(String coded)
    {
        byte [] bytes = Base64.decode(coded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    //https://www.baeldung.com/java-date-without-time
    public static Date getDateTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}

