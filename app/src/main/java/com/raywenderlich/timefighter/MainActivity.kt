package com.raywenderlich.timefighter

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private lateinit var tapMeButton: Button
    private var score = 0
    private var gameStarted = false
    private lateinit var countDownTimer: CountDownTimer
    private var initialCountDown: Long = 60000
    private var countDownInterval: Long = 1_000
    private var timeLeft = 60
    private val TAG = MainActivity::class.java.simpleName

    companion object {
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    // connect views to variables
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate called. Score is: $score")
        gameScoreTextView = findViewById(R.id.game_score_text_view)
        timeLeftTextView = findViewById(R.id.time_left_text_view)
        tapMeButton = findViewById(R.id.tap_me_button)


        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY)
            restoreGame()
        } else {
            resetGame()
        }

        tapMeButton.setOnClickListener { v ->
            val bounceAnimation = AnimationUtils.loadAnimation(this,
                R.anim.bounce)
            v.startAnimation(bounceAnimation)
            incrementScore()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putInt(TIME_LEFT_KEY, timeLeft)
        countDownTimer.cancel()
        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & TimeLeft: $timeLeft")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called.")
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about_item) {
            showInfo()
        }
        return true
    }

    private fun showInfo() {
        val dialogTitle = getString(R.string.about_title,
            BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    // increment score logic
    private fun incrementScore() {
        if (!gameStarted) {
            startGame()
        }
        score++
        val newScore = getString(R.string.your_score, score)
        gameScoreTextView.text = newScore

    }


    // reset game logic
    private fun resetGame() {
        score = 0
        val initialScore = getString(R.string.your_score, score)
        gameScoreTextView.text = initialScore
        val initialTimeLeft = getString(R.string.time_left, 6)
        timeLeftTextView.text = initialTimeLeft
        countDownTimer = object : CountDownTimer(
            initialCountDown,
            countDownInterval
        ) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt() / 1000
                val timeLeftString = getString(
                    R.string.time_left,
                    timeLeft
                )
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false

    }

    private fun restoreGame() {
        val restoredScore = getString(R.string.your_score, score)
        gameScoreTextView.text = restoredScore
        val restoredTime = getString(R.string.time_left, timeLeft)
        timeLeftTextView.text = restoredTime
        countDownTimer = object : CountDownTimer(
            (timeLeft *
                    1000).toLong(), countDownInterval
        ) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt() / 1000
                val timeLeftString = getString(
                    R.string.time_left,
                    timeLeft
                )
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }
        countDownTimer.start()
        gameStarted = true
    }

    // start game logic
    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    // end game logic
    private fun endGame() {
        Toast.makeText(
            this, getString(
                R.string.game_over_message,
                score
            ), Toast.LENGTH_LONG
        ).show()
        resetGame()
    }
}
