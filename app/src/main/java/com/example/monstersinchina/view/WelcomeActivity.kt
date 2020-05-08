package com.example.monstersinchina.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.monstersinchina.R
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        Handler().postDelayed(
            {
                startActivity(Intent(this@WelcomeActivity, HomeActivity::class.java))
                finish()
            },
            3000
        )
    }
}
