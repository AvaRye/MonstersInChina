package com.example.monstersinchina.view

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.monstersinchina.R
import com.example.monstersinchina.service.ViewModel
import com.example.monstersinchina.service.bindNonNull
import com.example.monstersinchina.service.detailLiveData
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_toolbar.view.*
import org.jetbrains.anko.webView
import org.jetbrains.anko.withAlpha

class DetailActivity : AppCompatActivity() {
    private val viewModel = ViewModel(this)

    lateinit var webView: WebView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        window.statusBarColor = Color.BLACK.withAlpha(80)

        detailLiveData.bindNonNull(this) {
            tb_home.apply {
                tv_title.text = it.title
                tv_toolbar.text = it.date
            }

        }

    }

    override fun onBackPressed() {
        onDestroy()
        super.onBackPressed()
    }

}
