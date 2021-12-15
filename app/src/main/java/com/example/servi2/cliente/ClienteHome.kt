package com.example.servi2.cliente

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import com.example.servi2.Apis
import com.example.servi2.R
import com.example.servi2.SaveUbicacion
import com.example.servi2.usuario.model.Servicio
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClienteHome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_home)

        val apiInterface = Apis.create().getServicios()

        if (apiInterface != null) {
            doAsync {
                apiInterface.enqueue(object : Callback<ArrayList<Servicio >> {

                    override fun onResponse(
                        call: Call<ArrayList<Servicio>>,
                        response: Response<ArrayList<Servicio>>
                    ) {
                        val servicios = response?.body()
                        if (servicios != null) {
                            servicios.forEach {
                                println("Price of book, ${it.tipoServicio}")
                            }
                        }

                        System.out.println( response.body())
                        var miAdapter = ServicioAdapter(this@ClienteHome, R.layout.item_rv_servicio, response.body()!!)
                        findViewById<ListView>(R.id.listaServicios).adapter= miAdapter
                        miAdapter!!.notifyDataSetChanged()
                        findViewById<ListView>(R.id.listaServicios).setOnItemClickListener(
                            AdapterView.OnItemClickListener { parent, view, position, id ->
                            val intent = Intent(this@ClienteHome, SaveUbicacion::class.java)
                                intent.putExtra("tipoServicio", response.body()!!.get(position).tipoServicio)
                                intent.putExtra("idServicio", response.body()!!.get(position).idServicio)
                                intent.putExtra("imagen", response.body()!!.get(position).imagenServicio)
                                Log.d("iddddddddddddddd",
                                    response.body()!!.get(position).idServicio
                                )
                                startActivity(intent)
                            })

                    }

                    override fun onFailure(call: Call<ArrayList<Servicio>>, t: Throwable) {
                        AlertDialog.Builder(this@ClienteHome).apply {
                            setTitle("Error en los datos")
                            setMessage("¡Upss hubo un error en el envio!")
                            setNegativeButton("ok", null)
                        }.show()
                    }
                })
            }
        }
        
    }
}