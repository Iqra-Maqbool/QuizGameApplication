package com.example.myexpensetracker.fragments.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myexpensetracker.authentication.Login
import com.example.myexpensetracker.databinding.FragmentProfileBinding
import com.example.myexpensetracker.models.UserModelClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)


        // Logout button
        binding.Logoutbutton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireActivity(), Login::class.java))
            requireActivity().finish()
        }




        // Fetching user data from Fire store
        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
            Firebase.firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    document?.let {
                        if (it.exists()) {
                            it.toObject(UserModelClass::class.java)?.let { user ->

                            binding.apply {

                                ShownameTV.text = user.username
                                ShowName.text = user.username
                                ShowemailTV.text = user.email
                                ShowpasswordTV.text = user.password
                            }

                        }} else {
                            Toast.makeText(context, "Failed to fetch user data", Toast.LENGTH_SHORT)
                                .show()
                                }
                }}
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to fetch the data", Toast.LENGTH_SHORT).show()
                }
        }

        return binding.root
    }
}