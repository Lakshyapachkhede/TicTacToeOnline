package com.pachkhede.tictactoeonline

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.pachkhede.tictactoeonline.GameOnlineActivity

class MainActivity : AppCompatActivity() ,ProfileSelectDialog.InputListener {

    private var interstitialAd: InterstitialAd? = null
    private var adView : AdView? = null
    private var clickTimes = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adView = findViewById<AdView>(R.id.ad_banner_main)
        MobileAds.initialize(this);
        val adRequest = AdRequest.Builder()
            .build()

        adView?.loadAd(adRequest)


        loadAd()


        findViewById<Button>(R.id.btnTwoPlayer).setOnClickListener {
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            showAdBeforeNavigating(intent)
        }

        findViewById<Button>(R.id.btnVsComputer).setOnClickListener {
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            intent.putExtra("computer", true)
            showAdBeforeNavigating(intent)

        }


        findViewById<Button>(R.id.btnOnline).setOnClickListener {
            val existingDialog = supportFragmentManager.findFragmentByTag("Create or join Room")
            if (existingDialog == null) {

                val dialog = RoomCreateJoinDialog()
                dialog.isCancelable = false

                if(interstitialAd != null){

                    interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
                        override fun onAdDismissedFullScreenContent() {
                            dialog.show(supportFragmentManager, "Create or join Room")
                            loadAd()
                        }


                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            interstitialAd = null
                            dialog.show(supportFragmentManager, "Create or join Room")
                        }

                    }
                    interstitialAd?.show(this@MainActivity)
                } else {
                    dialog.show(supportFragmentManager, "Create or join Room")
                }
            }
        }

        findViewById<ImageView>(R.id.user).setOnClickListener {
            val dialog = ProfileSelectDialog(ProfileSelectDialog.defaultList)
            dialog.show(supportFragmentManager, "Update Profile")
        }





    }

    override fun sendInput(name: String, img: String) {
        val sharedPref = getSharedPreferences(getString(R.string.shared_pref_main),Context.MODE_PRIVATE)

        with(sharedPref.edit()) {
            putString(getString(R.string.profileName), name)
            putString(getString(R.string.profileImg), img)
            commit()
        }
    }

    private fun showAdBeforeNavigating(intent: Intent) {
        if (interstitialAd != null && clickTimes > 2) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {

                    startActivity(intent)
                    loadAd()
                    clickTimes = 0;
                }
            }
            interstitialAd?.show(this)
        } else {

            startActivity(intent)
            loadAd()
            clickTimes++;
        }
    }

    private fun loadAd(){
        val adRequest = AdRequest.Builder()
            .build()
        InterstitialAd.load(this, getString(R.string.ad_inter_test), adRequest, object : InterstitialAdLoadCallback(){
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                Toast.makeText(this@MainActivity, "loaded", Toast.LENGTH_SHORT).show()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
                Toast.makeText(this@MainActivity, "failed", Toast.LENGTH_SHORT).show()

            }
        })
    }
}