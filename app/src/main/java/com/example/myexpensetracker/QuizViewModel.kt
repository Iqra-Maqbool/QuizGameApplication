package com.example.myexpensetracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myexpensetracker.models.QuestionModelClass
import com.example.myexpensetracker.models.UserModelClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
class QuizViewModel : ViewModel(){
    private val _spinChances = MutableLiveData<Long>()
    val spinChances: LiveData<Long> get() = _spinChances

    private val _username = MutableLiveData<String?>()
    val username: LiveData<String?> get() = _username

    private val _coins = MutableLiveData<Long>()
    val coins: LiveData<Long> get() = _coins

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage


    private val _questions = MutableLiveData<List<QuestionModelClass>>()
    val questions: LiveData<List<QuestionModelClass>> get() = _questions


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
        Firebase.auth.currentUser?.let { user ->
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



    fun fetchSpinChances() {
        Firebase.auth.currentUser?.let { user ->
            Firebase.database.reference.child("SpinChances").child(user.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _spinChances.value = snapshot.value as? Long ?: 0
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _errorMessage.value = "Failed to fetch spin chances"
                    }
                })
        }
    }



    fun fetchQuestions(subject: String?) {
        if (subject == null) return
        Firebase.firestore.collection("AllQuestions").document(subject)
            .collection("Question1").get()
            .addOnSuccessListener { questionData ->
                val questionsList = mutableListOf<QuestionModelClass>()
                for (data in questionData.documents) {
                    val question = data.toObject(QuestionModelClass::class.java)
                    question?.let { questionsList.add(it) }
                }
                _questions.value = questionsList
            }.addOnFailureListener {
                _errorMessage.value = "Failed to fetch questions"
            }
    }



    fun updateSpinChances() {
        Firebase.auth.currentUser?.let { user ->
            Firebase.database.reference.child("SpinChances").child(user.uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val currentChances = snapshot.value as? Long ?: 0L
                    Firebase.database.reference.child("SpinChances").child(user.uid)
                        .setValue(currentChances + 1)
                        .addOnSuccessListener {
                            _spinChances.value = currentChances + 1
                        }
                        .addOnFailureListener {
                            _errorMessage.value = "Failed to update spin chances"
                        }
                }.addOnFailureListener {
                    _errorMessage.value = "Failed to fetch spin chances"
                }
        }
    }
}

