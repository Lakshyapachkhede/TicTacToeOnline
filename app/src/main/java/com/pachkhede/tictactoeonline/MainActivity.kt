package com.pachkhede.tictactoeonline

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView

private fun AdRequest.Builder.addTestDevice(string: String) {}

class MainActivity : AppCompatActivity() ,ProfileSelectDialog.InputListener {

    private var adView : AdView? = null

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


        findViewById<Button>(R.id.btnTwoPlayer).setOnClickListener {
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnVsComputer).setOnClickListener {
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            intent.putExtra("computer", true)
            startActivity(intent)
        }


        findViewById<Button>(R.id.btnOnline).setOnClickListener {
            val existingDialog = supportFragmentManager.findFragmentByTag("Create or join Room")
            if (existingDialog == null) {
                val dialog = RoomCreateJoinDialog()
                dialog.isCancelable = false
                dialog.show(supportFragmentManager, "Create or join Room")
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


}