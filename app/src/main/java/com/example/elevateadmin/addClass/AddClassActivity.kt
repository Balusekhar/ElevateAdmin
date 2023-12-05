package com.example.elevateadmin.addClass

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.elevateadmin.databinding.ActivityAddClassBinding
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.storage

class AddClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddClassBinding

    private lateinit var imageUri: Uri

    val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            if (uri != null) {
                // Uncomment the following lines to set the imageUri and update the ImageView
                imageUri = uri
                binding.uploadedImageView.setImageURI(uri)
                binding.uploadedImageView.visibility = View.VISIBLE
            } else {
                Log.d("SelectedImageUri", "Uri is null")
                Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddClassBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Set a click listener for uploading an image
        binding.uploadImage.setOnClickListener {
            // Launch an Intent to open the gallery and select an image
            getContent.launch("image/*") // Pass the MIME type or null for any type
            Toast.makeText(this, "Intent called", Toast.LENGTH_SHORT).show()
        }



        binding.saveButton.setOnClickListener {
            val className = binding.className.text.toString()
            val classDescription = binding.description.text.toString()
            val dateOfTheClass = binding.dateOfTheClass.text.toString()
            val category = binding.selectCategory.selectedItem.toString()

            if (::imageUri.isInitialized) {
                // Generate a unique ID for the image
                val uniqueId = FirebaseDatabase.getInstance().reference.child("Classes").push().key
                uniqueId?.let {
                    val fileName = "$it.jpg"
                    val ref = Firebase.storage.reference.child("Images").child(it).child(fileName)
                    val uploadTask = ref.putFile(imageUri)

                    uploadTask.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // File uploaded successfully, now get the download URL
                            ref.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                                if (downloadUrlTask.isSuccessful) {
                                    val downloadUrl = downloadUrlTask.result.toString()

                                    // Now save the class details with the correct download URL
                                    saveClassDetailsToRealtimeDatabase(
                                        ClassDetails(
                                            className, classDescription, dateOfTheClass, category, downloadUrl
                                        )
                                    )
                                } else {
                                    // Handle the error in getting the download URL
                                    Toast.makeText(this, "Error getting download URL", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // Handle the error in uploading the file
                            Toast.makeText(this, "Error uploading file", Toast.LENGTH_SHORT).show()
                        }
                    }
                } ?: run {
                    // Handle the case where push() returns null
                    Toast.makeText(this, "Error generating unique ID", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun saveClassDetailsToRealtimeDatabase(classDetails: ClassDetails) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        if (classDetails.className.isEmpty() || classDetails.imageUri.isEmpty() || classDetails.dateOfTheClass.isEmpty() || classDetails.category.isEmpty() || classDetails.description.isEmpty()) {
            Toast.makeText(this, "Make sure all the fields are filled", Toast.LENGTH_SHORT)
                .show()
        } else {
            val path = databaseReference.child("Classes")
            val classId = path.push().key

            if (classId != null) {
                path.child(classId).setValue(classDetails).addOnSuccessListener {
                    Toast.makeText(this, "Details are saved Successfully", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this@AddClassActivity, ShowClassActivity::class.java))
                }.addOnFailureListener {
                    Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

