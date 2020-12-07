package com.marker.overlay.ui.fragments.cluster

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.marker.overlay.R
import com.marker.overlay.data.models.MarkerType
import com.marker.overlay.databinding.ClusterFragmentBinding
import com.marker.overlay.ui.fragments.BaseFragment
import com.marker.overlay.ui.fragments.cluster.data.ClusterMarker
import com.marker.overlay.ui.fragments.cluster.data.MarkerClusterRenderer
import com.marker.overlay.ui.fragments.home.FINE_LOCATION_PERMISSION
import com.marker.overlay.ui.fragments.home.HomeViewModel
import com.shahin.overlay.Projection
import org.koin.androidx.viewmodel.ext.android.viewModel


class ClusterFragment : BaseFragment<ClusterFragmentBinding>(R.layout.cluster_fragment),
    OnMapReadyCallback {

    private val viewModel: HomeViewModel by viewModel()

    private lateinit var mMap: GoogleMap

    private lateinit var clusterManager: ClusterManager<ClusterItem>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ClusterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.cluster_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        initObservers()

        binding.canvasBtn.setOnClickListener {
            fetchNewLocations(MarkerType.CANVAS_DRAW)
        }

        binding.bitmapBtn.setOnClickListener {
            fetchNewLocations(MarkerType.CANVAS_BITMAP)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            mMap = it

            mMap.uiSettings.apply {
                this.isMyLocationButtonEnabled = true
                this.isMapToolbarEnabled = false
            }

            fetchNewLocations(MarkerType.CANVAS_DRAW)
        }
    }

    private fun setUpCluster(latLng: LatLng, markerType: MarkerType) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
        clusterManager = ClusterManager(context, mMap)
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)
        viewModel.getSomeCluster(latLng, markerType)

        val clusterRenderer = MarkerClusterRenderer(requireContext(), mMap, clusterManager) {
            when (it) {
                null -> {
                    when (markerType) {
                        MarkerType.CANVAS_DRAW -> binding.canvasOverlay.clear()
                        MarkerType.CANVAS_BITMAP -> binding.bitmapOverlay.clear()
                    }
                }
                is ClusterItem -> {
                    when (markerType) {
                        MarkerType.CANVAS_DRAW -> binding.canvasOverlay.set(
                            Projection(
                                mMap.projection.toScreenLocation(
                                    it.position
                                ), it.position
                            )
                        )
                        MarkerType.CANVAS_BITMAP -> binding.bitmapOverlay.set(
                            Projection(
                                mMap.projection.toScreenLocation(
                                    it.position
                                ), it.position
                            )
                        )
                    }
                }
                is Marker -> {
                    when (markerType) {
                        MarkerType.CANVAS_DRAW -> binding.canvasOverlay.move(
                            Projection(
                                mMap.projection.toScreenLocation(
                                    it.position
                                ), it.position
                            )
                        )
                        MarkerType.CANVAS_BITMAP -> binding.bitmapOverlay.move(
                            Projection(
                                mMap.projection.toScreenLocation(
                                    it.position
                                ), it.position
                            )
                        )
                    }
                }
            }
        }
        clusterManager.renderer = clusterRenderer
    }

    private fun initObservers() {
        viewModel.clusterLocations.observe(viewLifecycleOwner, { locations ->
            if (isAdded && ::mMap.isInitialized) {
                locations.forEach {
                    clusterManager.addItem(it)
                }
                clusterManager.cluster()


                mMap.setOnCameraMoveListener {
                    locations.forEach {
                        val projection = Projection(
                            mMap.projection.toScreenLocation(it.position),
                            it.position
                        )
                        when (it.markerType) {
                            MarkerType.CANVAS_DRAW -> {
                                binding.canvasOverlay.move(
                                    projection
                                )
                            }
                            MarkerType.CANVAS_BITMAP -> {
                                binding.bitmapOverlay.move(
                                    projection
                                )
                            }
                        }
                    }
                }
            }
        })
    }

    private fun fetchNewLocations(markerType: MarkerType) {
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
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                FINE_LOCATION_PERMISSION
            )
            return
        }
        if (isAdded) {
            binding.canvasOverlay.clear()
            binding.bitmapOverlay.clear()
            if (::mMap.isInitialized) {
                mMap.clear()
                mMap.isMyLocationEnabled = true
                getCurrentLocation()?.let { latLng ->
                    setUpCluster(latLng, markerType)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fetchNewLocations(MarkerType.CANVAS_DRAW)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): LatLng? {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
        binding.bitmapOverlay.start()
    }

    override fun onStop() {
        super.onStop()
        binding.canvasOverlay.pause()
        binding.bitmapOverlay.pause()
    }
}