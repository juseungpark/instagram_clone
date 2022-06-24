package com.juseung.instagram_clone

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.juseung.instagram_clone.databinding.ActivityLoginBinding
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import java.util.*
import javax.security.auth.callback.Callback

class LoginActivity : AppCompatActivity() {

    val callbackManager: CallbackManager = CallbackManager.Factory.create()

    lateinit var googleactivityResultLauncher: ActivityResultLauncher<Intent>



    private lateinit var binding: ActivityLoginBinding


    var auth: FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001

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
        binding.googleSignInButton.setOnClickListener {
            googleactivityResultLauncher.launch(googleSignInClient!!.signInIntent)

        }
        binding.facebookLoginButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                callbackManager, //we added callback here according to new sdk 12.0.0 version of facebook
                listOf("public_profile", "email")
            )
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("463168803840-m3ahs1b27l5sjcphj7ltcc2cdan1h87a.apps.googleusercontent.com")
            .requestEmail()
            .build()


        googleSignInClient = GoogleSignIn.getClient(this,gso)

        googleactivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
            if (result.resultCode == RESULT_OK){
                var task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    var account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                    Log.d("GoogleLogin", "firebaseAuthWithGoogle: " + account.id)
                } catch (e:ApiException){
                    Log.d("GoogleLogin", "Google sign in failed: " + e.message)
                }
            }
        }
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(result: LoginResult?) {
                    if (result?.accessToken != null) {
                        val accessToken = result.accessToken.token
                        firebaseAuthWithfacebook(result?.accessToken)
                    }
                }
                override fun onCancel() {
                }
                override fun onError(error: FacebookException) {
                }
            })

    }
    fun firebaseAuthWithfacebook(token : AccessToken?) {
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful) {
                    moveMainPage(task.result?.user)
                } else {
                    // Show the error message, 아이디와 패스워드가 틀렸을 때
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
    fun firebaseAuthWithGoogle(idToken: String){
        var credential = GoogleAuthProvider.getCredential(idToken,null)
        auth?.signInWithCredential(credential)
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
    fun signinAndSignup() {
        auth?.createUserWithEmailAndPassword(
            binding.emailEdittext.text.toString(),
            binding.passwordEdittext.text.toString()
        )
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    //Creating a user account
                    moveMainPage(task.result?.user)
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