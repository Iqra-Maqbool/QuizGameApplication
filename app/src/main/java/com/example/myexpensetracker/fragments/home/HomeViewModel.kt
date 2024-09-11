package com.example.myexpensetracker.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myexpensetracker.models.UserModelClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeViewModel :ViewModel(){


    private val _username = MutableLiveData<String?>()
    val username: LiveData<String?> get() = _username

    private val _coins = MutableLiveData<Long>()
    val coins: LiveData<Long> get() = _coins

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchUsername() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            Firebase.firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                        val userModel = document.toObject(UserModelClass::class.java)
                        _username.value = userModel?.username
                }
                .addOnFailureListener {
                    _errorMessage.value = "Failed to fetch username"
                }
        }
    }


    fun fetchCoins() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            Firebase.database.reference.child("Coins").child(user.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                            _coins.value = snapshot.value as? Long ?: 0

                    }
                    override fun onCancelled(error: DatabaseError) {
                        _errorMessage.value = "Failed to fetch coins data"
                    }
                })
        }
    }
}
