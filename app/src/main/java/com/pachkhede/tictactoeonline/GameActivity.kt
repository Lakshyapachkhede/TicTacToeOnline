package com.pachkhede.tictactoeonline

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class GameActivity : AppCompatActivity(), ProfileSelectDialog.InputListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val ticTacToeView = findViewById<TicTacToeView>(R.id.ticTacToeView)

        val gameInfoTextView = findViewById<TextView>(R.id.gameInfoTextView)


        ticTacToeView.onTurnChange = {turn->
            if (turn == ticTacToeView.X){
                gameInfoTextView.text = "X's Turn"
            } else {
                gameInfoTextView.text = "O's Turn"
            }
        }

        ticTacToeView.onGameWin = {win ->
            if (win == ticTacToeView.Draw){
                gameInfoTextView.text = "It's a draw"
            } else if (win != ticTacToeView.Empty){
                val winner = if (win == ticTacToeView.X) "X" else "O"
                gameInfoTextView.text = "$winner won"

            }

        }


        findViewById<ImageView>(R.id.reset).setOnClickListener{
            ticTacToeView.reset()
        }

        findViewById<ImageView>(R.id.back).setOnClickListener{
            onBackPressed()
        }


        findViewById<LinearLayout>(R.id.playerInfo2).setOnClickListener {
            val dialog = ProfileSelectDialog(ProfileSelectDialog.defaultList)
            dialog.show(supportFragmentManager, "update profile")

        }

        val sharedPref = getSharedPreferences(getString(R.string.shared_pref_main), Context.MODE_PRIVATE)
        val name = sharedPref.getString(getString(R.string.profileName), "Player1")
        val img = sharedPref.getInt(getString(R.string.profileImg), R.drawable.a)

        findViewById<ImageView>(R.id.playerImage1).setImageResource(img)
        findViewById<TextView>(R.id.playerName1).text = name

    }

    override fun sendInput(name: String, img: Int) {
        findViewById<TextView>(R.id.playerName2).text = name
        findViewById<ImageView>(R.id.playerImage2).setImageResource(img)

    }


}