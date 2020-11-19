package com.saglamorhan.seyahatkitabim

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        /*
        val usak = LatLng(38.6729521, 29.3923981)
        mMap.addMarker(MarkerOptions().position(usak).title("Burası Uşak"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usak,15.0f))
        */
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location?) {

                if (location != null){
                    mMap.clear()
                    val kullaniciKonum = LatLng(location.latitude,location.longitude)
                    mMap.addMarker(MarkerOptions().position(kullaniciKonum).title("Şu an buradasınız."))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kullaniciKonum,15f))

                    //Konumun adresini almak
                    val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                    try {
                        val adresListesi = geocoder.getFromLocation(location.latitude,location.longitude,1)
                        if (adresListesi != null && adresListesi.size>0){
                            println(""+adresListesi.get(0).toString())
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //izin verilmemisse calisir
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
            val sonBilinenKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (sonBilinenKonum != null){

                val sonBilinenKonumLatLng = LatLng(sonBilinenKonum.latitude,sonBilinenKonum.longitude)
                mMap.addMarker(MarkerOptions().position(sonBilinenKonumLatLng).title("Buradasınız"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenKonumLatLng,15f))
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 1){
            if (grantResults.isNotEmpty()){
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
                }
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}