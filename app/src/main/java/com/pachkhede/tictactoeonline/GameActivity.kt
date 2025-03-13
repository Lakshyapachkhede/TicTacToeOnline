package com.pachkhede.tictactoeonline

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameActivity : AppCompatActivity() {
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

        findViewById<ImageView>(R.id.reset).setOnClickListener{
            ticTacToeView.reset()
        }

        findViewById<ImageView>(R.id.back).setOnClickListener{
            onBackPressed()
        }





    }



}