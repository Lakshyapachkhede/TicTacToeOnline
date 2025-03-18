package com.pachkhede.tictactoeonline


import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import org.json.JSONObject

class GameOnlineActivity : AppCompatActivity() {

    private var player = "X"
    private var opponent = "O"
    private var turn = player
    private var roomId = ""
    private var interstitialAd: InterstitialAd? = null
    private var pWon = 0
    private var oWon = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_online)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        MobileAds.initialize(this)


        val ticTacToeOnlineView = findViewById<TicTacToeOnlineView>(R.id.ticTacToeView)

        roomId = intent.getStringExtra("id").toString()

        val data = JSONObject(intent.getStringExtra("data")!!)

        val player1Data = data.getJSONObject("player1")
        val player2Data = data.getJSONObject("player2")

        if (player1Data.getString("id") == SocketManager.getSocketId()) {
            player = "X"
            opponent = "O"
            setPlayerLeftInfo(
                player1Data.getString("name"),
                player1Data.getString("image"),
                player1Data.getString("mark")
            )
            setPlayerRightInfo(
                player2Data.getString("name"),
                player2Data.getString("image"),
                player2Data.getString("mark")
            )


        } else {
            player = "O"
            opponent = "X"
            setPlayerRightInfo(
                player1Data.getString("name"),
                player1Data.getString("image"),
                player1Data.getString("mark")
            )
            setPlayerLeftInfo(
                player2Data.getString("name"),
                player2Data.getString("image"),
                player2Data.getString("mark")
            )

        }

        ticTacToeOnlineView.board = IntArray(9) { ticTacToeOnlineView.Empty }
        ticTacToeOnlineView.mplayer = player
        setInfo("$turn's turn")


        ticTacToeOnlineView.cellTouched = { i ->

            if (turn == player) {
                ticTacToeOnlineView.setAt(i, player == "X")
                SocketManager.playMove(roomId, i, player)
            }

        }

        SocketManager.setMovePlayedListener { index, played_by ->
            ticTacToeOnlineView.setAt(index, played_by == "X")
            turn = if (played_by == "X") "O" else "X"
            ticTacToeOnlineView.turn = turn



            setInfo("$turn's turn")

        }

        SocketManager.setWinListener { player, start, end ->
            ticTacToeOnlineView.isWon = true
            ticTacToeOnlineView.winner = player
            ticTacToeOnlineView.startWin = start
            ticTacToeOnlineView.endWin = end

            ticTacToeOnlineView.invalidate()


            val isCurrPlayerWon = (player == this@GameOnlineActivity.player)
            if (isCurrPlayerWon){
                pWon++
                updateScore(true)
            } else {
                oWon++
                updateScore(false)
            }

            setInfo("$player won")


        }




        SocketManager.setResetListener {
            ticTacToeOnlineView.reset()
            turn = "X"
            setInfo("$turn's turn")

        }


        SocketManager.setGameDrawListener {
            ticTacToeOnlineView.isDraw = true
            setInfo("Game Draw")


        }


        SocketManager.setUserDisconnectListener { data ->
            ticTacToeOnlineView.isDraw = true
            val name = data.getString("name")
            setInfo("$name disconnected")


        }


        loadAd()

        findViewById<ImageView>(R.id.reset).setOnClickListener {
            SocketManager.reset(roomId)
        }

        findViewById<ImageView>(R.id.back).setOnClickListener {
            onBackPressed()
        }


    }

    fun setPlayerLeftInfo(name: String, img: String, mark: String) {
        findViewById<TextView>(R.id.playerName1).text = name
        findViewById<ImageView>(R.id.playerImage1).setImageResource(getImageIdFromName(img))
        findViewById<ImageView>(R.id.playerMark1).setImageResource(getImageIdFromName(mark))

    }

    fun setPlayerRightInfo(name: String, img: String, mark: String) {
        findViewById<TextView>(R.id.playerName2).text = name
        findViewById<ImageView>(R.id.playerImage2).setImageResource(getImageIdFromName(img))
        findViewById<ImageView>(R.id.playerMark2).setImageResource(getImageIdFromName(mark))

    }

    private fun getImageIdFromName(id: String): Int {
        return resources.getIdentifier(id, "drawable", packageName)
    }


    override fun onDestroy() {
        super.onDestroy()
        SocketManager.disconnect()
    }

    private fun setInfo(message: String) {
        runOnUiThread {
            findViewById<TextView>(R.id.gameInfoTextView).text = message
        }
    }


    override fun onBackPressed() {

        if (interstitialAd != null) {
            interstitialAd?.show(this)
            interstitialAd = null
            super.onBackPressed()

        } else {
            super.onBackPressed()
        }

        SocketManager.disconnect()
    }



    private fun updateScore(isPlayerWon : Boolean){
        runOnUiThread {
            if (isPlayerWon) {
                findViewById<TextView>(R.id.player1WonNumber).text = ": " + pWon.toString()
            } else {
                findViewById<TextView>(R.id.player2WonNumber).text = ": " + oWon.toString()

            }
        }
    }

    private fun loadAd(){
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this, getString(R.string.ad_inter_test), adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad

                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null


                }
            })
    }

}
