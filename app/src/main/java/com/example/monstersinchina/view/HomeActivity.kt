package com.example.monstersinchina.view

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monstersinchina.R
import com.example.monstersinchina.commons.Item
import com.example.monstersinchina.commons.ItemAdapter
import com.example.monstersinchina.commons.ItemManager
import com.example.monstersinchina.service.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_toolbar.view.*
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

        tb_home.apply {
            tv_title.text = getText(R.string.app_name)
            tv_toolbar.text = getText(R.string.string_home)
            setOnTouchListener(OnDoubleClickListener {
                rv_home.smoothScrollToPosition(0)
                Toast.makeText(this@HomeActivity, "回到顶部👌", Toast.LENGTH_SHORT).show()
            })

        }

        loadingLiveData.postValue(false)

        srl_home.apply {
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

        rv_home.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = ItemAdapter(itemManager)
            itemManager.autoRefresh { removeAll { it is HomeItem } }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!canScrollVertically(1) && page < finalPage && loadingLiveData.value != true) {
                        loadingLiveData.postValue(true)
                        viewModel.getHome(++page)
                        Toast.makeText(
                            this@HomeActivity,
                            "第${page}页 共${finalPage}页",
                            Toast.LENGTH_SHORT
                        ).show()
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
                        TYPE_WITH_PIC -> {
                            this.imageCard.visibility = View.VISIBLE
                            Glide.with(this@HomeActivity)
                                .load(home.image)
                                .into(image)
                        }
                        TYPE_NO_PIC -> {
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
