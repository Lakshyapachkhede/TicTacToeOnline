package com.pachkhede.tictactoeonline


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class GameActivity : AppCompatActivity(), ProfileSelectDialog.InputListener {

    private var interstitialAd: InterstitialAd? = null
    private var backPressed = 0;
    private lateinit var sharedPref: SharedPreferences
    private var p1Won = 0
    private var p2Won = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MobileAds.initialize(this)


        sharedPref = getSharedPreferences(getString(R.string.shared_pref_game), MODE_PRIVATE)
        backPressed = sharedPref.getInt("back_pressed_game", 0)


        val againstComputer = intent.getBooleanExtra("computer", false)

        if (againstComputer) {
            setPlayerRightInfo("computer", "robot", "o")
        }


        val ticTacToeView = findViewById<TicTacToeView>(R.id.ticTacToeView)
        ticTacToeView.playAgainstComputer = againstComputer


        val gameInfoTextView = findViewById<TextView>(R.id.gameInfoTextView)


        ticTacToeView.onTurnChange = { turn ->
            if (turn == ticTacToeView.X) {
                gameInfoTextView.text = "X's Turn"
            } else {
                gameInfoTextView.text = "O's Turn"
            }
        }

        ticTacToeView.onGameWin = { win ->
            if (win == ticTacToeView.Draw) {
                gameInfoTextView.text = "It's a draw"
            } else if (win != ticTacToeView.Empty) {

                var winner = ""
                if (win == ticTacToeView.X) {
                    winner = "X"
                    p1Won++
                    updateScore(true)
                } else {
                    winner = "O"
                    p2Won++
                    updateScore(false)
                }

                gameInfoTextView.text = "$winner won"


            }

        }

        loadAd()

        findViewById<ImageView>(R.id.reset).setOnClickListener {
            ticTacToeView.reset()
        }

        findViewById<ImageView>(R.id.back).setOnClickListener {
            onBackPressed()
        }


        findViewById<LinearLayout>(R.id.playerInfo2).setOnClickListener {

            if (!againstComputer) {
                val dialog = ProfileSelectDialog(ProfileSelectDialog.defaultList)
                dialog.show(supportFragmentManager, "update profile")
            }
        }

        val sharedPref =
            getSharedPreferences(getString(R.string.shared_pref_main), Context.MODE_PRIVATE)
        val name = sharedPref.getString(getString(R.string.profileName), "Player1")
        val img = sharedPref.getString(getString(R.string.profileImg), "a")

        val imgId = resources.getIdentifier(img, "drawable", packageName)

        findViewById<ImageView>(R.id.playerImage1).setImageResource(imgId)
        findViewById<TextView>(R.id.playerName1).text = name




    }

    override fun sendInput(name: String, img: String) {
        findViewById<TextView>(R.id.playerName2).text = name
        val imgId = resources.getIdentifier(img, "drawable", packageName)
        findViewById<ImageView>(R.id.playerImage2).setImageResource(imgId)

    }


    fun setPlayerRightInfo(name: String, img: String, mark: String) {
        findViewById<TextView>(R.id.playerName2).text = name
        findViewById<ImageView>(R.id.playerImage2).setImageResource(getImageIdFromName(img))
        findViewById<ImageView>(R.id.playerMark2).setImageResource(getImageIdFromName(mark))

    }

    private fun getImageIdFromName(id: String): Int {
        return resources.getIdentifier(id, "drawable", packageName)
    }


    override fun onBackPressed() {

        if (interstitialAd != null && backPressed > 2) {
            interstitialAd?.show(this)
            interstitialAd = null
            super.onBackPressed()
            putBackPressed(0)
        } else {

            super.onBackPressed()
            putBackPressed(backPressed + 1)

        }


    }

    private fun putBackPressed(n: Int) {
        with(sharedPref.edit()) {
            putInt("back_pressed_game", n)
            commit()

        }
    }


    private fun updateScore(isPlayer1 : Boolean){
        if(isPlayer1){
            findViewById<TextView>(R.id.player1WonNumber).text = ": " + p1Won.toString()
        } else{
            findViewById<TextView>(R.id.player2WonNumber).text = ": " + p2Won.toString()

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