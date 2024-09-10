package com.example.foodbits

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LogIn : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        val editTextEmailOrUsername = findViewById<EditText>(R.id.editTextEmailOrUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogIn)
        buttonLogin.setOnClickListener {
            val emailOrUsername = editTextEmailOrUsername.text.toString()
            val password = editTextPassword.text.toString()

            if (emailOrUsername.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(emailOrUsername, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Home::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            handleUsernameLogin(emailOrUsername, password)
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "Please enter both email/username and password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleUsernameLogin(username: String, password: String) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                } else {
                    val userDoc = result.documents.first()
                    val email = userDoc.getString("email")

                    email?.let {
                        val auth = FirebaseAuth.getInstance()
                        auth.signInWithEmailAndPassword(it, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, Home::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } ?: Toast.makeText(this, "Error: Email not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
