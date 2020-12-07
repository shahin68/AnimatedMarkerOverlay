package com.marker.overlay.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.marker.overlay.data.MapsRepository
import com.marker.overlay.data.models.AnimatedMarker
import com.marker.overlay.data.models.MarkerType
import com.marker.overlay.ui.fragments.cluster.data.ClusterMarker
import kotlinx.coroutines.launch

class HomeViewModel(
    private val mapsRepository: MapsRepository
) : ViewModel() {

    private val _locations: MutableLiveData<List<AnimatedMarker>> = MutableLiveData()
    val locations: LiveData<List<AnimatedMarker>> = _locations

    fun getSomeLocations(myLatLng: LatLng, markerType: MarkerType) {
        viewModelScope.launch {
            val result = mapsRepository.fetchSomeNearbyLocations(myLatLng)
            _locations.postValue(result.map { AnimatedMarker(it, markerType) })
        }
    }

    private val _clusterLocations: MutableLiveData<List<ClusterMarker>> = MutableLiveData()
    val clusterLocations: LiveData<List<ClusterMarker>> = _clusterLocations

    fun getSomeCluster(myLatLng: LatLng, markerType: MarkerType) {
        viewModelScope.launch {
            val result = mapsRepository.fetchSomeNearbyLocations(myLatLng)
            _clusterLocations.postValue(result.map { ClusterMarker(it, it.latitude.toString(), it.longitude.toString(), markerType) })
        }
    }
}