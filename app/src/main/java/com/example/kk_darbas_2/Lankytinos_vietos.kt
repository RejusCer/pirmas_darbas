package com.example.kk_darbas_2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.example.kk_darbas_2.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray

class Lankytinos_vietos : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var locations = arrayListOf<locations>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lankytinos_vietos)

        var btn_lank = findViewById<Button>(R.id.lank)

        btn_lank.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }


        var jsonString = assets.open("lankytinos.json").bufferedReader().use { it.readText() }
        var jsonArray = JSONArray(jsonString)
        for (i in 0..jsonArray.length() - 1){
            val jsonObj = jsonArray.getJSONObject(i)
            locations += locations(jsonObj.getString("latitude"), jsonObj.getString("longtitude"))
        }


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        for (i in 0..locations.size - 1){
            val marker = LatLng(locations[i].latitude.toDouble(), locations[i].longtitude.toDouble())
            mMap.addMarker(MarkerOptions().position(marker).title("lankytina: $i"))
        }

        mMap.setOnMarkerClickListener(this)
        addMyLocation()
    }

    private fun addMyLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null){
                lastLocation = location
                var currentLatLong = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(currentLatLong).title("tavo vieta $currentLatLong"))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
            }
        }
    }

    override fun onMarkerClick(p0: Marker) = false
}