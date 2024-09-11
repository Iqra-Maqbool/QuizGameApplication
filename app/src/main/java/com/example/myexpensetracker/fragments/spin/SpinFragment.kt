package com.example.myexpensetracker.fragments.spin

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myexpensetracker.databinding.FragmentSpinBinding
import kotlin.math.sin
import kotlin.random.Random

class SpinFragment : Fragment() {
    private lateinit var binding: FragmentSpinBinding
    private lateinit var spinViewModel: SpinViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpinBinding.inflate(inflater, container, false)


        spinViewModel = ViewModelProvider(this)[SpinViewModel::class.java]



        spinViewModel.username.observe(viewLifecycleOwner) { username ->
            username?.let {
                binding.ShowName.text = it
            }
        }


        spinViewModel.coins.observe(viewLifecycleOwner) { coins ->
            binding.CoinTextView.text = coins.toString()
        }


        spinViewModel.spinChances.observe(viewLifecycleOwner) { chances ->
            binding.SpinChances.text = chances.toString()

        }



        spinViewModel.spinResult.observe(viewLifecycleOwner) { result ->
            showResult(result)
        }



        spinViewModel.rotation.observe(viewLifecycleOwner) { rotation ->
            binding.spinimg.rotation = rotation
        }



        spinViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }



        spinViewModel.fetchUsername()
        spinViewModel.fetchCoins()
        spinViewModel.fetchSpinChances()



        binding.spinbutton.setOnClickListener {
            binding.spinbutton.isEnabled = false
            if (spinViewModel.spinChances.value ?: 0 > 0) {
                spinViewModel.startSpin()
            } else {
                Toast.makeText(context, "No spin chances available", Toast.LENGTH_SHORT).show()
                binding.spinbutton.isEnabled = true
            }
        }

        return binding.root
    }

    private fun showResult(result: String) {
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
        binding.spinbutton.isEnabled = true
    }
}











