package com.example.myexpensetracker.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myexpensetracker.models.UserModelClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {
    private val _userData = MutableLiveData<UserModelClass?>()
    val userData: LiveData<UserModelClass?> get() = _userData

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchUserData() {
        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
            Firebase.firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                        val user = document.toObject(UserModelClass::class.java)
                        _userData.value = user
                }
                .addOnFailureListener {
                    _errorMessage.value = "Failed to fetch user data"
                }
        } ?: run {
            _errorMessage.value = "User is not logged in."
        }
    }
}
