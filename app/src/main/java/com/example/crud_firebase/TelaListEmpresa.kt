package com.example.crud_firebase

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class TelaListEmpresa : AppCompatActivity() {

    private val db = Firebase.firestore
    private val empresas = mutableListOf<Empresa>()
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_tela_list_empresa)

            listView = findViewById(R.id.listViewEmpresas)
            adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
            listView.adapter = adapter
            carregarEmpresas()
            // Clique curto para editar
            listView.setOnItemClickListener{ _, _, pos, _ ->
                abrirModalCadastro(pos)
            }
            // Clique longo para excluir
            listView.setOnItemLongClickListener { _, _, pos, _ ->
                confirmarExclusao(pos)
                true
            }

//            // Botão para cadastrar uma nova empresa
//            btnCadastrar.setOnClickListener {
//                abrirModalCadastro(null) // Passa null para indicar que é um novo cadastro
//            }
        }

        private fun carregarEmpresas() {
            db.collection("empresas").get()
                .addOnSuccessListener { result ->
                    empresas.clear()
                    for (doc in result) {
                        val empresa = Empresa(
                            id = doc.id,
                            nome = doc.getString("nome") ?: "",
                            cnpj = doc.getString("cnpj") ?: ""
                        )
                        empresas.add(empresa)
                    }
                    val nomesParaAdapter = empresas.map { it.nome }
                    // Atualiza o adapter para mostrar apenas os nomes
                    adapter.clear()
                    adapter.addAll(empresas.map { it.nome }) // Extrai os nomes para o adapter
                    adapter.notifyDataSetChanged()
                    atualizarAdapter()

                    if (empresas.isEmpty()) {
                        Toast.makeText(this, "Nenhuma empresa cadastrada.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar empresas: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        private fun atualizarAdapter() {
            val nomesParaAdapter = empresas.map { it.nome }
            adapter.clear()
            adapter.addAll(nomesParaAdapter)
            adapter.notifyDataSetChanged()
        }

        private fun abrirModalCadastro(pos: Int) {
            val empresa = empresas[pos]

            val dialogView = layoutInflater.inflate(R.layout.dialog_cadastrar_empresa, null)
            val editTextNome: EditText = dialogView.findViewById(R.id.editTextNomeFantasia)
            val editTextCnpj: EditText = dialogView.findViewById(R.id.editTextCnpj)

//            val tituloModal = if (empresa == null) "Cadastrar Empresa" else "Editar Empresa"

            editTextNome.setText(empresa.nome)
            editTextCnpj.setText(empresa.cnpj)

            AlertDialog.Builder(this)
                .setTitle("Editar Empresa")
                .setView(dialogView)
                .setPositiveButton("Salvar") { _, _ ->
                    val nome = editTextNome.text.toString().trim()
                    val cnpj = editTextCnpj.text.toString().trim()

                    if (nome.isNotEmpty() && cnpj.isNotEmpty()) {
                        val dadosAtualizados = mapOf(
                            "nome" to nome,
                            "cnpj" to cnpj
                        )
                        alterarEmpresa(empresa.id.toString(), dadosAtualizados)
                    } else {
                        Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        private fun alterarEmpresa(idDoc: String, dados: Map<String, Any>) {
            db.collection("empresas").document(idDoc)
                .update(dados)
                .addOnSuccessListener {
                    Toast.makeText(this, "Empresa alterado com sucesso!", Toast.LENGTH_SHORT).show()
                    // Recarrega os dados da lista para refletir as alterações
                    carregarEmpresas()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao alterar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }


        private fun confirmarExclusao(pos: Int) {
            val empresa = empresas[pos]
            AlertDialog.Builder(this)
                .setTitle("Excluir Empresa")
                .setMessage("Tem certeza que deseja excluir \"${empresa.nome}\"?")
                .setPositiveButton("Sim") { _, _ -> excluir(pos) }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        private fun excluir(pos: Int) {
            val idDoc = empresas[pos].id.toString()
            db.collection("empresas").document(idDoc).delete()
                .addOnSuccessListener {
//                nomes.removeAt(pos); ids.removeAt(pos)
//                adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Excluído.", Toast.LENGTH_SHORT).show()
                    carregarEmpresas()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }