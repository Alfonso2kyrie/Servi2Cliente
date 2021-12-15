package com.example.servi2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.servi2.chat.ChatActivity
import com.example.servi2.cliente.HomeClienteScreen
import com.example.servi2.coneccion.ConexionBD
import com.example.servi2.usuario.model.Orden
import com.example.servi2.usuario.model.Tecnico
import com.example.servi2.usuario.model.Trayecto
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.android.synthetic.main.activity_tecnico.*


class TecnicoActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var idorden2: String
    val handler = Handler()
    private lateinit var coor:LatLng
    private lateinit var map: GoogleMap
    lateinit var token:String
    private var status:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tecnico)

        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        idorden2 = sharedPreferences.getLong("idOrdenActiva", 0).toString()
        var idServidor = sharedPreferences.getString("idTecnico","")
        print("Holaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa $idServidor")
        createFragment()

        handler.postDelayed(object : Runnable {
            override fun run() {
                print("buscando")
                // función a ejecutar
                statusCancelar(idorden2) // función para refrescar la ubicación del conductor, creada en otra línea de código
                handler.postDelayed(this, 2000)
            }
        }, 2000)

        if(!status) {
            handler.postDelayed(object : Runnable {
                override fun run() {
                    print("buscando")
                    // función a ejecutar
                    trajecto() // función para refrescar la ubicación del conductor, creada en otra línea de código
                    handler.postDelayed(this, 2000)
                }
            }, 2000)


            val apiInterface = Apis.create().miTecnico(idServidor.toString())
            doAsync {
                apiInterface.enqueue(object : Callback<Tecnico> {
                    override fun onResponse(call: Call<Tecnico>, response: Response<Tecnico>) {
                        txtNombreTex.setText("${response.body()?.nombre} ${response.body()?.apellidoPaterno}")
                        txtModelo.setText("${response.body()?.modelo}")
                        txtColor.setText("${response.body()?.color}")
                        token = response.body()?.token.toString()
                    }

                    override fun onFailure(call: Call<Tecnico>, t: Throwable) {
                        Log.d("errorTecnico", t.message.toString())
                       Toast.makeText(this@TecnicoActivity, "error al obtener los datos", Toast.LENGTH_LONG).show()
                    }

                })
            }

            btnChat.setOnClickListener {
                val char = Intent(this, ChatActivity::class.java)
                char.putExtra("token", token)
                startActivity(char)
            }
            var btnCancelarO = findViewById<Button>(R.id.btnCancelar)
            btnCancelarO.setOnClickListener {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("¿Desea confirmar esta ubicacion?")
                    .setMessage("Esta apunto de poner su orden en marcha")
                    .setNegativeButton("Cancelar") { view, _ ->
                        Toast.makeText(this, "Cancel button pressed", Toast.LENGTH_SHORT).show()
                        view.dismiss()
                    }
                    .setPositiveButton("Aceptar") { view, _ ->

                        val apiInterface = Apis.create().cancelarOrden(idorden2.toLong())
                        doAsync {
                            apiInterface.enqueue(object : Callback<Void> {
                                override fun onResponse(
                                    call: Call<Void>,
                                    response: Response<Void>
                                ) {
                                    print("cancelada")
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    AlertDialog.Builder(this@TecnicoActivity).apply {
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
        }else{
            Log.d("objetoooooooooo3", "Orden ya esta canceladaaaaaaaaaaaaaaaaaaaaaaaa")
        }


    }
    private fun createFragment(){
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map2)as SupportMapFragment
        mapFragment.getMapAsync(this@TecnicoActivity)
    }

    override fun onMapReady(googleMap: GoogleMap?)  {
        map = googleMap!!
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)

        }
        map.uiSettings.isZoomControlsEnabled=true
    }

    fun trajecto(){

        val apiInterface = Apis.create().trayecto(idorden2)
        if (apiInterface != null) {
            doAsync {
                Log.d("objetoooooooooo3", "entreeee")
                Log.d("objetoooooooooo3", "$idorden2")
                apiInterface.enqueue(object : Callback<Trayecto> {
                    override fun onResponse(call: Call<Trayecto>, response: Response<Trayecto>) {

                        if(response.body()?.status_idStatus?.toInt()  == 3){
                            coor = LatLng(response.body()!!.latitud.toDouble(), response.body()!!.longitud.toDouble())
                            map.clear()
                            map.addMarker(
                                MarkerOptions()
                                    .position(coor)
                                    .title("Conductor")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.carro2))
                            )
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(coor, 16f))


                        }else{
                            handler.removeMessages(0)
                        }
                    }
                    override fun onFailure(call: Call<Trayecto>, t: Throwable) {
                        print("$t")
                    }

                })
            }
        }
    }

    fun statusCancelar(orden:String){
        val apiInterface = Apis.create().statusCancelar(orden)
        doAsync {
            Log.d("objetoooooooooo3", "entreeee al cancelar")
            Log.d("objetoooooooooo3", "$idorden2")
            apiInterface.enqueue(object : Callback<Orden> {
                override fun onResponse(call: Call<Orden>, response: Response<Orden>) {
                    Log.d("objetoooooooooo3", "${response.body()!!.status_idstatus}")
                    if(response.body()?.status_idstatus  == 1){
                        status = true
                        Log.d("objetoooooooooo3", "ya puse el true")
                        handler.removeMessages(0)
                        val con = ConexionBD(this@TecnicoActivity)
                        con.deleteData("chat")
                        AlertDialog.Builder(this@TecnicoActivity).apply {
                            setTitle("Orden Cancelada")
                            setMessage("Lo sentimos orden cancelada")
                            setNegativeButton("ok") { view, _ ->
                                val sesion: SharedPreferences.Editor =
                                    sharedPreferences.edit()
                                sesion.putLong("idOrdenActiva", 0)
                                sesion.apply()
                                val intent = Intent(
                                    this@TecnicoActivity,
                                    HomeClienteScreen::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        }.show()


                        handler.removeMessages(0)

                    }else if(response.body()?.status_idstatus  == 2){
                        val con = ConexionBD(this@TecnicoActivity)
                        con.deleteData("chat")
                        AlertDialog.Builder(this@TecnicoActivity).apply {
                            setTitle("Orden Finalizada")
                            setMessage("La orden a sido atendida")
                            setNegativeButton("ok") { view, _ ->
                                handler.removeMessages(0)
                                val sesion: SharedPreferences.Editor =
                                    sharedPreferences.edit()
                                sesion.putLong("idOrdenActiva", 0)
                                sesion.apply()
                                val intent = Intent(
                                    this@TecnicoActivity,
                                    HomeClienteScreen::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        }.show()

                    }
                }
                override fun onFailure(call: Call<Orden>, t: Throwable) {
                    print("$t")
                }

            })
        }
    }
}


