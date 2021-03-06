package com.example.servi2.cliente.ui.notifications

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.servi2.*
import com.example.servi2.databinding.FragmentNotificationsBinding
import com.example.servi2.usuario.model.Orden
import com.example.servi2.usuario.model.OrdenHistorial
import com.example.servi2.usuario.model.Perfil
import com.example.servi2.usuario.model.idOrden
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    lateinit var sharedPreferences: SharedPreferences
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE)
        var idPersona = sharedPreferences.getString("idPersona","").toString()
        var nombre = sharedPreferences.getString("nombre","").toString()
        var apellido = sharedPreferences.getString("ape1", "").toString()
        var email = sharedPreferences.getString("email","").toString()
        var telefono = sharedPreferences.getString("telefono","").toString()
        binding.textNombre.setText(nombre)
        binding.textApellido.setText(apellido)
        binding.textEmail.setText(email)
        binding.textTelefono.setText(telefono)

        binding.btnAcerca.setOnClickListener { binding->

            val dialog = activity?.let {
                AlertDialog.Builder(it)
                    .setTitle("??Desea cerrar sesion?")
                    .setMessage("Esta apunto de cerrar sesion")
                    .setNegativeButton("Cancelar") { view, _ ->
                        view.dismiss()
                    }
                    .setPositiveButton("Aceptar") { view, _ ->
                        sharedPreferences.edit().clear().commit()
                        var cerrarSesion = Intent(activity, MainActivity::class.java)
                        activity?.startActivity(cerrarSesion)
                        activity?.finish()
                    }
                    .setCancelable(false)
                    .create()
            }
            if (dialog != null) {
                dialog.show()
            }
        }

        binding.btnDetalles.setOnClickListener {
            var acerca = Intent(activity, AcercaDeActivity::class.java)
            activity?.startActivity(acerca)
        }

        binding.btnActualizar.setOnClickListener {
            Log.d("nombree",binding.textNombre.text.toString())
            var perfil = Perfil(idPersona, binding.textNombre.text.toString(), binding.textApellido.text.toString(), binding.textEmail.text.toString(), binding.textTelefono.text.toString())
            val apiInterface = Apis.create().actualizarUsuario(perfil)

            doAsync {
                apiInterface.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        val sesion: SharedPreferences.Editor = sharedPreferences.edit()
                        sesion.putString("nombre",perfil.nombre)
                        sesion.putString("ape1",perfil.apellidoPaterno)
                        sesion.putString("email",perfil.email)
                        sesion.putString("telefono",perfil.telefono)
                        sesion.apply()
                        activity?.let { it1 ->
                            AlertDialog.Builder(it1).apply {
                                setTitle("Guardado Correctamente")
                                setMessage("Actualizado")
                                setNegativeButton("ok", null)
                            }.show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        activity?.let { it1 ->
                            AlertDialog.Builder(it1).apply {
                                setTitle("Error de guardado")
                                setMessage("intentelo mas tarde")
                                setNegativeButton("ok", null)
                            }.show()
                        }
                    }
                })
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}