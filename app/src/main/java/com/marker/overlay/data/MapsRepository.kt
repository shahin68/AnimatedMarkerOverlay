package com.marker.overlay.data

import com.google.android.gms.maps.model.LatLng

interface MapsRepository {
    suspend fun fetchSomeNearbyLocations(
        myLatLng: LatLng
    ): List<LatLng>
}