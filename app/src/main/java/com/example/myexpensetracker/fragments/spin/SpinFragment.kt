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














/*
package com.example.myexpensetracker.fragments.spin

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myexpensetracker.databinding.FragmentSpinBinding
import com.example.myexpensetracker.models.UserModelClass
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
import java.util.Random
import kotlin.math.sin

class SpinFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: FragmentSpinBinding
    private lateinit var timer: CountDownTimer
    private val itemTitles = arrayOf("100", "Try Again", "1000", "Try Again", "500", "Try Again")
    private var currentChances = 0L
    private var currentCoins = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpinBinding.inflate(inflater, container, false)



        auth = Firebase.auth
        firestore = Firebase.firestore
        databaseReference = Firebase.database.reference



        //Fetching username
        auth.currentUser?.let { user ->
            firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        document.toObject(UserModelClass::class.java)?.let { userModel ->
                            binding.ShowName.text = userModel.username
                        } ?: Toast.makeText(context, "User data is null", Toast.LENGTH_SHORT).show()
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
                            currentCoins = snapshot.value as? Long ?: 0
                            binding.CoinTextView.text = currentCoins.toString()
                        } else {
                            binding.CoinTextView.text = "0"
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to fetch coin data", Toast.LENGTH_SHORT).show()
                    }
                })

            // Fetch spin chances
            fetchSpinChances(user.uid)
        } ?: Toast.makeText(context, "User is not logged in", Toast.LENGTH_SHORT).show()



        // Set up spin button click listener
        binding.spinbutton.setOnClickListener {
            binding.spinbutton.isEnabled = false
            if (currentChances > 0) {
                //Function to start the spin
                startSpin()
            } else {
                Toast.makeText(context, "Play Again to get more chances", Toast.LENGTH_SHORT).show()
                binding.spinbutton.isEnabled = true
            }
        }

        return binding.root
    }


    // Fetch spin chances
    private fun fetchSpinChances(userId: String) {
        Firebase.database.reference.child("SpinChances").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.value?.let { value ->
                        currentChances = value as Long
                        binding.SpinChances.text = currentChances.toString()
                    } ?: run {
                        binding.SpinChances.text = "0"
                        binding.spinbutton.isEnabled = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Failed to fetch spin chances")
                }
            })
    }


    // Start the spin animation and process result
    private fun startSpin() {
        val spin = Random().nextInt(6)
        val fullRotations = 8 * 360
        val degree = fullRotations + (spin * (360 / itemTitles.size))
        val slowDownRotation = 3 * 360

        timer = object : CountDownTimer(10000, 15) {
            var rotation = 0f
            val totalTicks = 10000 / 15
            var currentTick = 0

            override fun onTick(millisUntilFinished: Long) {
                currentTick++

                val t = currentTick / totalTicks.toFloat()
                val easedSpeed = (sin(t * Math.PI - Math.PI / 2) + 1) / 2 * 40

                if (rotation >= degree - slowDownRotation) {
                    val slowdownFactor = (degree - rotation) / slowDownRotation
                    rotation += (easedSpeed * slowdownFactor).toFloat()
                } else {
                    rotation += easedSpeed.toFloat()
                }

                if (rotation >= degree) {
                    rotation = degree.toFloat()
                    timer.cancel()
                    showResult(itemTitles[spin], spin)
                }

                binding.spinimg.rotation = rotation
            }

            override fun onFinish() {
                binding.spinimg.rotation = degree.toFloat()
                showResult(itemTitles[spin], spin)
            }
        }.start()
    }


    private fun showResult(itemTitle: String, spin: Int) {
        if (spin % 2 == 0) {
            val coinWin = itemTitle.toInt()
            Firebase.database.reference.child("Coins").child(Firebase.auth.currentUser!!.uid)
                .setValue(coinWin + currentCoins)
            binding.CoinTextView.text = (coinWin + currentCoins).toString()
        }

        if (currentChances > 0) {
            currentChances -= 1
            Firebase.auth.currentUser?.uid?.let { userId ->
                Firebase.database.reference.child("SpinChances").child(userId)
                    .setValue(currentChances)
                binding.SpinChances.text = currentChances.toString()
            }
        }

        showToast(itemTitle)
        binding.spinbutton.isEnabled = true
    }


    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
*/
