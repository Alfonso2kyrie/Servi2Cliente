package com.example.servi2.crearUsuario

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.servi2.Apis
import com.example.servi2.usuario.model.Usuario
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.servi2.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class ActivityCrearUsuario : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    var tok:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.servi2.R.layout.activity_crear_usuario)
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        tok = sharedPreferences.getString("token","").toString()
        val btnCrear = findViewById<Button>(com.example.servi2.R.id.btnCrearCuenta)
        btnCrear.setOnClickListener {

            var nombre = findViewById<Button>(com.example.servi2.R.id.txtNombre) as EditText
            var apellidoPaterno = findViewById<Button>(com.example.servi2.R.id.txtApe1) as EditText
            var apellidoMaterno = findViewById<Button>(com.example.servi2.R.id.txtApe2) as EditText
            var email = findViewById<Button>(com.example.servi2.R.id.txtEmail) as EditText
            var telefono = findViewById<Button>(com.example.servi2.R.id.txtTelefonso) as EditText
            var username = findViewById<Button>(com.example.servi2.R.id.txtUsername) as EditText
            var contrasena =
                findViewById<Button>(com.example.servi2.R.id.txtContrasena2) as EditText
            var newToken = tok
            print("miToken $newToken")
            val usuario = Usuario(
                "",
                nombre.text.toString(),
                apellidoPaterno.text.toString(),
                apellidoMaterno.text.toString(),
                email.text.toString(),
                telefono.text.toString(),
                username.text.toString(),
                contrasena.text.toString(),
                1,
                tok
            )
            Log.d("pruebaa", usuario.nombre)
            Log.d("pruebaa", usuario.token)
            val apiInterface = Apis.create().createUser(usuario)
            if (apiInterface != null) {
                doAsync {
                    apiInterface.enqueue(object : Callback<Usuario> {
                        override fun onResponse(
                            call: Call<Usuario>?,
                            response: Response<Usuario>?
                        ) {
                            val usuario = response?.body()

                            AlertDialog.Builder(this@ActivityCrearUsuario).apply {
                                setTitle("Registro Correcto")
                                setMessage("¡Guardado con exito!")
                                setPositiveButton(
                                    "Aceptar",
                                    DialogInterface.OnClickListener(function = positiveButtonClick)
                                )
                            }.show()

                        }

                        override fun onFailure(call: Call<Usuario>?, t: Throwable?) {
                            AlertDialog.Builder(this@ActivityCrearUsuario).apply {
                                setTitle("Error en los datos")
                                setMessage("¡Upss hubo un error en el envio!")
                                setNegativeButton("ok", null)
                            }.show()
                            Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    })
                }
            }

        }
    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        val intent = Intent(this@ActivityCrearUsuario, MainActivity::class.java)
        startActivity(intent)
    }

}