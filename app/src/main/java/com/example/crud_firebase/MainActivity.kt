package com.example.crud_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

private val db by lazy { Firebase.firestore }

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtNome  = findViewById<EditText>(R.id.edtNome)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtCargo = findViewById<EditText>(R.id.edtCargo)
        val btnCad   = findViewById<Button>(R.id.btnCadastrar)

        val btnAbrirLista = findViewById<Button>(R.id.btnAbrirLista)
        // abre a nova Activity
        btnAbrirLista.setOnClickListener {
            startActivity(Intent(this, TelaLista::class.java))
        }

        btnCad.setOnClickListener {
            val nome  = edtNome.text.toString()
            val email = edtEmail.text.toString()
            val cargo = edtCargo.text.toString()
            cadastrarFuncionario(nome, email, cargo) {
                edtNome.text?.clear()
                edtEmail.text?.clear()
                edtCargo.text?.clear()
            }
        }
    }

    private fun cadastrarFuncionario(
        nome: String,
        email: String,
        cargo: String,
        onSuccess: () -> Unit = {}
    ) {
        val dados = hashMapOf(
            "nome" to nome,
            "email" to email,
            "cargo" to cargo
        )

        db.collection("funcionarios")
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