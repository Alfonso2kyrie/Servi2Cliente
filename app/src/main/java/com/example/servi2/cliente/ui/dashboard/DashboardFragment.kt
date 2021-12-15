package com.example.servi2.cliente.ui.dashboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.servi2.Apis
import com.example.servi2.R
import com.example.servi2.SaveUbicacion
import com.example.servi2.cliente.HistorialAdapter
import com.example.servi2.cliente.ServicioAdapter
import com.example.servi2.databinding.FragmentDashboardBinding
import com.example.servi2.usuario.model.Orden
import com.example.servi2.usuario.model.OrdenHistorial
import com.example.servi2.usuario.model.Servicio
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    lateinit var sharedPreferences: SharedPreferences
    var idUser = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE)
        idUser = sharedPreferences.getString("idPersona", "").toString()
        println(idUser)
        val apiInterface = Apis.create().historialUsuario(idUser)
        if (apiInterface != null) {
            doAsync {
                apiInterface.enqueue(object : Callback<List<OrdenHistorial>> {
                    override fun onResponse(
                        call: Call<List<OrdenHistorial>>,
                        response: Response<List<OrdenHistorial>>
                    ) {
                        val historial = response.body()
                        var myAdapter = HistorialAdapter(activity!!, R.layout.item_list_historial, response.body()!!)
                        listHistorial.adapter = myAdapter
                        myAdapter!!.notifyDataSetChanged()
                        listHistorial.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->  })
                    }

                    override fun onFailure(call: Call<List<OrdenHistorial>>, t: Throwable) {
                        TODO("Not yet implemented")
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