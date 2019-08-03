package com.sanmiaderibigbe.travelmantics

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val providers = listOf(AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build())

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var firebaseAuthStateListner: FirebaseAuth.AuthStateListener

    private lateinit var adapter: DealAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        //TODO use no activity ui as the gate for checking authentication status.
        initFirebaseAuthStateListener()

        adapter = initRecyclerView()
        getTravelDeals()
        btn_new_travel_data.setOnClickListener {
            val intent = NewDealActivity.initUserActivity(this)
            startActivity(intent)
        }

    }

    private fun getTravelDeals() {
        firebaseDatabase.reference.child("deals").addValueEventListener(ValueEventListener1())

    }

    inner class ValueEventListener1 : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            val newDeals = mutableListOf<TravelDeals>()
            p0.children.forEach{
                newDeals.add(it.getValue(TravelDeals::class.java)!!)
            }
            Log.d("Deal", newDeals.toString())
            adapter.setDealList(newDeals)

        }

    }


    private fun initFirebaseAuthStateListener() {
        firebaseAuthStateListner = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.ic_launcher_background)
                        .setAvailableProviders(providers)
                        .build(),
                    RC_SIGN_IN
                )

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Sign in", Toast.LENGTH_SHORT).show()


            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseDatabase.reference.child("deals").addValueEventListener(ValueEventListener1())
        firebaseAuth.addAuthStateListener(firebaseAuthStateListner)
    }

    override fun onPause() {
        super.onPause()

        firebaseDatabase.getReference("deals").removeEventListener(ValueEventListener1())
        firebaseAuth.removeAuthStateListener(firebaseAuthStateListner)
    }

    private fun initRecyclerView(): DealAdapter {
        val adapter = DealAdapter(this)
        val recyclerView = findViewById<RecyclerView>(R.id.deal_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        return adapter
    }


    companion object {
        const val RC_SIGN_IN = 25
    }
}
