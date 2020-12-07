package com.shahin.overlay

import android.graphics.Point
import androidx.annotation.Keep
import com.google.android.gms.maps.model.LatLng

@Keep
data class Projection(
    var point: Point,
    var latLng: LatLng
)