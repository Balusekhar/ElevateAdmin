package com.example.elevateadmin.addClass


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.elevateadmin.databinding.ActivityShowClassBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowClassBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var showClassAdapter: ShowClassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabButton.setOnClickListener {
            startActivity(Intent(this, AddClassActivity::class.java))
        }

        showClassAdapter = ShowClassAdapter(emptyList())
        binding.showClassRecyclerView.adapter = showClassAdapter

        getClassDataFromFirebase()

    }

    private fun getClassDataFromFirebase() {

        dbRef = FirebaseDatabase.getInstance().reference.child("Classes")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val classList = mutableListOf<ClassDetails>()

                // Iterate through the snapshots and convert them to ClassDetails objects
                for (classSnapshot in snapshot.children) {
                    val classDetail = classSnapshot.getValue(ClassDetails::class.java)
                    classDetail?.let { classList.add(it) }
                }

                // Update the adapter with the new data
                showClassAdapter.updateData(classList)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ShowClassActivity, "Retrieve Fail", Toast.LENGTH_SHORT).show()
            }

        })

    }
}