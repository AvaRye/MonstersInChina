package com.example.monstersinchina.view

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.monstersinchina.R
import com.example.monstersinchina.commens.rec.Item
import com.example.monstersinchina.commens.rec.ItemAdapter
import com.example.monstersinchina.commens.rec.ItemManager
import com.example.monstersinchina.service.*
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.item_toolbar.view.*
import org.jetbrains.anko.*

class DetailActivity : AppCompatActivity() {
    private val viewModel = ViewModel(this)
    private val itemManager = ItemManager()

    lateinit var webView: WebView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        window.statusBarColor = Color.BLACK.withAlpha(80)

        rv_detail.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity)
            adapter = ItemAdapter(itemManager)
            itemManager.autoRefresh { removeAllViews() }
        }

        viewModel.getDetail(intent.getStringExtra("url").orEmpty())
        detailLiveData.bindNonNull(this) {
            tb_detail.apply {
                tv_title.text = it.title
                tv_toolbar.text = it.date
            }

            val items = mutableListOf<Item>()
            it.detail.forEach { detail ->
                when (detail.type) {
                    TYPE_STRONG -> items.detailStrongItem {
                        textView.text = detail.content
                    }

                    TYPE_TEXT -> items.detailTextItem {
                        textView.text = detail.content
                    }

                    TYPE_IMAGE -> items.detailImageItem {
                        Glide.with(this@DetailActivity)
                            .load(detail.content)
                            .into(imageView)
                    }
                }
            }
            itemManager.addAll(items)
        }
    }

    override fun onBackPressed() {
        detailLiveData.postValue(DetailPage("", "", listOf(Detail("text", ""))))
        super.onBackPressed()
    }
}
