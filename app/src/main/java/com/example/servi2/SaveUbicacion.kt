package com.example.servi2

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.util.Log.INFO
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import com.example.servi2.usuario.model.Orden
import com.example.servi2.usuario.model.Usuario
import com.example.servi2.usuario.model.idOrden
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level.INFO

class SaveUbicacion : AppCompatActivity(), OnMapReadyCallback {
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var map: GoogleMap
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var coor: LatLng
    private val AUTOCOMPLETE_REQUEST_CODE = 2
    var idser = ""
    var idUser = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_ubicacion)
        idser = intent.getSerializableExtra("idServicio").toString()
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        idUser = sharedPreferences.getString("idPersona", "").toString()
        createFragment()

        Toast.makeText(this, intent.getStringExtra("tipoServicio").toString(), Toast.LENGTH_LONG)
            .show()

        Places.initialize(applicationContext, getString(R.string.google_maps_key))

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        var btnUbiLook = findViewById<Button>(R.id.btnLookUbi)
        btnUbiLook.setOnClickListener {
            autoComplete()
        }
        var btnActual = findViewById<Button>(R.id.btnUbiActual)
        btnActual.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setTitle("¿Desea confirmar esta ubicacion?")
                .setMessage("Esta apunto de poner su orden en marcha")
                .setNegativeButton("Cancelar") { view, _ ->
                    Toast.makeText(this, "Cancel button pressed", Toast.LENGTH_SHORT).show()
                    view.dismiss()
                }
                .setPositiveButton("Aceptar") { view, _ ->
                    val sdf = SimpleDateFormat("dd/M/yyyy")
                    val currentDate = sdf.format(Date())
                    System.out.println("usuariooooooooooooooo " + idUser)
                    val orden = Orden(
                        "0",
                        idser,
                        3,
                        "",
                        "",
                        currentDate,
                        coor.latitude.toString(),
                        coor.longitude.toString(),
                        idUser,
                        14
                    )
                    val apiInterface = Apis.create().saveOrden(orden)
                    doAsync {
                        apiInterface.enqueue(object : Callback<idOrden> {
                            override fun onResponse(
                                call: Call<idOrden>,
                                response: Response<idOrden>
                            ) {
                                Log.d("idOrdeeeeeeeen", "entre")
                                val id = response.body()
                                if (id != null) {
                                    Log.d("idOrdeeeeeeeen", id.lastID.toString())
                                }
                                val sesion: SharedPreferences.Editor = sharedPreferences.edit()
                                sesion.putLong("idOrdenActiva", id!!.lastID)
                                sesion.apply()
                                AlertDialog.Builder(this@SaveUbicacion).apply {
                                    setTitle("Orden Exitosa")
                                    setMessage("Su orden ha sido creada, espere tecnico")
                                    setNegativeButton("ok") { view, _ ->
                                        var intent =
                                            Intent(this@SaveUbicacion, EsperaTecnico::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }.show()

                            }

                            override fun onFailure(call: Call<idOrden>, t: Throwable) {
                                AlertDialog.Builder(this@SaveUbicacion).apply {
                                    setTitle("Error en los datos")
                                    setMessage("¡Upss hubo un error en el envio! ${t}")
                                    setNegativeButton("ok", null)
                                }.show()
                            }
                        })
                    }
                }
                .setCancelable(false)
                .create()

            dialog.show()

        }

    }

    private fun createFragment() {
        val mapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap!!
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )

        }

        map.isMyLocationEnabled = true
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true

        fusedLocation.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val ubicacion = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 16f))
                coor = ubicacion
                Log.d("miubicacion", coor.longitude.toString())
            }
        }
    }


    private fun autoComplete() {


        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        val intent =
            Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("prueba", "si entre al activityResult")
        Log.e("prueba2", AUTOCOMPLETE_REQUEST_CODE.toString())
        if (requestCode == 2) {
            Log.e("resultCode", resultCode.toString())
            Log.e("Activity Resul ok", Activity.RESULT_OK.toString())
            when (resultCode) {


                Activity.RESULT_OK -> {

                    data?.let {
                        Log.e("result ok", "entre result ok")
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(TAG, "Place: ${place.name}, ${place.id}")
                        System.out.println("usuariooooooooooooooo " + idUser)

                        val direccion = place.latLng
                        map.addMarker(
                            MarkerOptions()
                                .position(direccion)
                                .title("Marker in Sydney")
                        )
                        map.moveCamera(CameraUpdateFactory.newLatLng(direccion))
                        coor = direccion
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        Log.e("result error", "entre result error")
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage.toString())
                    }
                }
                Activity.RESULT_CANCELED -> {
                    Log.e("cancel", "Entro al cancel")
                }
                else -> {
                    Log.e("prueba3", "Entro en el else")
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}