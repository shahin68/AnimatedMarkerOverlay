package com.marker.overlay.ui.fragments.cluster.data

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.marker.overlay.R


class MarkerClusterRenderer(
    context: Context?, map: GoogleMap,
    clusterManager: ClusterManager<ClusterItem>,
    private val block: (Any?) -> Unit,
) : DefaultClusterRenderer<ClusterItem>(context, map, clusterManager) {

    private var lastClusterSize = 0

    override fun onBeforeClusterRendered(cluster: Cluster<ClusterItem>, markerOptions: MarkerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions)
        markerOptions.anchor(0.5f, 0.5f)
    }

    override fun onClusterUpdated(cluster: Cluster<ClusterItem>, marker: Marker) {
        super.onClusterUpdated(cluster, marker)
        marker.setAnchor(0.5f, 0.5f)
    }

    override fun onBeforeClusterItemRendered(item: ClusterItem, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)
        markerOptions.icon(
            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)
        ).anchor(0.5f, 0.5f)
    }


    override fun onClusterItemUpdated(item: ClusterItem, marker: Marker) {
        super.onClusterItemUpdated(item, marker)
        marker.setAnchor(0.5f, 0.5f)

        if (marker.isVisible) {
            block.invoke(marker)
        }
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ClusterItem>): Boolean {
        return cluster.size > 2
    }

    override fun onClusterItemRendered(clusterItem: ClusterItem, marker: Marker) {
        super.onClusterItemRendered(clusterItem, marker)
        if (marker.isVisible) {
            block.invoke(clusterItem)
        }
    }

    override fun onClustersChanged(clusters: MutableSet<out Cluster<ClusterItem>>?) {
        super.onClustersChanged(clusters)
        if (clusters?.size != lastClusterSize) {
            block.invoke(null)
        }
        lastClusterSize = clusters?.size ?: 0
    }

}