package com.example.monstersinchina.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.monstersinchina.view.HomeActivity
import com.example.monstersinchina.view.MainFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.*

class ViewModel(/*private val context: Context*/) {
    fun getHome(activity: MainFragment) = GlobalScope.launch(Dispatchers.Main) {
        SpiderApi.getHomeAsync().awaitAndHandle {
            Log.d("getHome", it.toString())
            Toast.makeText(activity.context, it.message, Toast.LENGTH_SHORT).show()
        }?.let {
            homeLiveData.postValue(it)
        }
        loadingLiveData.postValue(false)
        activity.srl_frag_main.isRefreshing = false
    }

    fun getHome(page: Int) = GlobalScope.launch(Dispatchers.Main) {
        SpiderApi.getHomeAsync(page).awaitAndHandle {
            Log.d("getHome", it.toString())
//            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }?.let {
            homeLiveData.postValue(it)
        }
        loadingLiveData.postValue(false)
    }

    fun getDetail(url: String) = GlobalScope.launch(Dispatchers.Main) {
        SpiderApi.getDetailAsync(url).awaitAndHandle {
            Log.d("getDetail", it.toString())
//            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }?.let {
            detailLiveData.postValue(it)
        }
    }

    fun getBookList() = GlobalScope.launch(Dispatchers.Main) {
        SpiderApi.getBookAsync().awaitAndHandle {
            Log.d("getBookList", it.toString())
        }?.let {
            Log.d("books", it.toString())
            bookLiveData.postValue(it)
        }
    }


    private suspend fun <T> Deferred<T>.awaitAndHandle(handler: suspend (Throwable) -> Unit = {}): T? =
        try {
            await()
        } catch (t: Throwable) {
            handler(t)
            null
        }
}