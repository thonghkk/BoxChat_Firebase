package com.example.boxchat.ui.main.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.Firebase.Companion.firebaseDatabase
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.model.LatLngMap
import com.example.boxchat.model.MapLocation
import com.example.boxchat.model.User
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

const val REQUEST_CODE = 101

class MapsActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var client: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMapViewModel: MapModel
    private lateinit var mMap: GoogleMap
    private val mUserLocation = mutableListOf<MapLocation>()

    override fun getLayoutID() = R.layout.activity_maps
    override fun onCreateActivity(savedInstanceState: Bundle?) {

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        client = LocationServices.getFusedLocationProviderClient(this)
        mMapViewModel = ViewModelProvider(this).get(MapModel::class.java)

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


    private fun showUserOnMap():List<MapLocation> {
        var mMapLocation = mUserLocation

        mMapViewModel.friendRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mUserLocation.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val mLocation = dataSnapShot.getValue(MapLocation::class.java)
                    if (mLocation?.userId != auth.uid) {
                        mUserLocation.add(mLocation!!)
                    }
                }
                 mMapLocation = mUserLocation
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return mMapLocation
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val mLatLng = mutableListOf<LatLngMap>()
        mMap = googleMap!!

        


//        mMap.setOnMapLoadedCallback {
//            GoogleMap.OnMapLoadedCallback {
//                for (i in showUserOnMap()){
//                    mLatLng.add(LatLngMap(i.latitude,i.longitude))
//                    for (j in mLatLng){
//                        mMap.addMarker(
//                            MarkerOptions().position(j)
//                        )
//                    }
//
//                }
//
//            }
//        }
    }


}