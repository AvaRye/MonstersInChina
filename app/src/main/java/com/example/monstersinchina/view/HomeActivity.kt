package com.example.monstersinchina.view

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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

        /*
        val baseUrl = "http://www.cbaigui.com/"
        val client = OkHttpClient()
        GlobalScope.launch(Dispatchers.IO) {
            val string = client.newCall(
                Request.Builder()
                    .url(baseUrl)
                    .get()
                    .build()
            ).execute().body()?.string().orEmpty()

            Log.d("getHtml",string)
        }*/

        /*
        val list = mutableListOf<Home>()
        list.add(
            Home(
                "孽龙",
                "http://www.cbaigui.com/wp-content/uploads/2019/10/中国妖怪百集loading-320x320.jpg",
                "传说能兴水为害、作恶造孽的龙。 傩戏中邪&#46;&#46;&#46;",
                "2020/03/12",
                "sff",
                32
            )
        )

        list.add(
            Home(
                "玉兔",
                "http://www.cbaigui.com/wp-content/uploads/2019/07/ChanchuYutuWadang-320x320.jpg",
                "在中国神话中，月兔在月宫陪伴嫦娥并捣药。&#46;&#46;&#46;",
                "2020/03/12",
                "sff",
                32
            )
        )
        homeLiveData.postValue(list)
        */

        srl_menu.apply {
            setOnRefreshListener {
                if (loadingLiveData.value != true) {
                    loadingLiveData.postValue(true)
                    resetPage()
                    itemManager.autoRefresh {
                        removeAll { it is HomeItem }
                        viewModel.getHome()
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
                    }
                }
            })
        }

        viewModel.getHome()
        homeLiveData.bindNonNull(this) { list ->
            finalPage = list[0].finalPage
            val items = mutableListOf<Item>()
            list.forEach { home ->
                items.homeItem {
                    Glide.with(this@HomeActivity)
                        .load(home.image)
                        .into(image)
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
