package com.example.servi2.splashscreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.example.servi2.EsperaTecnico
import com.example.servi2.MainActivity
import com.example.servi2.R
import com.example.servi2.cliente.HomeClienteScreen
import com.example.servi2.cliente.ui.notifications.NotificationsViewModel
import com.example.servi2.usuario.model.Login

class SplashScreed : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screed)

        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        var user = sharedPreferences.getString("username","").toString()
        var pass = sharedPreferences.getString("contrasena","").toString()
        var orden:Long = sharedPreferences.getLong("idOrdenActiva",0)
        Toast.makeText(
            applicationContext,
            user,
            Toast.LENGTH_LONG
        ).show()
        startTimer(user, pass, orden)
    }
    fun startTimer(user:String,  pass:String, orden:Long){
        object : CountDownTimer(2000, 1000){
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {

                if(user!= "" && pass!=""){

                    if(orden != 0.toLong()){
                        val espera = Intent(this@SplashScreed, EsperaTecnico::class.java).apply {  }
                        startActivity(espera)
                        finish()
                    }else {
                        val sesion =
                            Intent(this@SplashScreed, HomeClienteScreen::class.java).apply { }
                        startActivity(sesion)
                        finish()
                    }
                }else {

                    val instent = Intent(this@SplashScreed, MainActivity::class.java).apply { }
                    startActivity(instent)
                    finish()
                }
            }

        }.start()
    }
}