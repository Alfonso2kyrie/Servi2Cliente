package com.example.servi2.cliente

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.servi2.R
import com.example.servi2.usuario.model.Orden
import com.example.servi2.usuario.model.OrdenHistorial
import com.example.servi2.usuario.model.Servicio

class HistorialAdapter (private var context: Context, private var layout: Int, private var dataSource: List<OrdenHistorial>) : BaseAdapter() {

    private  val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(p0: Int): Any {
        return dataSource[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = inflater.inflate(layout,p2,false)
        val tipoServicio = view.findViewById<TextView>(R.id.txtServicioHistorial)
        val status = view.findViewById<TextView>(R.id.txtEstatus)
        val dia = view.findViewById<TextView>(R.id.txtDia)
        val idOrden = view.findViewById<TextView>(R.id.txtIdOrden)
        val tecnico = view.findViewById<TextView>(R.id.txtServidor_idPersona)
        val nombreTecnico = view.findViewById<TextView>(R.id.txtNombreTecnico)
        val apeTec = view.findViewById<TextView>(R.id.txtApellidoTecnico)

        val element = getItem(p0) as OrdenHistorial
        tipoServicio.text = element.tipoServicio
        status.text = element.status
        dia.text = element.dia
        idOrden.text = element.idOrden
        tecnico.text = element.servidor_idPersona
        nombreTecnico.text = element.nombre
        apeTec.text = element.apellidoPaterno

        return view
    }

}