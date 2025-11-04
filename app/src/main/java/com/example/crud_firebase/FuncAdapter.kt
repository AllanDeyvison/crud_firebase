package com.example.crud_firebase


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FuncAdapter(
    private val dados: List<Funcionario>,
    private val onEditar: (Funcionario) -> Unit,
    private val onExcluir: (Funcionario) -> Unit
) : RecyclerView.Adapter<FuncAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val txtNome: TextView = v.findViewById(R.id.txtNome)
        val txtEmail: TextView = v.findViewById(R.id.txtEmail)
        val txtCargo: TextView = v.findViewById(R.id.txtCargo)
        val btnEditar: Button = v.findViewById(R.id.btnEditar)
        val btnExcluir: Button = v.findViewById(R.id.btnExcluir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_funcionario, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, position: Int) {
        val f = dados[position]
        h.txtNome.text = f.nome
        h.txtEmail.text = "E-mail: ${f.email}"
        h.txtCargo.text = "Cargo: ${f.cargo}"
        h.btnEditar.setOnClickListener { onEditar(f) }
        h.btnExcluir.setOnClickListener { onExcluir(f) }
    }

    override fun getItemCount(): Int = dados.size
}