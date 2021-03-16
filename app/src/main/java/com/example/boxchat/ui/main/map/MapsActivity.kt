package com.example.boxchat.ui.main.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.Firebase.Companion.firebaseDatabase
import com.example.boxchat.commom.Firebase.Companion.user
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

const val REQUEST_CODE = 101

class MapsActivity : BaseActivity() {
    private lateinit var client: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment


    override fun getLayoutID() = R.layout.activity_maps
    override fun onCreateActivity(savedInstanceState: Bundle?) {

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        client = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        // get the last known location of a user's device
        val task = client.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                mapFragment.getMapAsync { googleMap ->
                    //get location
                    val latLng = LatLng(location.latitude, location.longitude)
                    Log.d("Location", "${location.latitude}")
                    val markerOptions = MarkerOptions().position(latLng).title("You Here")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_avatar))
                        .flat(true)

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11F))
                    googleMap.addMarker(markerOptions).showInfoWindow()

                    //firebase
                    val userId = user?.uid
                    val ref = firebaseDatabase.getReference("driverAvailable")

                    // library Geo
                    val geoFire = GeoFire(ref)
                    geoFire.setLocation(
                        userId,
                        GeoLocation(location.latitude, location.longitude),
                        GeoFire.CompletionListener { key, error ->
                            if (error != null) {
                                Toast.makeText(this, "Can't go Active", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(this, "You are Active", Toast.LENGTH_SHORT).show();
                        })
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

//    override fun onStop() {
//        super.onStop()
//        //firebase
//        val userId = user?.uid
//        val ref = firebaseDatabase.getReference("driverAvailable")
//        // library Geo
//        val geoFire = GeoFire(ref)
//        geoFire.removeLocation(userId)
//    }
}