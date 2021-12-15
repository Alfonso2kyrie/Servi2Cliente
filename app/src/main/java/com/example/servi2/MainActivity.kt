package com.example.servi2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.servi2.cliente.ClienteHome
import com.example.servi2.cliente.HomeClienteScreen
import com.example.servi2.crearUsuario.ActivityCrearUsuario
import com.example.servi2.trabajador.TrabajadorHome
import com.example.servi2.usuario.model.Login
import com.example.servi2.usuario.model.Usuario
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.GsonBuilder
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Console

class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
            if(it.isSuccessful){
                val token = it.result
                println("Firebase token -> $token")
                val sesion: SharedPreferences.Editor =
                    sharedPreferences.edit()
                sesion.putString("token", token.toString())
                sesion.apply()
                return@OnCompleteListener
            }
        })
        val btnInicio = findViewById<Button>(R.id.btnIniciar)
        btnInicio.setOnClickListener {

            var user = findViewById<Button>(R.id.txtUsuario) as EditText
            var contra = findViewById<Button>(R.id.txtContrasena) as EditText

            val usuario = Login(user.text.toString(), contra.text.toString())
            val apiInterface = Apis.create().iniciarSesion(usuario)
            //Toast.makeText(applicationContext, user.text.toString(), Toast.LENGTH_LONG).show()
            ///Toast.makeText(applicationContext, contra.text.toString(), Toast.LENGTH_LONG).show()
            Log.d("objetoooooooooo", usuario.usuario)
            Log.d("objetoooooooooo2", usuario.contrasena)
            if (apiInterface != null) {
                doAsync {
                    Log.d("objetoooooooooo3", "entreeee")
                    apiInterface.enqueue(object : Callback<Usuario> {
                        override fun onResponse(
                            call: Call<Usuario>?,
                            response: Response<Usuario>?
                        ) {
                            Log.d("objetoooooooooo3", "entreeee")
                            val usuario = response?.body()
                            /*val body = response?.body()?.toString()
                             val gson = GsonBuilder().create()
                             val usuario = gson.fromJson(body, Usuario::class.java) */

                            val sesion: SharedPreferences.Editor = sharedPreferences.edit()
                            sesion.putString("idPersona", usuario?.idPersona)
                            System.out.println("usuariooooooooooooooo "+usuario?.idPersona)
                            sesion.putString("nombre", usuario?.nombre)
                            sesion.putString("ape1", usuario?.apellidoPaterno)
                            sesion.putString("ape2", usuario?.apellidosMaterno)
                            sesion.putString("email", usuario?.email)
                            sesion.putString("telefono", usuario?.telefono)
                            sesion.putString("username", usuario?.username)
                            sesion.putString("contrasena", usuario?.contrasena)
                            sesion.putInt("rol", usuario?.rol_idRol!!)
                            sesion.apply()
                            Toast.makeText(
                                applicationContext,
                                usuario?.username.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("funcionaa", usuario.rol_idRol.toString())

                            if (usuario.rol_idRol == 1) {
                                val cliente = Intent(this@MainActivity, HomeClienteScreen::class.java)
                                startActivity(cliente)
                                finish()
                            } else {
                                val trabajador =
                                    Intent(this@MainActivity, TrabajadorHome::class.java)
                                startActivity(trabajador)

                            }
                        }

                        override fun onFailure(call: Call<Usuario>?, t: Throwable?) {
                            Log.e("errorrrrrrrrrr",t.toString())
                            AlertDialog.Builder(this@MainActivity).apply {
                                setTitle("Error de sesion")
                                setMessage("¡Usuario o contraseña incorrecta!")
                                setNegativeButton("ok", null)
                            }.show()
                            Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_LONG)
                                .show()
                            Log.e("malo",t.toString())
                        }
                    })
                }

            }
        }
        val btnCrear = findViewById<Button>(R.id.btnCrear)
        btnCrear.setOnClickListener {
            val intent = Intent(this, ActivityCrearUsuario::class.java)
            startActivity(intent)
            finish()
        }
    }
}

