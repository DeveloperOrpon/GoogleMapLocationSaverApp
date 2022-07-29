package com.example.myturmap

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myturmap.model.Place
import com.example.myturmap.model.UserMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

public const val MAP_EXTRA="MAP_EXTRA"
private const val REQUISTED_CODE =11
private const val DataFile="Local.Data"
class MainActivity : AppCompatActivity() {

    private lateinit var userData:MutableList<UserMap>
    private lateinit var mapdapter:MapAdapter
    private var locationPermissionGranted = false

    companion object{
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title ="Your Selected Places!!"
        userData=deSerializableUserDataMap(this).toMutableList()
        //layout manager
        rvMap.layoutManager=LinearLayoutManager(this)
        //adapter
        mapdapter=MapAdapter(this, userData,object : MapAdapter.OnClickListener{
            override fun onItemClick(position: Int) {
               var intent =Intent(this@MainActivity,DisplayMapActivity::class.java)
                intent.putExtra(MAP_EXTRA,userData[position])
                overridePendingTransition(R.anim.slide_out_right,R.anim.slide_in_left)
                startActivity(intent)
            }

        })
        rvMap.adapter=mapdapter

        //add location
        addBtn.setOnClickListener {
            ShowAlertDialog()
            getLocationPermission()
        }
    }

    private fun ShowAlertDialog() {
        val placeFormView= LayoutInflater.from(this).inflate(R.layout.activity_create_title,null)
        val dialog= AlertDialog.Builder(this)
            .setView(placeFormView)
            .setPositiveButtonIcon(this.resources.getDrawable(R.drawable.ok))
            .setNegativeButton("NO",null)
            .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            var title=placeFormView.findViewById<TextView>(R.id.mapTitleId).text.toString()
            if (title.trim().isEmpty()){
                Toast.makeText(this,"Title And Description Empty Does Not Allow",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            var intent=Intent(this@MainActivity,CreateMapActivity::class.java)
            intent.putExtra("title",title)
            startActivityForResult(intent,REQUISTED_CODE)
            dialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode== REQUISTED_CODE && resultCode== Activity.RESULT_OK){
            val userMap= data?.getSerializableExtra("EXTRA_USER_MAP") as UserMap
            userData.add(userMap)
            mapdapter.notifyItemChanged(userData.size-1)
            serializableUserDataMap(this, userData)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    ///Save Data local Database
    private fun getDataFile(context: Context):File{
        return File(context.dataDir, DataFile)
    }
    private fun serializableUserDataMap(context: Context,userMap: List<UserMap>){
            ObjectOutputStream(FileOutputStream(getDataFile(context))).use { it.writeObject(userMap) }
    }
    private fun deSerializableUserDataMap(context: Context): List<UserMap>{
        val dataFile=getDataFile(context)
        if (!dataFile.exists()){
            return emptyList()
        }
        ObjectInputStream(FileInputStream(dataFile)).use { return it.readObject() as List<UserMap> }
    }

    //end//


    private fun generateSampleData(): List<UserMap> {
        return listOf(
            UserMap(
                "Memories from University",
                listOf(
                    Place("Branner Hall", "Best dorm at Stanford", 37.426, -122.163),
                    Place("Gates CS building", "Many long nights in this basement", 37.430, -122.173),
                    Place("Pinkberry", "First date with my wife", 37.444, -122.170)
                )
            ),
            UserMap("January vacation planning!",
                listOf(
                    Place("Tokyo", "Overnight layover", 35.67, 139.65),
                    Place("Ranchi", "Family visit + wedding!", 23.34, 85.31),
                    Place("Singapore", "Inspired by \"Crazy Rich Asians\"", 1.35, 103.82)
                )),
            UserMap("Singapore travel itinerary",
                listOf(
                    Place("Gardens by the Bay", "Amazing urban nature park", 1.282, 103.864),
                    Place("Jurong Bird Park", "Family-friendly park with many varieties of birds", 1.319, 103.706),
                    Place("Sentosa", "Island resort with panoramic views", 1.249, 103.830),
                    Place("Botanic Gardens", "One of the world's greatest tropical gardens", 1.3138, 103.8159)
                )
            ),
            UserMap("My favorite places in the Midwest",
                listOf(
                    Place("Chicago", "Urban center of the midwest, the \"Windy City\"", 41.878, -87.630),
                    Place("Rochester, Michigan", "The best of Detroit suburbia", 42.681, -83.134),
                    Place("Mackinaw City", "The entrance into the Upper Peninsula", 45.777, -84.727),
                    Place("Michigan State University", "Home to the Spartans", 42.701, -84.482),
                    Place("University of Michigan", "Home to the Wolverines", 42.278, -83.738)
                )
            ),
            UserMap("Restaurants to try",
                listOf(
                    Place("Champ's Diner", "Retro diner in Brooklyn", 40.709, -73.941),
                    Place("Althea", "Chicago upscale dining with an amazing view", 41.895, -87.625),
                    Place("Shizen", "Elegant sushi in San Francisco", 37.768, -122.422),
                    Place("Citizen Eatery", "Bright cafe in Austin with a pink rabbit", 30.322, -97.739),
                    Place("Kati Thai", "Authentic Portland Thai food, served with love", 45.505, -122.635)
                )
            )
        )
    }
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

}
