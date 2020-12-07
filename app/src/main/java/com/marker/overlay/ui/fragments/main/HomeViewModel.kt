package com.marker.overlay.ui.fragments.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.marker.overlay.data.MapsRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val mapsRepository: MapsRepository
) : ViewModel() {

    private val _locations: MutableLiveData<List<LatLng>> = MutableLiveData()
    val locations: LiveData<List<LatLng>> = _locations

    fun getSomeLocations(myLatLng: LatLng) {
        viewModelScope.launch {
            val result = mapsRepository.fetchSomeNearbyLocations(myLatLng)
            _locations.postValue(result)
        }
    }

}