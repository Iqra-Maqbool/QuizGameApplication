package com.example.myexpensetracker
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myexpensetracker.databinding.ActivitySignupBinding
import com.example.myexpensetracker.models.UserModelClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Signup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        //To add new User
        binding.registerBtn.setOnClickListener {
            binding.apply {
                val signupUsername = signupUsernameEt.text.toString()
                val signupEmail = signupEmailEt.text.toString()
                val signupPassword = signupPasswordEt.text.toString()
                if (signupUsername.isNotEmpty() && signupEmail.isNotEmpty() && signupPassword.isNotEmpty()) {
                    if (Patterns.EMAIL_ADDRESS.matcher(signupEmail).matches()) {
                        signupUser(signupUsername, signupEmail, signupPassword)
                    } else {
                        Toast.makeText(this@Signup, "Invalid Email Format", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Signup, "Fill All Fields", Toast.LENGTH_SHORT).show()
                } }

        }


        binding.loginhere.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }


    //Function to add new User
    private fun signupUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser
                    if (user != null) {
                        val userModelClass = UserModelClass(username, email, password)
                        firestore.collection("users").document(user.uid).set(userModelClass)
                            .addOnCompleteListener { firestoreTask ->
                                if (firestoreTask.isSuccessful) {
                                    Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, Login::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
