package com.example.servi2.trabajador

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.servi2.MainActivity
import com.example.servi2.R
import com.example.servi2.crearUsuario.ActivityCrearUsuario

class TrabajadorHome : AppCompatActivity() {
    lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trabajador_home)

        val btnCerrar = findViewById<Button>(R.id.btnCerrarSesion)
        btnCerrar.setOnClickListener {

            val editor:SharedPreferences.Editor = preferences.edit()
            editor.clear()
            editor.apply()

            val cerrar= Intent(this, MainActivity::class.java)
            startActivity(cerrar)
            finish()
        }
    }
}