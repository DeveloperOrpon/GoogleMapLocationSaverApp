package com.example.myturmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.myturmap.databinding.ActivityDisplayMapBinding
import com.example.myturmap.model.UserMap
import com.google.android.gms.maps.model.LatLngBounds

class DisplayMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var userMap: UserMap
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDisplayMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userMap=intent.getSerializableExtra(MAP_EXTRA) as UserMap
        supportActionBar?.title= userMap.title


        binding = ActivityDisplayMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.i("Map","Map location ${userMap.title}")

        val boundsBuilder=LatLngBounds.Builder()
        for (place in userMap.Place){
            val location = LatLng(place.latitude, place.longtude)
            boundsBuilder.include(location)
            mMap.addMarker(MarkerOptions().position(location).title(place.title).snippet(place.discription))
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(),1000,1000,0))

    }
}