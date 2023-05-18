package dev.cisnux.mystory.ui

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import dev.cisnux.mystory.R
import dev.cisnux.mystory.databinding.FragmentStoryLocationBinding
import dev.cisnux.mystory.domain.StoryMap
import dev.cisnux.mystory.viewmodels.StoryLocationViewModel
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class StoryLocationFragment : Fragment() {
    private var _binding: FragmentStoryLocationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StoryLocationViewModel by viewModels()
    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var googleMap: GoogleMap
    private val resolutionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    Log.i(TAG, "onActivityResult: All location settings are satisfied.")
                }

                Activity.RESULT_CANCELED -> {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.turn_on_gps),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLocation()
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireActivity(),
                R.raw.map_style
            )
        )
        viewModel.storyMaps.observe(viewLifecycleOwner, ::showStoryLocations)
        getMyLocation()
    }

    private fun showStoryLocations(stories: List<StoryMap>) {
        if (stories.isNotEmpty()) {
            stories.forEach {
                if (it.lat != null && it.lon != null) {
                    val latLng = LatLng(it.lat, it.lon)
                    googleMap.addMarker(MarkerOptions().apply {
                        icon(BitmapDescriptorFactory.fromBitmap(it.photo))
                        position(latLng)
                        getAddressName(it.lat, it.lon) { address ->
                            snippet(address)
                        }
                        title(it.username)
                    })
                    boundsBuilder.include(latLng)
                }
            }
            val bounds = boundsBuilder.build()
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

    private fun getAddressName(latitude: Double, longitude: Double, onAddress: (String?) -> Unit) {
        val geoCoder = Geocoder(requireActivity(), Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geoCoder.getFromLocation(latitude, longitude, 1) {
                onAddress(it.firstOrNull()?.getAddressLine(0))
                return@getFromLocation
            }
        }
        try {
            @Suppress("DEPRECATION") val geoList = geoCoder.getFromLocation(latitude, longitude, 1)
            onAddress(geoList?.firstOrNull()?.getAddressLine(0))
        } catch (e: IOException) {
            Log.e(TAG, e.stackTraceToString())
        }
    }

    private fun getMyLocation() {
        if (arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).all { permission ->
                ContextCompat.checkSelfPermission(
                    requireActivity(), permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        ) {
            val locationRequest = LocationRequest.Builder(
                TimeUnit.SECONDS.toMillis(1)
            ).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                setMaxUpdateDelayMillis(TimeUnit.SECONDS.toMillis(1))
            }.build()
            val locationSettingsBuilder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            val client = LocationServices.getSettingsClient(requireActivity())
            client.checkLocationSettings(locationSettingsBuilder.build()).addOnSuccessListener {
                googleMap.isMyLocationEnabled = true
            }.addOnFailureListener { exception ->
                googleMap.isMyLocationEnabled = false
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Log.e(TAG, sendEx.stackTraceToString())
                    }
                }
            }
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryLocationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val TAG = StoryLocationFragment::class.simpleName
    }
}