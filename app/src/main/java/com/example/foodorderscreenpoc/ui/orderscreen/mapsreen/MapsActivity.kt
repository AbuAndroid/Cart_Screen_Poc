package com.example.foodorderscreenpoc.ui.orderscreen.mapsreen

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.foodorderscreenpoc.R
import com.example.foodorderscreenpoc.databinding.ActivityMapsBinding
import com.example.foodorderscreenpoc.repository.AddressFinderRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val mapsViewModel: MapsViewModel by viewModel()

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var latLng: LatLng
    private lateinit var geocoder: Geocoder
    private lateinit var marker: Marker


    private val requestLocationPermission by lazy {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        requestLocationPermission.launch(ACCESS_FINE_LOCATION)

        val bundle = intent.extras
        val title = bundle?.getString("Title")

        binding.uiTvLocationTitle.text = title

        mapsViewModel.currentAddress.observe(this) {
            binding.uiTvLocationName.text = it
            binding.uiEtStreetName.text = Editable.Factory.getInstance().newEditable(it)
        }

        geocoder = Geocoder(this, Locale.getDefault())
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setUpListeners()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission.launch(ACCESS_FINE_LOCATION)
        } else {

            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLang = LatLng(location.latitude, location.longitude)
                    mapsViewModel.getCurrentLocation(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLang, 18f))
                }
            }

            mMap.setOnMapClickListener { point ->
                latLng = point
                var addresses: MutableList<Address>? = mutableListOf()
                try {
                    addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val address = addresses?.get(0)
                val customPlaces = address?.latitude?.let {
                    AddressFinderRepository.CurrentPlaceModel(
                        latitude = it,
                        longitude = address.longitude,
                        address = address.getAddressLine(0),
                        locality = address.locality,
                        subLocality = address.subLocality,
                        adminArea = address.adminArea,
                        city = address.locality,
                        state = address.adminArea,
                        country = address.countryName,
                        postalCode = address.postalCode
                    )
                }

                binding.uiTvLocationName.text = customPlaces?.address

                marker = mMap.addMarker(
                    MarkerOptions().position(point).title("Marker")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )!!
            }
        }
    }

    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                address = addresses[0]
                addressText = address.toString()
            }
            Log.e("address", addressText)
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }
        return addressText
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOption = MarkerOptions().position(location)
        val titleStr = getAddress(location)
        markerOption.title(titleStr)

        mMap.addMarker(markerOption)
    }

    private fun setUpListeners() {
        binding.uiBtConfirm.setOnClickListener {

            val bundle = intent.extras
            if (bundle != null) {
                if (bundle.getString("Title") == "SelectDeliveryUpLocation") {
                    intent.putExtra("selectedLocation", binding.uiTvLocationName.text)
                    setResult(100, intent)
                    finish()
                } else {
                    Log.e("title2", intent.getStringExtra("Title").toString())
                    intent.putExtra("selectedLocation", binding.uiTvLocationName.text)
                    setResult(200, intent)
                    finish()
                }
            }

        }
    }
}


