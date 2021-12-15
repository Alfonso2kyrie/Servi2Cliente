package com.example.servi2.cliente.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.servi2.Apis
import com.example.servi2.R
import com.example.servi2.SaveUbicacion
import com.example.servi2.chat.ChatActivity
import com.example.servi2.cliente.ServicioAdapter
import com.example.servi2.databinding.FragmentHomeBinding
import com.example.servi2.usuario.model.Servicio
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        val apiInterface = Apis.create().getServicios()

        if (apiInterface != null) {
            doAsync {
                apiInterface.enqueue(object : Callback<ArrayList<Servicio>> {

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
                        var miAdapter = ServicioAdapter(activity!!, R.layout.item_rv_servicio, response.body()!!)
                        listaServicios.adapter= miAdapter
                        miAdapter!!.notifyDataSetChanged()
                        listaServicios.setOnItemClickListener(
                            AdapterView.OnItemClickListener { parent, view, position, id ->
                                val intent = Intent(activity!!, SaveUbicacion::class.java)
                                intent.putExtra("tipoServicio", response.body()!!.get(position).tipoServicio)
                                intent.putExtra("idServicio", response.body()!!.get(position).idServicio)
                                intent.putExtra("imagen", response.body()!!.get(position).imagenServicio)
                                Log.d("iddddddddddddddd",
                                    response.body()!!.get(position).idServicio
                                )
                                activity!!.startActivity(intent)
                            })

                    }

                    override fun onFailure(call: Call<ArrayList<Servicio>>, t: Throwable) {
                        AlertDialog.Builder(context!!).apply {
                            setTitle("Error en los datos")
                            setMessage("¡Upss hubo un error en el envio!")
                            setNegativeButton("ok", null)
                        }.show()
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