package com.firebot.dhruv.tensorflow.ml;

import android.content.Context;
import android.graphics.Bitmap;

public interface DeeplabInterface {

    boolean initialize(Context context);

    boolean isInitialized();

    int getInputSize();

    Bitmap segment(Bitmap bitmap);

}


