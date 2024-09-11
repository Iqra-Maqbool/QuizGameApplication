package com.example.myexpensetracker
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myexpensetracker.databinding.ActivityQuizBinding
import com.example.myexpensetracker.models.QuestionModelClass

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var quizViewModel: QuizViewModel

    private var currentQuestion = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        quizViewModel = ViewModelProvider(this)[QuizViewModel::class.java]



      quizViewModel.username.observe(this) { username ->
     username?.let {
            binding.ShowName.text = it
            }
        }


        quizViewModel.coins.observe(this) { coins ->
            binding.CoinTextView.text = coins.toString()
        }



        quizViewModel.questions.observe(this) { questionsList ->
            if (questionsList.isNotEmpty()) {
                updateQuestionUI(questionsList)
            }
        }



        quizViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }


        quizViewModel.fetchUsername()
        quizViewModel.fetchCoins()
        quizViewModel.fetchSpinChances()

        val text = intent.getStringExtra("QuestionType")
        quizViewModel.fetchQuestions(text)

        setupOptionClickListeners()
    }


    private fun updateQuestionUI(questionsList: List<QuestionModelClass>) {
        val question = questionsList[currentQuestion]
        binding.apply {
            Question.text = question.Question
            Option1.text = question.Option1
            Option2.text = question.Option2
            Option3.text = question.Option3
            Option4.text = question.Option4
        }
    }


    private fun setupOptionClickListeners() {
        binding.apply {
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
        val questionsList = quizViewModel.questions.value ?: return
        if (ans == questionsList[currentQuestion].Answer) {
            score += 1
        }
        currentQuestion++
        if (currentQuestion < questionsList.size) {
            updateQuestionUI(questionsList)
        } else {
            handleQuizCompletion()
        }
    }



    private fun handleQuizCompletion() {
        if (score >= 4) {
            quizViewModel.updateSpinChances()
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
}

















