package com.example.crud_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

private val db by lazy { Firebase.firestore }

class TelaCadastroEmpresa : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_cadastro_empresa)


//        val btnFuncionarios: Button = findViewById(R.id.btnGerenciarFuncionarios)
        val btnFuncionario: Button = findViewById(R.id.btnGerenciarFuncionario)

        val edtNome  = findViewById<EditText>(R.id.edtNome2)
        val edtCnpj = findViewById<EditText>(R.id.edtCnpj)
        val btnCad   = findViewById<Button>(R.id.btnCadastrar2)

        val btnAbrirLista = findViewById<Button>(R.id.btnAbrirLista2)
        // abre a nova Activity
        btnAbrirLista.setOnClickListener {
            startActivity(Intent(this, TelaListEmpresa::class.java))
        }

        btnFuncionario.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnCad.setOnClickListener {
            val nome  = edtNome.text.toString()
            val cnpj = edtCnpj.text.toString()
            cadastrarEmpresa(nome, cnpj) {
                edtNome.text?.clear()
                edtCnpj.text?.clear()
            }
        }
    }

    private fun cadastrarEmpresa(
        nome: String,
        cnpj: String,
        onSuccess: () -> Unit = {}
    ) {
        val dados = hashMapOf(
            "nome" to nome,
            "cnpj" to cnpj,
        )

        db.collection("empresas")
            .add(dados)
            .addOnSuccessListener {
                Toast.makeText(this, "Cadastrado!", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}