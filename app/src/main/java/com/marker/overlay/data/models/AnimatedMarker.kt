package com.marker.overlay.data.models

import androidx.annotation.Keep
import com.google.android.gms.maps.model.LatLng

@Keep
data class AnimatedMarker(
    val latLng: LatLng,
    val markerType: MarkerType
)