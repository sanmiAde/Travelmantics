package com.sanmiaderibigbe.travelmantics

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    private val providers = listOf(AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build())

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var firebaseAuthStateListner: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        initFirebaseAuthStateListener()

    }

    private fun initFirebaseAuthStateListener() {
        firebaseAuthStateListner = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(),
                    RC_SIGN_IN
                )

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            if (resultCode == Activity.RESULT_OK){
                Toast.makeText(this, "Sign in", Toast.LENGTH_SHORT).show()


            }else if(resultCode == Activity.RESULT_CANCELED){
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth.addAuthStateListener(firebaseAuthStateListner)
    }

    override fun onPause() {
        super.onPause()
        firebaseAuth.removeAuthStateListener(firebaseAuthStateListner)
    }

    companion object {
        const val RC_SIGN_IN = 25
    }
}
