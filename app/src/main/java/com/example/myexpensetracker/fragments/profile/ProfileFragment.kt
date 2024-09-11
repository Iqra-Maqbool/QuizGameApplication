package com.example.myexpensetracker.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myexpensetracker.Login
import com.example.myexpensetracker.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)


        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]


        binding.Logoutbutton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireActivity(), Login::class.java))
            requireActivity().finish()
        }


        profileViewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.apply {
                    ShownameTV.text = user.username
                    ShowName.text = user.username
                    ShowemailTV.text = user.email
                    ShowpasswordTV.text = user.password
                }
            }
        }

        profileViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
        profileViewModel.fetchUserData()

        return binding.root
    }
}


















