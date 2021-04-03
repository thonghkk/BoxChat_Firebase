package com.example.boxchat.ui.main.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.boxchat.R
import com.example.boxchat.base.BaseActivity
import com.example.boxchat.commom.Firebase
import com.example.boxchat.commom.Firebase.Companion.auth
import com.example.boxchat.commom.Firebase.Companion.user
import com.example.boxchat.model.MapLocation
import com.example.boxchat.model.User
import com.example.boxchat.ui.main.MainActivity
import com.example.boxchat.ui.main.users.StrangerFragment
import com.example.boxchat.ui.main.users.UserViewModel
import com.example.boxchat.ui.main.users.ViewStrangerActivity
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

const val REQUEST_CODE = 101

class MapsActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var client: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMapViewModel: MapModel
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var mMarker: Marker
    private var mUserLocation = mutableListOf<MapLocation>()
    private val userList = mutableListOf<User>()

    override fun getLayoutID() = R.layout.activity_maps
    override fun onCreateActivity(savedInstanceState: Bundle?) {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        client = LocationServices.getFusedLocationProviderClient(this)
        mMapViewModel = ViewModelProvider(this).get(MapModel::class.java)
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        //get Location of user
        getLocation()
        //getCurrentLocation()
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

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21F))
                    googleMap.addMarker(markerOptions).showInfoWindow()
                    //User #
                    onMapReady(googleMap)
                    //firebase
                    val userId = auth.uid
                    val ref2 = mMapViewModel.mUserOnReference.child(userId!!)
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["userId"] = userId
                    hashMap["latitude"] = location.latitude.toString()
                    hashMap["longitude"] = location.longitude.toString()
                    ref2.setValue(hashMap)
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

    private fun getLocation() {
        mMapViewModel.mUserOnReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val mLocation = dataSnapShot.getValue(MapLocation::class.java)
                    if (mLocation!!.userId != auth.uid) {
                        mUserLocation.add(mLocation)
                        mMapViewModel.addDriverAvailable(mUserLocation)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMapViewModel.driverAvailable.observe(this@MapsActivity, Observer { driver ->
            for (i in driver) {
                val location = LatLng(i.latitude.toDouble(), i.longitude.toDouble())
                mMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(i.userId)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_logo_chat))
                )
                mMarker.tag = 0
            }
        })
        mMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        try {
            val clickCount = marker?.tag as Int
            clickCount.let {
                val newClickCount = it + 1
                marker.tag = newClickCount

                mMapViewModel.mUser.observe(this, Observer { user ->
                    for (i in user) {
                        if (marker.title == i.userId) {
                            val intent = Intent(this, ViewStrangerActivity::class.java)
                            intent.putExtra("userId", i.userId)
                            intent.putExtra("userName", i.userName)
                            intent.putExtra("userImage", i.userProfileImage)
                            startActivity(intent)
                        }
                    }
                })
            }
        }catch (e:Exception){
            Toast.makeText(this,"That Are You !!!",Toast.LENGTH_SHORT).show()
        }

        return true
    }

}

