package com.marker.overlay.data

import android.R.attr.radius
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MapsRepositoryImpl: MapsRepository {
    override suspend fun fetchSomeNearbyLocations(myLatLng: LatLng): List<LatLng> {
        val list = arrayListOf<LatLng>()
        for (i in 0..10) {
            val random = Random()

            val radiusInDegrees = (2000 / 111000f).toDouble()

            val u: Double = random.nextDouble()
            val v: Double = random.nextDouble()
            val w = radiusInDegrees * sqrt(u)
            val t = 2 * Math.PI * v
            val x = w * cos(t)
            val y = w * sin(t)

            val newX = x / cos(Math.toRadians(myLatLng.longitude))
            val foundLongitude: Double = newX + myLatLng.latitude
            val foundLatitude: Double = y + myLatLng.longitude
            list.add(
                LatLng(
                    foundLongitude,
                    foundLatitude
                )
            )
        }
        return list
    }
}