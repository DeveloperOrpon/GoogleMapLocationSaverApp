package com.example.myturmap

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myturmap.databinding.ActivityCreateMapBinding
import com.example.myturmap.model.Place
import com.example.myturmap.model.UserMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar


class CreateMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityCreateMapBinding
    private var markers: MutableList<Marker> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapFragment.view?.let {
            Snackbar.make(it,"Please Long Pless To Add Location!!",Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok",{})
                .setActionTextColor(ContextCompat.getColor(this,android.R.color.white))
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.saveMenuId){
            if (markers.isEmpty()){
                Toast.makeText(this@CreateMapActivity,"You Should Add At list One Location",Toast.LENGTH_LONG).show()
            }
            val places=markers.map { marker -> marker.title?.let { marker.snippet?.let { it1 ->
                Place(it,
                    it1,marker.position.latitude,marker.position.longitude)
            } } }
            var Title=getIntent().getStringExtra("title");
            val userMap= Title?.let { UserMap(it, places as List<Place>) }
            val data =Intent()
            data.putExtra("EXTRA_USER_MAP",userMap)
            setResult(Activity.RESULT_OK,data)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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
        mMap.setOnInfoWindowClickListener { Deletemarker->
            markers.remove(Deletemarker)
            Deletemarker.remove()
        }
        mMap.setOnMapLongClickListener { latlng->
            ShowAlertDialog(latlng)
        }

        val boundsBuilder= LatLngBounds.Builder()
        val location = LatLng(23.805374, 90.368405)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,8F))
    }

    private fun ShowAlertDialog(latlng: LatLng) {
        val placeFormView=LayoutInflater.from(this).inflate(R.layout.dialog_layout_map,null)
        val dialog=AlertDialog.Builder(this)
            .setView(placeFormView)
            .setNegativeButton("NO",null)
            .setPositiveButton("YES",null)
            .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            var title=placeFormView.findViewById<TextView>(R.id.titleId).text.toString()
            var dis=placeFormView.findViewById<TextView>(R.id.dicId).text.toString()
            if (title.trim().isEmpty() && dis.trim().isEmpty()){
                Toast.makeText(this@CreateMapActivity,"Title And Description Empty Does Not Allow",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            var marker=mMap.addMarker(MarkerOptions().position(latlng).title(title).snippet(dis))
            if (marker != null) {
                markers.add(marker)
            }
            dialog.dismiss()
        }
    }
}