package com.example.elevateadmin.addTrainer

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.elevateadmin.databinding.ActivityAddTrainerBinding
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.storage

class AddTrainerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTrainerBinding

    private lateinit var imageUri: Uri

    val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            if (uri != null) {
                // Uncomment the following lines to set the imageUri and update the ImageView
                imageUri = uri
                binding.uploadedImageViewOfTheTrainer.setImageURI(uri)
                binding.uploadedImageViewOfTheTrainer.visibility = View.VISIBLE
            } else {
                Log.d("SelectedImageUri", "Uri is null")
                Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTrainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set a click listener for uploading an image
        binding.uploadImageOfTheTrainer.setOnClickListener {
            // Launch an Intent to open the gallery and select an image
            getContent.launch("image/*") // Pass the MIME type or null for any type
            Toast.makeText(this, "Intent called", Toast.LENGTH_SHORT).show()
        }


        binding.saveButtonForTrainer.setOnClickListener {
            val trainerName = binding.trainerName.text.toString()
            val trainerDescription = binding.trainerDescription.text.toString()
            val trainerId = binding.trainerId.text.toString()

            if (::imageUri.isInitialized) {
                // Generate a unique ID for the image
                val uniqueId = FirebaseDatabase.getInstance().reference.child("Trainer").push().key
                uniqueId?.let {
                    val fileName = "$it.jpg"
                    val ref = Firebase.storage.reference.child("Trainer Images").child(it).child(fileName)
                    val uploadTask = ref.putFile(imageUri)

                    uploadTask.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // File uploaded successfully, now get the download URL
                            ref.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                                if (downloadUrlTask.isSuccessful) {
                                    val downloadUrl = downloadUrlTask.result.toString()

                                    // Now save the class details with the correct download URL
                                    saveTrainerDetailsToRealtimeDatabase(
                                        TrainerDetails(
                                            trainerName, trainerId ,trainerDescription, downloadUrl
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

    private fun saveTrainerDetailsToRealtimeDatabase(trainerDetails: TrainerDetails) {

        val databaseReference = FirebaseDatabase.getInstance().reference
        if (trainerDetails.trainerName.isEmpty() || trainerDetails.trainerDescription.isEmpty() || trainerDetails.imageUriOfTheTrainer.isEmpty()) {
            Toast.makeText(this, "Make sure all the fields are filled", Toast.LENGTH_SHORT)
                .show()
        } else {
            val path = databaseReference.child("Trainers")
            val trainerId = path.push().key

            if (trainerId != null) {
                path.child(trainerId).setValue(trainerDetails).addOnSuccessListener {
                    Toast.makeText(this, "Details are saved Successfully", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this@AddTrainerActivity, ShowTrainerActivity::class.java))
                }.addOnFailureListener {
                    Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}