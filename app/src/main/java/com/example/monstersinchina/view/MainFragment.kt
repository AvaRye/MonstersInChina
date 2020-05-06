package com.example.monstersinchina.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.monstersinchina.R
import com.example.monstersinchina.commons.Item
import com.example.monstersinchina.commons.ItemAdapter
import com.example.monstersinchina.commons.ItemManager
import com.example.monstersinchina.service.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : Fragment() {

    private var page = 1
    private var finalPage = 1
    private val itemManager = ItemManager()
    private val viewModel = ViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        loadingLiveData.postValue(false)

        view.srl_frag_main.apply {
            setOnRefreshListener {
                setColorSchemeResources(R.color.colorPrimary)
                if (loadingLiveData.value != true) {
                    loadingLiveData.postValue(true)
                    resetPage()
                    itemManager.autoRefresh {
                        removeAll { it is HomeItem }
                        viewModel.getHome(this@MainFragment)
                    }
                }
            }
        }

        view.rv_frag_main.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = ItemAdapter(itemManager)
            itemManager.autoRefresh { removeAll { it is HomeItem } }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!canScrollVertically(1) && page < finalPage && loadingLiveData.value != true) {
                        loadingLiveData.postValue(true)
                        viewModel.getHome(++page)
                        Toast.makeText(
                            this@MainFragment.context,
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
                            Glide.with(this@MainFragment.context)
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
                        val intent = Intent(this@MainFragment.context, DetailActivity::class.java)
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
        return view
    }

    private fun resetPage() {
        page = 1
        finalPage = 1
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}

