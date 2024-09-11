
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

























