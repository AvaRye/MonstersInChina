package com.example.monstersinchina.view

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.example.monstersinchina.R
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.webView
import org.jetbrains.anko.withAlpha

class DetailActivity : AppCompatActivity() {

    lateinit var webView: WebView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        window.statusBarColor = Color.BLACK.withAlpha(80)

        webView = webView {
            loadUrl(intent.getStringExtra("url"))
            webChromeClient = WebChromeClient()
        }
    }
}
