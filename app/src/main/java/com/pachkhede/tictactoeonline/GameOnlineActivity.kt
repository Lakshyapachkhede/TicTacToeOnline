package com.pachkhede.tictactoeonline

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameOnlineActivity : AppCompatActivity() {

    private var player = "X"
    private var opponent = "O"
    private var turn = player


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_online)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ticTacToeOnlineView = findViewById<TicTacToeOnlineView>(R.id.ticTacToeView)


        ticTacToeOnlineView.cellTouched = { i ->

        }



        findViewById<ImageView>(R.id.reset).setOnClickListener{
            // pass
        }

        findViewById<ImageView>(R.id.back).setOnClickListener{
            onBackPressed()
        }






    }

    fun setPlayerOneInfo(name : String, img : Int){
        findViewById<TextView>(R.id.playerName1).text = name
        findViewById<ImageView>(R.id.playerImage1).setImageResource(img)
    }

    fun setPlayerTwoInfo(name : String, img : Int){
        findViewById<TextView>(R.id.playerName2).text = name
        findViewById<ImageView>(R.id.playerImage2).setImageResource(img)
    }



}
