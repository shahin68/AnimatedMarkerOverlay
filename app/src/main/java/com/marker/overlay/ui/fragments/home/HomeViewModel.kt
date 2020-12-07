package com.marker.overlay.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.marker.overlay.data.MapsRepository
import com.marker.overlay.data.models.AnimatedMarker
import com.marker.overlay.data.models.MarkerType
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

}