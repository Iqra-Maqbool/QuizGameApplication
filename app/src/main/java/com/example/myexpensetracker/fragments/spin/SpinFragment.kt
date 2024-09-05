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
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Random
import kotlin.math.sin

class SpinFragment : Fragment() {

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



        binding.spinbutton.setOnClickListener {
            binding.spinbutton.isEnabled = false
            if (currentChances > 0) {
                startSpin()
            } else {
                Toast.makeText(context, "Play Again to get more chances", Toast.LENGTH_SHORT).show()
                binding.spinbutton.isEnabled = true
            }
        }

        fetchUserData()

        return binding.root
    }


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



    private fun fetchUserData() {
        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
            val userId = currentUser.uid
            // Fetching username from Fire store
            Firebase.firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    document?.let {
                        if (it.exists()) {
                            it.toObject(UserModelClass::class.java)?.let { user ->
                                binding.ShowName.text = user.username
                            } ?: showToast("Failed to fetch user data")
                        } else {
                            showToast("No such document exists")
                        }
                    }
                }
                .addOnFailureListener {
                    showToast("Failed to fetch the data")
                }

            // Fetching spin chances and coins
            fetchSpinChances(userId)
            fetchCoins(userId)
        } ?: showToast("User is not logged in")
    }



    //fetching spin chances
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


//fetching coins
    private fun fetchCoins(userId: String) {
        Firebase.database.reference.child("Coins").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.value?.let { value ->
                        currentCoins = value as Long
                        binding.CoinTextView.text = currentCoins.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Failed to fetch coin data")
                }
            })
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

