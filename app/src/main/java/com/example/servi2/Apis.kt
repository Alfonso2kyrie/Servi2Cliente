package com.example.servi2

import android.service.autofill.UserData
import com.example.servi2.chat.MessageModel
import com.example.servi2.usuario.model.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*


interface Apis {
    @GET("/test")
    fun listaUsuario(): ArrayList<Usuario>

    @POST("usuario/create")
    fun createUser(@Body Usuario: Usuario?): Call<Usuario>?

    @POST("login")
    fun iniciarSesion(@Body usuario:Login?): Call<Usuario>?

    @PUT("usuario/update")
    fun actualizarUsuario(@Body usuario: Perfil): Call<Void>

    @DELETE("usuario/delete/{id}")
    fun eliminarUsuario(@Path("id") idUsuario: Int): Call<String>

    @GET("servicios")
    fun getServicios(): Call<ArrayList<Servicio>>

    @GET("misServicios/{id}")
    fun oneServicio(@Path("id") idServicio: Int): Call<Servicio>

    @POST("misServicios/save/{id}")
    fun saveServicio(@Path("id") idUsuario: Int, @Body idServicio: Int): Call<String>

    @DELETE("misServicios/delete/1")
    fun deleteServicio(@Path("id") idUsuario: Int, @Body idServicio: Int): Call<String>

    @POST("orden/save")
    fun saveOrden(@Body orden: Orden):Call<idOrden>

    @GET("orden/get/{id}")
    fun oneOrden(@Path("id") idOrden:Int): Call<Orden>

    @PUT("orden/asignar/{id}")
    fun asignarOrden(@Path("id") idUsuario: Int, @Body orden: Orden):Call<Void>

    @GET("orden/historial/{id}")
    fun historialUsuario(@Path("id")idUsuario: String): Call<List<OrdenHistorial>>

    @PUT("orden/cancelar/{id}")
    fun cancelarOrden(@Path("id")idOrden: Long):Call<Void>

    @GET("orden/espera/{id}")
    fun espera(@Path("id")id: String):Call<Orden>

    @GET("orden/trayecto/{id}")
    fun trayecto(@Path("id")id:String):Call<Trayecto>

    @GET("usuario/tecnico/{id}")
    fun miTecnico(@Path("id")id:String):Call<Tecnico>

    @GET("orden/statusCancelada/{id}")
    fun statusCancelar(@Path("id")id: String):Call<Orden>

    @POST("sendMessage")
    fun envio(@Body message: MessageModel): Call<MessageModel>


    companion object {

        var BASE_URL = "http://192.168.0.24:3000/"

        fun create() : Apis {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(Apis::class.java)

        }
    }

}

/*private var retrofit = Retrofit.Builder()
    .baseUrl("http://localhost:3000/")
    .addConverterFactory(ScalarsConverterFactory.create())
    .build()

var service: Apis = retrofit.create(Apis::class.java)*/