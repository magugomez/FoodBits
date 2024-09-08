package com.example.foodbits

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

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
        lateinit var auth: FirebaseAuth

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
                            // Sign in success
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            // Redirect to another activity
                        } else {
                            // If sign in fails
                            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
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
}