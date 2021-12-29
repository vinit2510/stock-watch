package com.example.stockwatchkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInAccount




class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            finish()

            val account = GoogleSignIn.getLastSignedInAccount(this)

            if (account == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            else {
                startActivity(Intent(this, MainActivity::class.java))
            }


        }, (2000..3000).random().toLong())
    }

    override fun onBackPressed() {

    }
}