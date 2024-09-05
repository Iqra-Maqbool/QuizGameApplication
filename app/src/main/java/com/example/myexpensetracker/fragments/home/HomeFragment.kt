package com.example.myexpensetracker.fragments.home
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myexpensetracker.R
import com.example.myexpensetracker.databinding.FragmentHomeBinding
import com.example.myexpensetracker.models.UserModelClass
import com.example.myexpensetracker.models.CategoryModelClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var categoryList: ArrayList<CategoryModelClass>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize Firebase instances
        auth = Firebase.auth
        firestore = Firebase.firestore
        databaseReference = Firebase.database.reference


        categoryList = ArrayList<CategoryModelClass>().apply{
            add(CategoryModelClass(R.drawable.science, "Physics"))
            add(CategoryModelClass(R.drawable.english3, "English"))
            add(CategoryModelClass(R.drawable.programming, "Programming"))
            add(CategoryModelClass(R.drawable.math2, "Math"))
        }


        binding.categoryRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = CategoryAdapter(categoryList, requireActivity())
            setHasFixedSize(true)
        }


        // Fetching username from Fire store
        auth.currentUser?.let { user ->
            firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        document.toObject(UserModelClass::class.java)?.let { userModel ->
                            binding.ShowName.text = userModel.username
                        } ?: Toast.makeText(context, "User data is null ", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No such document exists", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to fetch the data", Toast.LENGTH_SHORT).show()
                }



            // Fetch coins from Realtime Database
            databaseReference.child("Coins").child(user.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            binding.CoinTextView.text = (snapshot.value as? Long ?: 0).toString()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to fetch coin data", Toast.LENGTH_SHORT).show()
                    }
                })
        } ?: Toast.makeText(context, "User is not logged in", Toast.LENGTH_SHORT).show()

        return binding.root
    }
}
