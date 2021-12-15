package com.example.servi2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.servi2.chat.MessageModel
import com.example.servi2.cliente.HomeClienteScreen
import com.example.servi2.usuario.model.Orden
import com.example.servi2.usuario.model.Usuario
import com.example.servi2.usuario.model.idOrden
import com.google.android.datatransport.runtime.util.PriorityMapping.toInt
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class EsperaTecnico : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var idorden2: String
    val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_espera_tecnico)
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        idorden2 = sharedPreferences.getLong("idOrdenActiva", 0).toString()

        handler.postDelayed(object : Runnable {
            override fun run() {
                print("buscando")
                // función a ejecutar
                esperando() // función para refrescar la ubicación del conductor, creada en otra línea de código
                handler.postDelayed(this, 2000)
            }
        }, 2000)

        var imageView = findViewById<ImageView>(R.id.idCarga);
        var uri = Uri.parse("https://acegif.com/wp-content/uploads/loading-9.gif");
        Glide.with(getApplicationContext()).load(uri).into(imageView)


        val btnCancelar = findViewById<Button>(R.id.btnCancelarOrden)
        btnCancelar.setOnClickListener {
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
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                AlertDialog.Builder(this@EsperaTecnico).apply {
                                    setTitle("Orden Cancelada")
                                    setMessage("Su Orden se cancelo correctamente")
                                    setNegativeButton("ok") { view, _ ->
                                        val sesion: SharedPreferences.Editor =
                                            sharedPreferences.edit()
                                        sesion.putLong("idOrdenActiva", 0)
                                        sesion.apply()
                                        val intent = Intent(
                                            this@EsperaTecnico,
                                            HomeClienteScreen::class.java
                                        )
                                        startActivity(intent)
                                        finish()
                                    }
                                }.show()
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                AlertDialog.Builder(this@EsperaTecnico).apply {
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

    fun esperando() {

        val apiInterface = Apis.create().espera(idorden2)

        if (apiInterface != null) {
            doAsync {
                Log.d("objetoooooooooo3", "entreeee")
                apiInterface.enqueue(object : Callback<Orden> {
                    override fun onResponse(call: Call<Orden>, response: Response<Orden>) {
                        print("${response.body()?.servidor_idPersona}")

                            if(response.body()?.servidor_idPersona  != 14){
                                val sesion : SharedPreferences.Editor = sharedPreferences.edit()
                                sesion.putString("idTecnico","${response.body()?.servidor_idPersona}")
                                sesion.apply()
                                handler.removeMessages(0)
                                val tecnico = Intent(this@EsperaTecnico, TecnicoActivity::class.java).apply {  }
                                startActivity(tecnico)
                                finish()
                            }

                    }

                    override fun onFailure(call: Call<Orden>, t: Throwable) {

                        Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_LONG)
                            .show()
                        Log.e("malo",t.toString())
                    }
                })
            }
        }
    }
}
