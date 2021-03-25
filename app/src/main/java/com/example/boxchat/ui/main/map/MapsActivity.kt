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
        //set User on map
        getUsersList()

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
                    mMapViewModel.users.observe(this, Observer { user ->
                        for (i in user) {
                            val hashMap: HashMap<String, String> = HashMap()
                            hashMap["userId"] = userId
                            hashMap["userName"] = i.userName
                            hashMap["latitude"] = location.latitude.toString()
                            hashMap["longitude"] = location.longitude.toString()
                            ref2.setValue(hashMap)
                        }
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

    private fun getUsersList() {
        mUserViewModel.databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val mUser = dataSnapShot.getValue(User::class.java)
                    if (mUser!!.userId == user?.uid) {
                        userList.add(mUser)
                        mMapViewModel.addListUser(userList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapsActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMapViewModel.driverAvailable.observe(this@MapsActivity, Observer { driver ->
            for (i in driver) {
                val a = LatLng(i.latitude.toDouble(), i.longitude.toDouble())
               mMap.addMarker(
                    MarkerOptions()
                        .position(a)
                        .title(i.userName)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_logo_chat))
                ).showInfoWindow()

//                mMap.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
//                    override fun onMarkerClick(p0: Marker?): Boolean {
//
//                        mMapViewModel.users.observe(mapFragment, Observer { user ->
//                            for (j in user) {
//                                if (i.userId == j.userId) {
//                                    val intent =
//                                        Intent(this@MapsActivity, ViewStrangerActivity::class.java)
//                                    intent.putExtra("userId", i.userId)
//                                    intent.putExtra("userName", i.userName)
//                                    intent.putExtra("userImage", i.userProfileImage)
//                                    startActivity(intent)
//                                }
//                            }
//                        })
//
//                        return true
//                    }
//
//                })
            }
        })
        mMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(maker: Marker?): Boolean {
        val a = maker!!.title
        val intent = Intent(this,StrangerFragment::class.java)
        intent.putExtra("name",a)
        startActivity(intent)
        return false
     }


}

