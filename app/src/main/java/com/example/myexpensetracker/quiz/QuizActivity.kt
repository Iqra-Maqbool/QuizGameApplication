package com.example.myexpensetracker.quiz
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myexpensetracker.R
import com.example.myexpensetracker.databinding.ActivityQuizBinding
import com.example.myexpensetracker.models.QuestionModelClass
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

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var databaseReference: DatabaseReference
    private lateinit var questionsList: ArrayList<QuestionModelClass>
    private var currentOption = 0
    private var score = 0
    private var currentChances = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        auth = Firebase.auth
        firestore = Firebase.firestore
        databaseReference = Firebase.database.reference


        auth.currentUser?.let { user ->
            firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        document.toObject(UserModelClass::class.java)?.let { userModel ->
                            binding.ShowName.text = userModel.username
                        } ?: Toast.makeText(this@QuizActivity, "User data is null ", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@QuizActivity, "No such document exists", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this@QuizActivity, "Failed to fetch the data", Toast.LENGTH_SHORT).show()
                }



            // Fetch coins from Realtime Database
            databaseReference.child("Coins").child(user.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            binding.CoinTextView.text = (snapshot.value as? Long ?: 0).toString()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@QuizActivity, "Failed to fetch coin data", Toast.LENGTH_SHORT).show()
                    }
                })
        } ?: Toast.makeText(this@QuizActivity, "User is not logged in", Toast.LENGTH_SHORT).show()




            val image = intent.getIntExtra("QuizImg", 0)
            binding.QuizImg.setImageResource(image)
            val text = intent.getStringExtra("QuestionType")
            binding.QuestionType.text = text


            questionsList = ArrayList()
            fetchQuestionsFromFireStore(text)
            setupOptionClickListeners()
        }



    //Fetching Question from fire store
    private fun fetchQuestionsFromFireStore(subject: String?) {
        if (subject == null) return
        Firebase.firestore.collection("AllQuestions").document(subject)
            .collection("Question1").get()
            .addOnSuccessListener { questionData ->
                questionsList.clear()
                for (data in questionData.documents) {
                    val question = data.toObject(QuestionModelClass::class.java)
                    question?.let { questionsList.add(it) }
                }
                if (questionsList.isNotEmpty()) {
                    updateQuestionUI()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to fetch questions", Toast.LENGTH_SHORT).show()
            }
    }



    //Options
    private fun setupOptionClickListeners() {

        binding.apply{
            Option1.setOnClickListener {
                nextQuestion(Option1.text.toString())
            }
            Option2.setOnClickListener {
                nextQuestion(Option2.text.toString())
            }
            Option3.setOnClickListener {
                nextQuestion(Option3.text.toString())
            }
            Option4.setOnClickListener {
                nextQuestion(Option4.text.toString())
            }
        }

    }



    private fun nextQuestion(ans: String) {
        if (ans == questionsList[currentOption].Answer) {
            score += 1
        }
        currentOption++
        if (currentOption < questionsList.size) {
            updateQuestionUI()
        } else {
            handleQuizCompletion()
        }
    }



    private fun updateQuestionUI() {
        val question = questionsList[currentOption]
        binding.apply {
            Question.text = question.Question
            Option1.text = question.Option1
            Option2.text = question.Option2
            Option3.text = question.Option3
            Option4.text = question.Option4
        }

    }

    private fun handleQuizCompletion() {
        if (score >= 4) {
            updateSpinChances()
            binding.apply {
                quizlayout.visibility = View.GONE
                winner.visibility = View.VISIBLE
            }

            Toast.makeText(this, "You Won!", Toast.LENGTH_SHORT).show()
        } else {
            binding.apply {
                quizlayout.visibility = View.GONE
                loser.visibility = View.VISIBLE
            }

            Toast.makeText(this, "Sorry, you lost.", Toast.LENGTH_SHORT).show()
        }
    }




    private fun updateSpinChances() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            Firebase.database.reference.child("SpinChances").child(currentUser.uid)
                .get().addOnSuccessListener { snapshot ->
                    currentChances = snapshot.getValue(Long::class.java) ?: 0L
                    Firebase.database.reference.child("SpinChances").child(currentUser.uid)
                        .setValue(currentChances + 1)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Spin chance updated!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update spin chances", Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch spin chances", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
        }

    }
    }






