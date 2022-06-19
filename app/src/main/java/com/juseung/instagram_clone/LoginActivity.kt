package com.juseung.instagram_clone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.juseung.instagram_clone.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding


    var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)


        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        binding.emailLoginButton.setOnClickListener {
            signinAndSignup()
        }
    }

    fun signinAndSignup() {
        auth?.createUserWithEmailAndPassword(
            binding.emailEdittext.text.toString(),
            binding.passwordEdittext.text.toString()
        )
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    //Creating a user account
                    moveMainPage(task.result.user)
                } else {
                    //Login if you have account
                    signinEmail()
                }
            }

    }
    fun signinEmail() {
        auth?.signInWithEmailAndPassword(
            binding.emailEdittext.text.toString(),
            binding.passwordEdittext.text.toString()
        )
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Login
                    moveMainPage(task.result.user)
                } else {
                    //show the error message
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()

                }
            }

    }

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}