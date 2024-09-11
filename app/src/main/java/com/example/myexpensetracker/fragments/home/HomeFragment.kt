
package com.example.myexpensetracker.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myexpensetracker.R
import com.example.myexpensetracker.adapter.CategoryAdapter
import com.example.myexpensetracker.databinding.FragmentHomeBinding
import com.example.myexpensetracker.models.CategoryModelClass

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)



        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        val categoryList = ArrayList<CategoryModelClass>().apply {
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




        homeViewModel.username.observe(viewLifecycleOwner) { username ->
            username?.let {
                binding.ShowName.text = it
            }
        }




        homeViewModel.coins.observe(viewLifecycleOwner) { coins ->
            binding.CoinTextView.text = coins.toString()
        }



        homeViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
       homeViewModel.fetchUsername()
        homeViewModel.fetchCoins()
        return binding.root
    }
}



























/*
package com.example.myexpensetracker.fragments.home
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myexpensetracker.R
import com.example.myexpensetracker.adapter.CategoryAdapter
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
                        } ?: showToast("User data is null")
                    } else {
                      showToast("No such document exist")
                    }
                }
                .addOnFailureListener {
                    showToast("Failed to fetch coins data")
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
                       showToast("Failed to fetch coins data")
                    }
                })
        } ?: showToast("User is not logged in")
        return binding.root
    }

private fun showToast(message: String){
    Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
}}*/
