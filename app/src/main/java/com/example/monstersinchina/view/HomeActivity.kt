package com.example.monstersinchina.view

import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monstersinchina.R
import com.example.monstersinchina.commens.rec.Item
import com.example.monstersinchina.commens.rec.ItemAdapter
import com.example.monstersinchina.commens.rec.ItemManager
import com.example.monstersinchina.service.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.withAlpha

class HomeActivity : AppCompatActivity() {
    private var page = 1
    private var finalPage = 1
    private val itemManager = ItemManager()
    private val viewModel = ViewModel(this)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        window.statusBarColor = Color.BLACK.withAlpha(80)
        loadingLiveData.postValue(false)

        srl_menu.apply {
            setOnRefreshListener {
                setColorSchemeResources(R.color.colorPrimary)
                if (loadingLiveData.value != true) {
                    loadingLiveData.postValue(true)
                    resetPage()
                    itemManager.autoRefresh {
                        removeAll { it is HomeItem }
                        viewModel.getHome(this@HomeActivity)
                    }
                }
            }
        }

        rv_menu.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = ItemAdapter(itemManager)
            itemManager.autoRefresh { removeAll { it is HomeItem } }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!canScrollVertically(1) && page < finalPage && loadingLiveData.value != true) {
                        loadingLiveData.postValue(true)
                        viewModel.getHome(++page)
                        Toast.makeText(this@HomeActivity, "$page, $finalPage", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
        }

        viewModel.getHome(this)

        homeLiveData.bindNonNull(this) { homePage ->
            finalPage = homePage.finalPage
            val items = mutableListOf<Item>()
            homePage.homeList.forEach { home ->
                items.homeItem {
                    when (home.type) {
                        "withPic" -> {
                            this.imageCard.visibility = View.VISIBLE
                            Glide.with(this@HomeActivity)
                                .load(home.image)
                                .into(image)
                        }
                        "noPic" -> {
                            this.imageCard.visibility = View.GONE
                        }
                    }
                    name.text = home.name
                    description.text = home.description
                    card.setOnClickListener {
                        val intent = Intent(this@HomeActivity, DetailActivity::class.java)
                            .putExtra("url", home.url)
                        startActivity(intent)
                    }
                }
            }
            if (page == 1) {
                itemManager.autoRefresh {
                    removeAll { it is HomeItem }
                    addAll(items)
                }
            } else {
                itemManager.addAll(items)
            }
        }
    }

    private fun resetPage() {
        page = 1
        finalPage = 1
    }

}
