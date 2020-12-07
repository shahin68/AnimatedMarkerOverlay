package com.marker.overlay.ui.fragments.cluster.data

import androidx.annotation.Keep
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.marker.overlay.data.models.MarkerType

@Keep
data class ClusterMarker(
    private val position: LatLng,
    private val title: String?,
    private val snippet: String?,
    val markerType: MarkerType
): ClusterItem {
    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String? {
        return title
    }

    override fun getSnippet(): String? {
        return snippet
    }
}