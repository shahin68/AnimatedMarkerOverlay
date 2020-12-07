package com.marker.overlay.ui.fragments.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.marker.overlay.R
import com.marker.overlay.databinding.HomeFragmentBinding
import com.marker.overlay.ui.fragments.BaseFragment
import com.shahin.overlay.Projection
import org.koin.androidx.viewmodel.ext.android.viewModel


const val FINE_LOCATION_PERMISSION = 100
class HomeFragment : BaseFragment<HomeFragmentBinding>(R.layout.home_fragment), OnMapReadyCallback {

    private val viewModel: HomeViewModel by viewModel()

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as? SupportMapFragment
        mapFragment?.getMapAsync(this)


        viewModel.locations.observe(viewLifecycleOwner, { locations ->
            if (isAdded && ::mMap.isInitialized) {
                locations.forEach {
                    val marker = mMap.addMarker(
                        MarkerOptions().position(it).anchor(0.5f, 0.5f).zIndex(111000f).icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)
                        )
                    )
                    if (marker.isVisible) {
                        val projection = Projection(
                            mMap.projection.toScreenLocation(it),
                            it
                        )
                        binding.canvasOverlay.set(
                            projection
                        )
                    }
                }
                mMap.setOnCameraMoveListener {
                    locations.forEach {
                        val projection = Projection(
                            mMap.projection.toScreenLocation(it),
                            it
                        )
                        binding.canvasOverlay.move(
                            projection
                        )
                    }
                }
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            mMap = it

            mMap.uiSettings.apply {
                this.isMyLocationButtonEnabled = true
                this.isMapToolbarEnabled = false
            }


            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    FINE_LOCATION_PERMISSION
                )
                return
            }

            mMap.isMyLocationEnabled = true

            getCurrentLocation()?.let { latLng ->
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                viewModel.getSomeLocations(latLng)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                if (::mMap.isInitialized) {
                    mMap.isMyLocationEnabled = true
                }

                getCurrentLocation()?.let { latLng ->
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                    viewModel.getSomeLocations(latLng)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): LatLng? {
        val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        return if (location != null) {
            LatLng(location.latitude, location.longitude)
        } else {
            null
        }
    }

    override fun onResume() {
        super.onResume()
        binding.canvasOverlay.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.canvasOverlay.pause()
    }
}