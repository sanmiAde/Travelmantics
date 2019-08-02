package com.sanmiaderibigbe.travelmantics

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_new_deal.*
import java.net.URI
import java.util.*

class NewDealActivity : AppCompatActivity() {

    private lateinit var firebaseStorage: FirebaseStorage

    private lateinit var firebaseStorageReference: StorageReference

    private lateinit var firebaseDatabase: FirebaseDatabase

    private var imageUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_deal)

        firebaseStorage = FirebaseStorage.getInstance()
        firebaseStorageReference =
            firebaseStorage.reference.child("Deals/").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
        firebaseDatabase = FirebaseDatabase.getInstance()

        btn_upload_picture.setOnClickListener {
            val pictureIntent = Intent(Intent.ACTION_GET_CONTENT)
            pictureIntent.type = "image/jpeg"
            pictureIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(pictureIntent, PICTURE_REQUEST_CODE)
        }


    }

    private fun uploadNewDeal() {
        val locationTextInput = text_input_location
        val costTextInput = text_input_cost
        val descriptionInput = text_input_description

        val isFormValid = isFormValid(locationTextInput.getText(), costTextInput.getText(), descriptionInput.getText())
        when {
            isFormValid -> {
                if (imageUrl == null) {
                    imageUrl = Uri.EMPTY
                }

                //TODO add on failure and network error stuff


                firebaseStorageReference.child(UUID.randomUUID().toString())
                    .putFile(imageUrl!!)
                    .addOnFailureListener {
                        Toast.makeText(this, "${it.message.toString()}", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener {

                        val travelDeal = TravelDeals(
                            description = descriptionInput.getData(),
                            location = locationTextInput.getData(),
                            cost = costTextInput.getData(),
                            imageUrl = it.uploadSessionUri.toString()
                        )
                        firebaseDatabase.reference.child("deals").push().setValue(travelDeal)
                            .addOnFailureListener {
                                Toast.makeText(this, "${it.message.toString()}", Toast.LENGTH_SHORT).show()
                            }
                            .addOnSuccessListener {
                                locationTextInput.editText?.setText("")
                                costTextInput.editText?.setText("")
                                descriptionInput.editText?.setText("")
                            }

                    }

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_add_new_deal -> {
                uploadNewDeal()
                Toast.makeText(this, "it is working.", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_deals_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    private fun isFormValid(locationText: Editable, costTextInput: Editable, descriptionText: Editable): Boolean {

        val isLocationValid = isTextValid(locationText)
        val isCostValid = isCostValid(costTextInput)
        val isDescriptionValid = isTextValid(descriptionText)

        when {
            isLocationValid -> text_input_location.error = null
            else -> text_input_location.error = "Location is not valid."
        }

        when {
            isCostValid -> text_input_cost.error = null
            else -> text_input_cost.error = "Cost is not valid."
        }

        when {
            isLocationValid -> text_input_description.error = null
            else -> text_input_description.error = "Description is not valid."
        }

        return isLocationValid && isCostValid && isDescriptionValid

    }

    private fun isTextValid(text: Editable): Boolean {
        return text.isNotBlank()
    }

    private fun isCostValid(text: Editable): Boolean {
        return text.isDigitsOnly() && text.isNotBlank()
    }

    private fun TextInputLayout.getText(): Editable {
        return this.editText?.text!!
    }

    private fun TextInputLayout.getData(): String {
        return this.editText?.text.toString()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageUrl = data?.data
            //TODO use bitmap. Actual picture is very slow for huge files.
            imageView_location.visibility = View.VISIBLE
            imageView_location.setImageURI(data?.data)

        }
    }

    companion object {
        internal fun initUserActivity(context: Context): Intent {
            return Intent(context, NewDealActivity::class.java)
        }

        const val PICTURE_REQUEST_CODE = 234
    }


}
