package com.assessor.android.cam.camutil;

import androidx.annotation.FloatRange;

public class Layer {
    float x; // top left X
    float y; // top left Y
    float scale;
    @FloatRange(from = 0.0F, to = 360.0F)
    float rotationInDegrees;
}
