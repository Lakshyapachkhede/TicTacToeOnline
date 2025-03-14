package com.pachkhede.tictactoeonline

import android.annotation.SuppressLint
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
            val list = List<Int>(10){(0)}.toMutableList()
            list[0] = R.drawable.a
            list[1] = R.drawable.b
            list[2] = R.drawable.c
            list[3] = R.drawable.d
            list[4] = R.drawable.e
            list[5] = R.drawable.f
            list[6] = R.drawable.g
            list[7]= R.drawable.h
            list[8] = R.drawable.i
            list[9] = R.drawable.j

            val dialog = ProfileSelectDialog(list)
            dialog.show(supportFragmentManager, "update profile")

        }





    }

    override fun sendInput(name: String, img: Int) {
        findViewById<TextView>(R.id.playerName2).text = name
        findViewById<ImageView>(R.id.playerImage2).setImageResource(img)

    }


}