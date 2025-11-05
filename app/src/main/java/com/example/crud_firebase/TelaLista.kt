package com.example.crud_firebase

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class TelaLista : AppCompatActivity() {

    private val db = Firebase.firestore
//    private val nomes = mutableListOf<String>()
//    private val ids = mutableListOf<String>()

    private val funcionarios = mutableListOf<Funcionario>()
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_lista)

        listView = findViewById(R.id.listViewFuncionarios)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        listView.adapter = adapter
        carregarFuncionarios()
        listView.setOnItemLongClickListener { _, _, pos, _ ->
            confirmarExclusao(pos)
            true
        }
        listView.setOnItemClickListener { _, _, pos, _ ->
            abrirModalDeEdicao(pos)
        }
    }

    private fun carregarFuncionarios() {
        db.collection("funcionarios").get()
            .addOnSuccessListener { result ->
                funcionarios.clear() // Limpa a lista de funcionários
                for (doc in result) {
                    val funcionario = Funcionario(
                        id = doc.id,
                        nome = doc.getString("nome") ?: "",
                        cargo = doc.getString("cargo") ?: "",
                        email = doc.getString("email") ?: ""
                    )
                    funcionarios.add(funcionario)
                }
                val nomesParaAdapter = funcionarios.map { it.nome }
                // Atualiza o adapter para mostrar apenas os nomes
                adapter.clear()
                adapter.addAll(funcionarios.map { it.nome }) // Extrai os nomes para o adapter
                adapter.notifyDataSetChanged()

                if (funcionarios.isEmpty()) {
                    Toast.makeText(this, "Nenhum funcionário cadastrado.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao listar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

//    private fun carregarNomes() {
//        db.collection("funcionarios").get()
//            .addOnSuccessListener { result ->
//                nomes.clear(); ids.clear()
//                for (doc in result) {
//                    ids.add(doc.id)
//                    nomes.add(doc.getString("nome") ?: "(sem nome)")
//                }
//                adapter.notifyDataSetChanged()
//                if (nomes.isEmpty()) Toast.makeText(
//                    this, "Nenhum funcionário cadastrado.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Erro ao listar: ${e.message}", Toast.LENGTH_LONG).show()
//            }
//    }

    private fun confirmarExclusao(pos: Int) {
        val funcionario = funcionarios[pos]
        AlertDialog.Builder(this)
            .setTitle("Excluir")
            .setMessage("Excluir \"${funcionario.nome}\"?")
            .setPositiveButton("Sim") { _, _ -> excluir(pos) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun excluir(pos: Int) {
        val idDoc = funcionarios[pos].id.toString()
        //val idDoc = ids[pos]
        db.collection("funcionarios").document(idDoc).delete()
            .addOnSuccessListener {
//                nomes.removeAt(pos); ids.removeAt(pos)
//                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Excluído.", Toast.LENGTH_SHORT).show()
                carregarFuncionarios()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Em TelaLista.kt
    private fun abrirModalDeEdicao(pos: Int) {
        // Pega o objeto funcionário completo da lista
        val funcionario = funcionarios[pos]
        // Infla o layout do dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_editar_funcionario, null)

        // Pega as referências dos EditTexts do layout inflado
        val editTextNome = dialogView.findViewById<EditText>(R.id.editTextNome)
        val editTextEmail = dialogView.findViewById<EditText>(R.id.editTextEmail)
        val editTextCargo = dialogView.findViewById<EditText>(R.id.editTextCargo)

        // Preenche os EditTexts com os dados atuais do funcionário
        editTextNome.setText(funcionario.nome)
        editTextCargo.setText(funcionario.cargo)
        editTextEmail.setText(funcionario.email)


        // Cria e exibe o AlertDialog
        AlertDialog.Builder(this)
            .setTitle("Editar Funcionário")
            .setView(dialogView)
            .setPositiveButton("Salvar") { _, _ ->
                val novoNome = editTextNome.text.toString().trim()
                val novoEmail = editTextEmail.text.toString().trim()
                val novoCargo = editTextCargo.text.toString().trim()

                if (novoNome.isNotEmpty() && novoEmail.isNotEmpty() && novoCargo.isNotEmpty()) {
                    val dadosAtualizados = mapOf(
                        "nome" to novoNome,
                        "email" to novoEmail,
                        "cargo" to novoCargo
                    )
                    alterarFuncionario(funcionario.id.toString(), dadosAtualizados)
                } else {
                    Toast.makeText(this, "Todos os campos são obrigatórios.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun alterarFuncionario(idDoc: String, dados: Map<String, Any>) {
        db.collection("funcionarios").document(idDoc)
            .update(dados)
            .addOnSuccessListener {
                Toast.makeText(this, "Funcionário alterado com sucesso!", Toast.LENGTH_SHORT).show()
                // Recarrega os dados da lista para refletir as alterações
                carregarFuncionarios()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao alterar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
