package com.example.elevateadmin.addTrainer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.elevateadmin.addClass.ClassDetails
import com.example.elevateadmin.addClass.ShowClassAdapter
import com.example.elevateadmin.databinding.ActivityShowTrainerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowTrainerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowTrainerBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var showTrainerAdapter: ShowTrainerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowTrainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabButton.setOnClickListener {
            startActivity(Intent(this, AddTrainerActivity::class.java))
        }

        showTrainerAdapter = ShowTrainerAdapter(emptyList())
        binding.showTrainerRecyclerView.adapter = showTrainerAdapter

        getClassDataFromFirebase()

    }

    private fun getClassDataFromFirebase() {

        dbRef = FirebaseDatabase.getInstance().reference.child("Trainers")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val trainerList = mutableListOf<TrainerDetails>()

                // Iterate through the snapshots and convert them to ClassDetails objects
                for (classSnapshot in snapshot.children) {
                    val trainerDetails = classSnapshot.getValue(TrainerDetails::class.java)
                    trainerDetails?.let { trainerList.add(it) }
                }

                // Update the adapter with the new data
                showTrainerAdapter.updateData(trainerList)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ShowTrainerActivity, "Retrieve Fail", Toast.LENGTH_SHORT).show()
            }

        })
    }
}