package com.pachkhede.tictactoeonline

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject

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


            val data = JSONObject(intent.getStringExtra("data")!!)

            Toast.makeText(this@GameOnlineActivity, "hello", Toast.LENGTH_SHORT).show()

            val player1Data = data.getJSONObject("player1")
            val player2Data = data.getJSONObject("player2")

            if (player1Data.getString("id") == SocketManager.getSocketId()){
                player = "X"
                opponent = "O"
                ticTacToeOnlineView.player = ticTacToeOnlineView.X
                setPlayerLeftInfo(player1Data.getString("name"), player1Data.getString("image"), player1Data.getString("mark"))
                setPlayerRightInfo(player2Data.getString("name"), player2Data.getString("image"), player2Data.getString("mark"))


            } else {
                player = "O"
                opponent = "X"
                ticTacToeOnlineView.player = ticTacToeOnlineView.O
                setPlayerRightInfo(player1Data.getString("name"), player1Data.getString("image"), player1Data.getString("mark"))
                setPlayerLeftInfo(player2Data.getString("name"), player2Data.getString("image"), player2Data.getString("mark"))

            }

            ticTacToeOnlineView.turn = ticTacToeOnlineView.X
            ticTacToeOnlineView.board = IntArray(9) { ticTacToeOnlineView.Empty }








        ticTacToeOnlineView.cellTouched = { i ->

        }



        findViewById<ImageView>(R.id.reset).setOnClickListener{
            // pass
        }

        findViewById<ImageView>(R.id.back).setOnClickListener{
            onBackPressed()
        }






    }

    fun setPlayerLeftInfo(name : String, img : String, mark:String){
        findViewById<TextView>(R.id.playerName1).text = name
        findViewById<ImageView>(R.id.playerImage1).setImageResource(getImageIdFromName(img))
        findViewById<ImageView>(R.id.playerMark1).setImageResource(getImageIdFromName(mark))

    }

    fun setPlayerRightInfo(name : String, img : String, mark:String){
        findViewById<TextView>(R.id.playerName2).text = name
        findViewById<ImageView>(R.id.playerImage2).setImageResource(getImageIdFromName(img))
        findViewById<ImageView>(R.id.playerMark2).setImageResource(getImageIdFromName(mark))

    }

    private fun getImageIdFromName(id: String) : Int{
        return resources.getIdentifier(id, "drawable", packageName)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        SocketManager.disconnect()
    }
}
