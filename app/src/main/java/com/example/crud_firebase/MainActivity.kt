package com.example.crud_firebase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
class MainActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val col = db.collection("funcionarios")
    // views
    private lateinit var edtNome: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var edtCargo: EditText
    private lateinit var btnSalvarOuAtualizar: Button
    private lateinit var btnCancelar: Button
    private lateinit var rv: RecyclerView

    // estado
    private val lista = mutableListOf<Funcionario>()
    private lateinit var adapter: FuncAdapter
    private var editId: String? = null // null = criar, senão = atualizar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // bind
        edtNome = findViewById(R.id.edtNome)
        edtEmail = findViewById(R.id.edtEmail)
        edtSenha = findViewById(R.id.edtSenha)
        edtCargo = findViewById(R.id.edtCargo)
        btnSalvarOuAtualizar = findViewById(R.id.btnSalvarOuAtualizar)
        btnCancelar = findViewById(R.id.btnCancelar)
        rv = findViewById(R.id.rvFuncionarios)

        // lista
        adapter = FuncAdapter(
            dados = lista,
            onEditar = { f -> preencherFormulario(f) },
            onExcluir = { f -> excluir(f) }
        )
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        btnSalvarOuAtualizar.setOnClickListener { salvarOuAtualizar() }
        btnCancelar.setOnClickListener { limparFormulario() }

        // READ em tempo real (ordenado por criação)
        col.orderBy("criadoEm", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                lista.clear()
                snap?.documents?.forEach { d ->
                    val f = d.toObject(Funcionario::class.java)
                    if (f != null) {
                        f.id = d.id
                        lista.add(f)
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun salvarOuAtualizar() {
        val nome = edtNome.text.toString()
        val email = edtEmail.text.toString()
        val senha = edtSenha.text.toString()
        val cargo = edtCargo.text.toString()

        // sem muitas validações; foco didático
        if (editId == null) {
            // CREATE
            val dados = hashMapOf(
                "nome" to nome,
                "email" to email,
                "senha" to senha,
                "cargo" to cargo,
                "criadoEm" to FieldValue.serverTimestamp()
            )
            col.add(dados).addOnSuccessListener {
                toast("Salvo")
                limparFormulario()
            }
        } else {
            // UPDATE
            val dados = mapOf(
                "nome" to nome,
                "email" to email,
                "senha" to senha,
                "cargo" to cargo
            )
            col.document(editId!!).update(dados).addOnSuccessListener {
                toast("Atualizado")
                limparFormulario()
            }
        }
    }

    private fun preencherFormulario(f: Funcionario) {
        editId = f.id
        edtNome.setText(f.nome)
        edtEmail.setText(f.email)
        edtSenha.setText(f.senha)
        edtCargo.setText(f.cargo)
        btnSalvarOuAtualizar.text = "Atualizar"
        btnCancelar.visibility = Button.VISIBLE
    }

    private fun excluir(f: Funcionario) {
        val id = f.id ?: return
        col.document(id).delete().addOnSuccessListener { toast("Excluído") }
    }

    private fun limparFormulario() {
        editId = null
        edtNome.setText("")
        edtEmail.setText("")
        edtSenha.setText("")
        edtCargo.setText("")
        btnSalvarOuAtualizar.text = "Salvar"
        btnCancelar.visibility = Button.GONE
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
